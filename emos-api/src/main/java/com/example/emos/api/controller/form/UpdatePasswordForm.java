package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Schema(description = "パスワード変更の検証用フォーム")
@Data
public class UpdatePasswordForm {

    @NotBlank(message = "パスワードはNULL又は空文字列が不可")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "パスワードを正しく入力してください。")
    @Schema(description = "パスワード")
    private String password;
}
