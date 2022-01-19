package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "役割削除要フォーム")
public class DeleteRoleByIdsForm {

    @Schema(description = "役割ID")
    @NotEmpty(message = "役割IDはNULL又は空文字列が不可")
    private Integer[] ids;
}
