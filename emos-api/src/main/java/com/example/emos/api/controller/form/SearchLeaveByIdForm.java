package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "休暇記録をIDで検索する検証用フォーム")
public class SearchLeaveByIdForm {

    @NotNull(message = "休暇記録IDはNULLまたは空文字列が不可")
    @Min(value = 1, message = "休暇記録IDは1以上が必要")
    private Integer id;
}
