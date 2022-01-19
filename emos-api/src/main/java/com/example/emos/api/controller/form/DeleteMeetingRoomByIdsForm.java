package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "会議室削除要フォーム")
@Data
public class DeleteMeetingRoomByIdsForm {

    @Schema(description = "会議室ID")
    @NotEmpty(message = "会議室IDはNULL又は空文字列が不可")
    private Integer[] ids;
}
