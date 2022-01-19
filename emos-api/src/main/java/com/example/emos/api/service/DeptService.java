package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbDept;

import java.util.ArrayList;
import java.util.HashMap;

public interface DeptService {

    public PageUtils searchDeptByPage(HashMap param);

    public int insert(TbDept tbDept);

    public int update(TbDept tbDept);

    public int deleteDeptByIds(Integer[] ids);

    public ArrayList<HashMap> searchAllDept();

    public HashMap searchById(int id);
}
