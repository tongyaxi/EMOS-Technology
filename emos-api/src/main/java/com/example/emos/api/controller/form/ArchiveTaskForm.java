package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "アーカイブ検証用フォーム")
public class ArchiveTaskForm {

    @Schema(description = "任務ID")
    @NotBlank(message = "任務IDはNULLまたは空文字列が不可")
    private String taskId;

    @Schema(description = "アーカイブファイルJSONArray")
    @NotBlank(message = "filesはNULLまたは空文字列が不可")
    private String files;

}
