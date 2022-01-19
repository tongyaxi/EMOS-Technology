package com.example.emos.api.db.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * tb_dept
 *
 * @author
 */
@Data
public class TbDept implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主キー
     */
    private Integer id;

    /**
     * 部門名称
     */
    private String deptName;

    private String tel;

    private String email;

    private String desc;
}