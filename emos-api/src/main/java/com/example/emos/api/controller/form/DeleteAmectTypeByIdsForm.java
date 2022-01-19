package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "罰金タイプの削除用フォーム")
public class DeleteAmectTypeByIdsForm {

    @Schema(description = "罰金タイプID")
    @NotEmpty(message = "罰金タイプIDはNULL又は空文字列が不可")
    private Integer[] ids;
}
