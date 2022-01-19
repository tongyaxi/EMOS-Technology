package com.example.emos.api.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.TbAmectDao;
import com.example.emos.api.db.pojo.TbAmect;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.service.AmectService;
import com.example.emos.api.wxpay.MyWXPayConfig;
import com.example.emos.api.wxpay.WXPay;
import com.example.emos.api.wxpay.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AmectServiceImpl implements AmectService {

    @Autowired
    private TbAmectDao tbAmectDao;

    @Autowired
    private MyWXPayConfig myWXPayConfig;

    @Override
    public PageUtils searchAmectByPage(HashMap param) {

        ArrayList<HashMap> list = tbAmectDao.searchAmectByPage(param);
        long count = tbAmectDao.searchAmectCount(param);
        int start = (Integer) param.get("start");
        int length = (Integer) param.get("length");

        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public int insert(ArrayList<TbAmect> list) {
        list.forEach(one->{
            tbAmectDao.insert(one);
        });
        return list.size();
    }

    @Override
    public HashMap searchAmectById(int id) {
        HashMap amect = tbAmectDao.searchAmectById(id);
        return amect;
    }

    @Override
    public int update(HashMap param) {
        int rows = tbAmectDao.update(param);
        return rows;
    }

    @Override
    public int deleteAmectByIds(Integer[] ids) {
        int rows = tbAmectDao.deleteAmectByIds(ids);
        return rows;
    }

    @Override
    public String createNativeAmectPayOrder(HashMap param) {
        int userId = (Integer) param.get("userId");
        int amectId = (Integer) param.get("amectId");
        HashMap amect = tbAmectDao.searchAmectByCondition(param);
        if(amect != null && amect.size() > 0){
            String amount = new BigDecimal(MapUtil.getStr(amect,"amount")).multiply(new BigDecimal("100")).intValue() + "";

            try{
                // 创建订单
                WXPay wxPay = new WXPay(myWXPayConfig);
                param.clear();
                // 随机字符串
                param.put("nonce_str", WXPayUtil.generateNonceStr());
                param.put("body", "缴纳罚款");
                param.put("out_trade_no", MapUtil.getStr(amect, "uuid"));
                param.put("total_fee", amount);
                param.put("spbill_create_ip", "127.0.0.1");
                param.put("notify_url", "http://s2.z100.vip:11176/emos-api/amect/recieveMessage");
                param.put("trade_type", "NATIVE");
                String sign = WXPayUtil.generateSignature(param, myWXPayConfig.getKey());
                param.put("sign", sign);
                // 创建支付订单
                Map<String, String> result = wxPay.unifiedOrder(param);
                // 微信订单ID
                String prepayId = result.get("prepay_id");
                // 支付连接，需要生成二维码让手机扫码
                String codeUrl = result.get("code_url");
                if (prepayId != null) {
                    param.clear();
                    param.put("prepayId", prepayId);
                    param.put("amectId", amectId);
                    int rows = tbAmectDao.updatePrepayId(param);
                    if (rows != 1) {
                        throw new EmosException("更新罚款单的支付订单ID失败");
                    }

                    //把支付订单的URL生成二维码
                    QrConfig qrConfig = new QrConfig();
                    qrConfig.setWidth(255);
                    qrConfig.setHeight(255);
                    qrConfig.setMargin(2);
                    String qrCodeBase64 = QrCodeUtil.generateAsBase64(codeUrl, qrConfig, "jpg");
                    return qrCodeBase64;
                } else {
                    log.error("创建支付订单失败", result);
                    throw new EmosException("创建支付订单失败");
                }
            }catch(Exception e){
                log.error("创建支付订单失败", e);
                throw new EmosException("创建支付订单失败");
            }
        }else{
            throw new EmosException("没有找到罚款单");
        }
    }

    @Override
    public int updateStatus(HashMap param) {
        int rows = tbAmectDao.updateStatus(param);
        return rows;
    }

    @Override
    public int searchUserIdByUUID(String uuid) {
        int userId = tbAmectDao.searchUserIdByUUID(uuid);
        return userId;
    }

    @Override
    public void searchNativeAmectPayResult(HashMap param) {
        HashMap map =  tbAmectDao.searchAmectByCondition(param);
        if(MapUtil.isNotEmpty(map)){
            String uuid = MapUtil.getStr(map, "uuid");
            param.clear();

            param.put("appid", myWXPayConfig.getAppID());
            param.put("mch_id", myWXPayConfig.getMchID());
            param.put("out_trade_no", uuid);
            param.put("nonce_str", WXPayUtil.generateNonceStr());

            try{
                String sign = WXPayUtil.generateSignature(param, myWXPayConfig.getKey());
                param.put("sign",sign);

                WXPay wxPay = new WXPay(myWXPayConfig);
                Map<String,String> result = wxPay.orderQuery(param);

                // 判断通信/业务状态码是否成功
                String returnCode = result.get("return_code");
                String resultCode = result.get("result_code");
                if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {

                    // 查询交易状态码是否成功
                    String tradeState = result.get("trade_state");
                    if ("SUCCESS".equals(tradeState)) {
                        //更新订单状态
                        tbAmectDao.updateStatus(new HashMap() {{
                            put("uuid", uuid);
                            put("status", 2);
                        }});
                    }
                }
            }catch(Exception e){
                log.error("执行异常",e);
                throw new EmosException("执行异常");
            }

        }
    }
}
