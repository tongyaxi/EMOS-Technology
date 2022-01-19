package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "休暇申請を削除する用フォーム")
public class DeleteLeaveByIdForm {

    @Schema(description = "休暇申請ID")
    @NotNull(message = "休暇申請IDはNULL又は空文字列が不可")
    @Min(value = 1, message = "休暇申請IDは1以上が必要")
    private Integer id;
}
