package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "部門削除要フォーム")
public class DeleteDeptByIdsForm {

    @Schema(description = "部門ID")
    @NotEmpty(message = "部門IDはNULL又は空文字列が不可")
    private Integer[] ids;


}
