package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "会議申請削除要フォーム")
public class DeleteMeetingApplicationForm {

    @Schema(description = "会議ID")
    @NotNull(message = "会議IDはNULLまたは空文字列が不可")
    @Min(value = 1)
    private Long id;

    @Schema(description = "UUID")
    @NotBlank(message = "UUIDはNULLまたは空文字列が不可")
    private String uuid;

    @Schema(description = "InstanceId")
    @NotBlank(message = "InstanceIdはNULLまたは空文字列が不可")
    private String instanceId;

    @Schema(description = "原因")
    @NotBlank(message = "原因不能为空")
    private String reason;
}
