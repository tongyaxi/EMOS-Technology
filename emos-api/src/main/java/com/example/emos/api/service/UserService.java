package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbUser;
import org.springframework.data.redis.hash.HashMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public interface UserService {

    public Integer login(HashMap param);

    public Set<String> searchUserPermissions(int userId);

    public HashMap searchUserSummary(int userId);

    public int updatePassword(HashMap param);

    public PageUtils searchUserByPage(HashMap param);

    public int insert(TbUser tbUser);

    public HashMap searchUserById(int userId);

    public int update(HashMap param);

    public int deleteUserByIds(Integer[] ids);

    public ArrayList<String> searchUserRoles(int userId);

    public HashMap searchNameAndDept(int userId);

    public HashMap createQrCode();

    public boolean checkQrCode(String code,String uuid);

    public HashMap wechatLogin(String uuid);

    public ArrayList<HashMap> searchAllUser();
}
