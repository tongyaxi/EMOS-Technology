package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "役割新規用フォーム")
public class InsertRoleForm {

    @Schema(description = "役割名称")
    @NotBlank(message = "役割名称はNULLまたは空文字列が不可")
    private String roleName;

    @Schema(description = "備考")
    @Length(max = 20, message = "備考は20以上の文字列が不可")
    private String desc;

    @Schema(description = "権限")
    @NotEmpty(message = "権限はNULLまたは空文字列が不可")
    private Integer[] permissions;
}
