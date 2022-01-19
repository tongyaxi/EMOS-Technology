package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "会議審査検証用フォーム")
public class ApprovalTaskForm {

    @Schema(description = "任務ID")
    @NotBlank(message = "任務IDはNULLまたは空文字列が不可")
    private String taskId;

    @Schema(description = "審査意見")
    @NotBlank(message = "審査意見はNULLまたは空文字列が不可")
    @Pattern(regexp = "^同意$|^不同意$", message = "審査意見を正しく入力してください。")
    private String approval;
}
