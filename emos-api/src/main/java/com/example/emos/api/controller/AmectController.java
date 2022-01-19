package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbAmect;
import com.example.emos.api.service.AmectService;
import com.example.emos.api.websocket.WebSocketService;
import com.example.emos.api.wxpay.WXPayUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/amect")
@Tag(name="AmectController",description = "罰金制度WebAPI")
@Slf4j
public class AmectController {

    @Autowired
    private AmectService amectService;

    @Value("${wechatpay.key}")
    private String key;

    @PostMapping("/searchAmectByPage")
    @Operation(summary = "罰金情報をページデータで検索")
    @SaCheckLogin
    public R searchAmectByPage(@Valid @RequestBody SearchAmectByPageForm form){

        if ((form.getStartDate() != null && form.getEndDate() == null)
                || (form.getStartDate() == null && form.getEndDate() != null)) {
            return R.error("開始時間と終了時間は同時に空文字列、または非空文字列になる必要です。");
        }

        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;

        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("start", start);
        param.put("currentUserId", StpUtil.getLoginIdAsInt());

        // 下記の権限を持ってないと、自分のみの罰金記録を検索する
        if (!(StpUtil.hasPermission("AMECT:SELECT") || StpUtil.hasPermission("ROOT"))) {
            param.put("userId", StpUtil.getLoginIdAsInt());
        }

        PageUtils pageUtils = amectService.searchAmectByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @Operation(summary = "罰金記録を新規する")
    @PostMapping("/insert")
    @SaCheckPermission(value = {"AMECT:INSERT","ROOT"}, mode = SaMode.OR)
    public R insert(@Valid @RequestBody InsertAmectForm form){
        ArrayList<TbAmect> list = new ArrayList<>();
        for(Integer one : form.getUserId()){
           TbAmect tbAmect = new TbAmect();
           tbAmect.setUuid(IdUtil.simpleUUID());
           tbAmect.setAmount(new BigDecimal(form.getAmount()));
           tbAmect.setReason(form.getReason());
           tbAmect.setTypeId(form.getTypeId());
           tbAmect.setUserId(one);

            list.add(tbAmect);
        }

        int rows = amectService.insert(list);
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "罰金記録をIDで検索する")
    @PostMapping("/searchById")
    @SaCheckPermission(value = {"AMECT:SELECT","ROOT"}, mode = SaMode.OR)
    public R searchById(@Valid @RequestBody SearchAmectByIdForm form){
        HashMap amect = amectService.searchAmectById(form.getId());
        return R.ok(amect);
    }

    @Operation(summary = "罰金記録を更新する")
    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "AMECT:UPDATE"}, mode = SaMode.OR)
    public R update(@Valid @RequestBody UpdateAmectForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        int rows = amectService.update(param);
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "罰金記録を削除する")
    @PostMapping("/deleteAmectByIds")
    @SaCheckPermission(value = {"ROOT", "AMECT:DELETE"}, mode = SaMode.OR)
    public R deleteAmectByIds(@Valid @RequestBody DeleteAmectByIdsForm form) {
        int rows = amectService.deleteAmectByIds(form.getIds());
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "Nativeの支払罰金オーダーを作成する")
    @PostMapping("/createNativeAmectPayOrder")
    @SaCheckLogin
    public R createNativeAmectPayOrder(@Valid @RequestBody CreateNativeAmectPayOrderForm form){

        int userId = StpUtil.getLoginIdAsInt();
        int amectId = form.getAmectId();

        HashMap param = new HashMap(){{
           put("userId",userId);
           put("amectId",amectId);
        }};
        String qrCodeBase64 = amectService.createNativeAmectPayOrder(param);
        return R.ok().put("qrCodeBase64",qrCodeBase64);
    }

    @Operation(summary = "WechatPayから支払結果を受信する")
    @RequestMapping("/recieveMessage")
    public void recieveMessage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        request.setCharacterEncoding("utf-8");

        Reader reader = request.getReader();
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        StringBuffer sb = new StringBuffer();
        while(line != null){
            sb.append(line);
            line = br.readLine();
        }
        br.close();
        reader.close();

        String xml = sb.toString();
        // 判断只能接收来自微信支付平台的请求,验证数字签名
        if(WXPayUtil.isSignatureValid(xml,key)){
            // xml➡Mapに変換する
            Map<String,String> map = WXPayUtil.xmlToMap(xml);
            String resultCode = map.get("result_code");
            String returnCode = map.get("return_code");
            // 判断是否扣款成功
            if ("SUCCESS".equals(resultCode) && "SUCCESS".equals(returnCode)) {
                // 罚款单UUID
                String outTradeNo = map.get("out_trade_no");
                // 更新订单状态
                HashMap param = new HashMap() {{
                    put("status", 2);
                    put("uuid", outTradeNo);
                }};
                int rows = amectService.updateStatus(param);
                if (rows == 1) {

                    // 向前端页面推送付款结果
                    // 根据罚款单ID查询用户ID
                    int userId = amectService.searchUserIdByUUID(outTradeNo);
                    WebSocketService.sendInfo("決済完了", userId + "");

                    // 给微信平台返回响应
                    response.setCharacterEncoding("utf-8");
                    response.setContentType("application/xml");
                    Writer writer = response.getWriter();
                    BufferedWriter bufferedWriter = new BufferedWriter(writer);
                    bufferedWriter.write("<xml><return_code><![CDATA[SUCCESS]]></return_code> <return_msg><![CDATA[OK]]></return_msg></xml>");

                    bufferedWriter.close();
                    writer.close();
                } else {
                    log.error("更新订单状态失败");
                    response.sendError(500, "更新订单状态失败");
                }
            }
        }else {
            log.error("数字签名异常");
            response.sendError(500, "数字签名异常");
        }
    }

    @Operation(summary = "Navtiveの支払結果を検索する")
    @PostMapping("/searchNativeAmectPayResult")
    @SaCheckLogin
    public R searchNativeAmectPayResult(@Valid @RequestBody SearchNativeAmectPayResultForm form){
        int userId = StpUtil.getLoginIdAsInt();
        int amectId = form.getAmectId();

        HashMap param = new HashMap(){{
            put("userId", userId);
            put("amectId", amectId);
            put("status", 1);
        }};
        amectService.searchNativeAmectPayResult(param);
        return R.ok();
    }
}
