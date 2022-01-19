package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "部門をIDで検索する検証用フォーム")
public class SearchDeptByIdForm {

    @Schema(description = "部門ID")
    @NotNull(message = "部門IDはNULLまたは空文字列が不可")
    @Min(value = 1, message = "部門IDは1以上が必要")
    private Integer id;
}
