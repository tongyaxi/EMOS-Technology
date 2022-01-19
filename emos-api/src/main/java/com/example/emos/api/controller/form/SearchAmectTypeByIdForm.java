package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "罰金タイプをIDで検索する検証用フォーム")
public class SearchAmectTypeByIdForm {

    @Schema(description = "罰金ID")
    @NotNull(message = "罰金IDはNULLまたは空文字列が不可")
    @Min(value = 1, message = "罰金IDは1以上が必要")
    private Integer id;
}
