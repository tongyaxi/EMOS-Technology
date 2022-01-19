package com.example.emos.api.controller.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "ユーザー名と部門名称を検索する検証用フォーム")
public class SearchNameAndDeptForm {

    @NotNull(message = "ユーザーIDはNULLまたは空文字列が不可")
    @Min(value = 1, message = "ユーザーIDは1以上が必要")
    @Schema(description = "ユーザーID")
    private Integer id;
}
