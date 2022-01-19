package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Schema(description = "役割の更新検証用フォーム")
@Data
public class UpdateRoleForm {

    @Schema(description = "役割ID")
    @NotNull(message = "IDはNULLが不可")
    @Min(value = 1, message = "ユーザーIDは1以上が必要")
    private Integer id;

    @Schema(description = "役割名称")
    @NotBlank(message = "役割名称はNULLまたは空文字列が不可")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,10}$", message = "役割名称を正しく入力してください。")
    private String roleName;

    @Schema(description = "備考")
    @Length(max = 20, message = "備考は20以上の文字列が不可")
    private String desc;

    @Schema(description = "権限")
    @NotEmpty(message = "権限はNULLまたは空文字列が不可")
    private Integer[] permissions;

    @Schema(description = "権限は変更されるか")
    @NotNull(message = "changedはNULLまたは空文字列が不可")
    private Boolean changed;


}
