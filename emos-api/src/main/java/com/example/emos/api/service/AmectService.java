package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbAmect;

import java.util.ArrayList;
import java.util.HashMap;

public interface AmectService {

    public PageUtils searchAmectByPage(HashMap param);

    public int insert(ArrayList<TbAmect> list);

    public HashMap searchAmectById(int id);
    public int update(HashMap param);

    public int deleteAmectByIds(Integer[] ids);

    public String createNativeAmectPayOrder(HashMap param);

    public int updateStatus(HashMap param);

    public int searchUserIdByUUID(String uuid);

    public void searchNativeAmectPayResult(HashMap param);
}
