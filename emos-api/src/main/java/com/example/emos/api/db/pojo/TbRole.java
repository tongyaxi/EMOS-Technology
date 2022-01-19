package com.example.emos.api.db.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * tb_role
 * @author
 */
@Data
public class TbRole implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主キー
     */
    private Integer id;

    /**
     * 役割名称
     */
    private String roleName;

    /**
     * 権限
     */
    private String permissions;

    /**
     * 備考
     */
    private String desc;

    /**
     * ディフォルト権限
     */
    private String defaultPermissions;

    /**
     * ディフォルト役割
     */
    private Boolean systemic;
}