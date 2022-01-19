package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Schema(description = "ユーザー更新検証用フォーム")
public class UpdateUserForm {

    @Schema(description = "ユーザーID")
    @NotNull(message = "ユーザーIDはNULL又は空文字列が不可")
    @Min(value = 1, message = "ユーザーIDは1以上が必要")
    private Integer userId;

    @Schema(description = "ユーザー名")
    @NotBlank(message = "ユーザー名はNULL又は空文字列が不可")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,20}$", message = "ユーザー名を正しく入力してください。")
    private String username;

    @Schema(description = "パスワード")
    @NotBlank(message = "パスワードはNULL又は空文字列が不可")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "パスワードを正しく入力してください。")
    private String password;

    @Schema(description = "氏名")
    @NotBlank(message = "氏名はNULL又は空文字列が不可")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,10}$", message = "氏名を正しく入力してください。")
    private String name;

    @Schema(description = "性別")
    @NotBlank(message = "性別はNULL又は空文字列が不可")
    @Pattern(regexp = "^男$|^女$", message = "性別を正しく入力してください。")
    private String sex;

    @Schema(description = "電話番号")
    @NotBlank(message = "電話番号はNULL又は空文字列が不可")
    @Pattern(regexp = "^1\\d{10}$", message = "電話番号を正しく入力してください。")
    private String tel;

    @Schema(description = "郵便番号")
    @NotBlank(message = "郵便番号はNULL又は空文字列が不可")
    @Email(message = "郵便番号を正しく入力してください。")
    private String email;

    @Schema(description = "入社日")
    @NotBlank(message = "入社日はNULL又は空文字列が不可")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$", message = "入社日を正しく入力してください。")
    private String hiredate;

    @Schema(description = "役割")
    @NotEmpty(message = "役割はNULL又は空文字列が不可")
    private Integer[] role;

    @Schema(description = "部門")
    @NotNull(message = "部門IDはNULL又は空文字列が不可")
    @Min(value = 1, message = "部門IDは1以上が必須")
    private Integer deptId;

}
