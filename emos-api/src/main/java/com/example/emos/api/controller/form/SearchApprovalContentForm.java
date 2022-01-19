package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "会議詳細検証用フォーム")
public class SearchApprovalContentForm {

    @Schema(description = "WorkFlow InstanceId")
    @NotBlank(message = "InstanceIdはNULL又は空文字列が不可")
    @Pattern(regexp = "^[0-9a-zA-Z\\-]{36}$", message = "WorkFlow InstanceIdを正しく入力してください。")
    private String instanceId;

    @Schema(description = "会議タイプ")
    @NotBlank(message = "会議タイプはNULL又は空文字列が不可")
    @Pattern(regexp = "^员工请假$|^会议申请$|^报销申请$", message = "会議タイプを正しく入力してください。")
    private String type;

    @Schema(description = "会議ステータス")
    @NotBlank(message = "会議ステータスはNULL又は空文字列が不可")
    @Pattern(regexp = "^待审批$|^已审批$|^已结束$", message = "会議ステータス")
    private String status;
}
