package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "立替金をIDで検索用フォーム")
public class SearchReimByIdForm {

    @Schema(description = "立替金ID")
    @NotNull(message = "立替金IDはNULLが不可")
    @Min(value = 1, message = "立替金IDは1以上が必要")
    private Integer id;
}

