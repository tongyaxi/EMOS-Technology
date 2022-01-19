package com.example.emos.api.service.impl;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.TbRoleDao;
import com.example.emos.api.db.pojo.TbRole;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private TbRoleDao tbRoleDao;

    @Override
    public ArrayList<HashMap> searchAllRole() {
        ArrayList<HashMap> allRoles = tbRoleDao.searchAllRole();
        return allRoles;
    }

    @Override
    public PageUtils searchRoleByPage(HashMap param) {

        int page = (Integer) param.get("page");
        int length = (Integer) param.get("length");
        int start = (page-1)*length;
        param.put("start",start);
        ArrayList<HashMap> roles = tbRoleDao.searchRoleByPage(param);
        long count = tbRoleDao.searchRoleCount(param);
        PageUtils pageUtils = new PageUtils(roles, count, page, length);
        return pageUtils;
    }

    @Override
    public int insert(TbRole tbRole) {
        int rows = tbRoleDao.insert(tbRole);
        return rows;
    }

    @Override
    public HashMap searchRoleById(int id) {
        HashMap role = tbRoleDao.searchRoleById(id);
        return role;
    }

    @Override
    public ArrayList<Integer> searchUserIdByRoleId(int roleId) {
        ArrayList<Integer> userIdList = tbRoleDao.searchUserIdByRoleId(roleId);
        return userIdList;
    }

    @Override
    public int update(TbRole tbRole) {
        int rows = tbRoleDao.update(tbRole);
        return rows;
    }

    @Override
    public int deleteRoleByIds(Integer[] ids) {
        if(!tbRoleDao.searchDeleteRoleInfo(ids)){
            throw new EmosException("該当の役割に繋がっているユーザーがあるので、削除できません。");
        }
        int rows = tbRoleDao.deleteRoleByIds(ids);
        return rows;
    }
}
