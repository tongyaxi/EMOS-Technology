package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "オンライン会議の参加者検証用フォーム")
public class SearchOnlineMeetingMembersForm {

    @NotNull(message = "MeetingIdはNULLまたは空文字列が不可")
    @Min(value = 1, message = "MeetingIdは1以上が必要")
    @Schema(description = "会議ID")
    private Integer meetingId;
}
