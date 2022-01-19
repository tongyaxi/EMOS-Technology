package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "ユーザー情報をIDで検索する検証用フォーム")
public class SearchUserByIdForm {

    @Schema(description = "ユーザーID")
    @NotNull(message = "ユーザーIDはNULLが不可")
    @Min(value = 1, message = "ユーザーIDは1以上が必要")
    private Integer userId;
}
