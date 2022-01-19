package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbRole;

import java.util.ArrayList;
import java.util.HashMap;

public interface RoleService {

    public ArrayList<HashMap> searchAllRole();

    public PageUtils searchRoleByPage(HashMap param);

    public int insert(TbRole tbRole);

    public HashMap searchRoleById(int id);

    public ArrayList<Integer> searchUserIdByRoleId(int roleId);
    public int update(TbRole tbRole);

    public int deleteRoleByIds(Integer[] ids);
}
