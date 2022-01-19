package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "会議室を会議室IDで検索する検証用フォーム")
public class SearchMeetingRoomByIdForm {

    @NotNull(message = "会議室IDはNULLまたは空文字列が不可")
    @Min(value = 1,message = "会議室IDは1以上が必要")
    @Schema(description = "会議室ID")
    private Integer id;
}
