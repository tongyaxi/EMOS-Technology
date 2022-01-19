package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 添加@Mapper注解后，这个dao接口在编译时会产生相应的实现类，
 * 注意：这个接口不可以定义同名的方法，因为回生成的相同的id，也就是说这个接口不可以重载
 */
@Mapper
public interface TbUserDao {

    // 登録
    public Integer login(HashMap param);

    // 権限検索
    public Set<String> searchUserPermissions(int userId);

    // アイコン/ネーム検索
    public HashMap searchUserSummary(int userId);

    // パスワード変更
    public int updatePassword(HashMap param);

    // ページ検索/条件制御検索
    public ArrayList<HashMap> searchUserByPage(HashMap param);
    public long searchUserCount(HashMap param);

    // ユーザー新規
    public int insert(TbUser tbUser);

    // IDによるユーザーを検索する
    public HashMap searchUserById(int userId);

    // ユーザーを変更する
    public int update(HashMap param);

    // ユーザーを削除する
    public int deleteUserByIds(Integer[] ids);

    // 工作流项目于线程任务类使用
    // 申请人信息
    public HashMap searchUserInfo(int userId);
    // 部门经理信息
    public Integer searchDeptManagerId(int id);
    // 总经理信息
    public Integer searchGmId();

    public Integer searchIdByOpenId(String openId);

    public ArrayList<HashMap> searchAllUser();

    public ArrayList<String> searchUserRoles(int userId);

    public HashMap searchNameAndDept(int userId);

}