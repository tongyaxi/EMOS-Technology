package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Schema(description = "部門の更新検証用フォーム")
@Data
public class UpdateDeptForm {

    @Schema(description = "部門ID")
    @NotNull(message = "部門IDはNULLまたは空文字列が不可")
    private Integer id;

    @Schema(description = "部門名称")
    @NotBlank(message = "部門名称はNULLまたは空文字列が不可")
    private String deptName;

    @Schema(description = "携帯電話")
    @Pattern(regexp = "^1\\d{10}$|^(0\\d{2,3}\\-){0,1}[1-9]\\d{6,7}$",message = "携帯電話を正しく入力してください。")
    private String tel;

    @Schema(description = "メールアドレス")
    @Email(message = "メールアドレスを正しく入力してください。")
    private String email;

    @Schema(description = "備考")
    @Length(max = 20,message = "備考は20以上の文字列が不可")
    private String desc;
}
