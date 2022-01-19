package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbRoleDao {

    public ArrayList<HashMap> searchAllRole();

    public ArrayList<HashMap> searchRoleByPage(HashMap param);
    public long searchRoleCount(HashMap param);

    public int insert(TbRole tbRole);

    public HashMap searchRoleById(int id);

    public ArrayList<Integer> searchUserIdByRoleId(int roleId);
    public int update(TbRole tbRole);

    public Boolean searchDeleteRoleInfo(Integer[] ids);
    public int deleteRoleByIds(Integer[] ids);
}
