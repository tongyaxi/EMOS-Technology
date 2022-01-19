package com.example.emos.api.db.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * tb_user
 * @author 
 */
@Data
public class TbUser implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主キー
     */
    private Integer id;

    /**
     * ユーザー名
     */
    private String username;


    /**
     * パスワード
     */
    private String password;

    /**
     * 长期授权字符串
     */
    private String openId;

    /**
     * ニックネーム
     */
    private String nickname;

    /**
     * アイコンパス
     */
    private String photo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private Object sex;

    /**
     * 電話番号
     */
    private String tel;

    /**
     * メールアドレス
     */
    private String email;

    /**
     * 入社日
     */
    private Date hiredate;

    /**
     * 役割jsonarray
     */
    private Object role;

    /**
     * システム管理者
     */
    private Boolean root;

    /**
     * 部門番号
     */
    private Integer deptId;

    /**
     * ステータス(1:在職2:退職)
     */
    private Byte status;

    /**
     * 作成日時
     */
    private Date createTime;
}