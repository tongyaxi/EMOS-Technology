package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "会議情報を検索する検証用フォーム")
public class SearchMeetingInfoForm {

    @Schema(description = "会議ID")
    @NotNull(message = "会議IDはNULLまたは空文字列が不可")
    @Min(value = 1,message = "会議IDは1以上が必須")
    private Long id;

    @Schema(description = "ステータス")
    @NotNull(message = "ステータスはNULLまたは空文字列が不可")
    @Range(min = 1, max = 5,message = "ステータスは1~5の間が必須")
    private Short status;
}
