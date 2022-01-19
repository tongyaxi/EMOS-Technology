package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @NotBlank： 检查约束(只针对字符串)是不是NULL，并且Trim后的长度是否大于0
 * @Schema： 使用在SpringDoc上，使其接收数据
 */
@Data
@Schema(description = "登録機能用フォーム")
public class LoginForm {

    @NotBlank(message = "ユーザー名はNULLまたは空文字列が不可")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,20}$", message = "ユーザー名を正しく入力してください。")
    @Schema(description = "ユーザー名")
    private String username;

    @NotBlank(message = "パスワードはNULLまたは空文字列が不可")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "パスワードを正しく入力してください。")
    @Schema(description = "パスワード")
    private String password;
}
