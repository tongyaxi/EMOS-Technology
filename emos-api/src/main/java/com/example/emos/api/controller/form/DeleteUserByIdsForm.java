package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "ユーザー削除要フォーム")
@Data
public class DeleteUserByIdsForm {

    @Schema(description = "ユーザーID")
    @NotEmpty(message = "ユーザーIDはNULL又は空文字列が不可")
    private Integer[] ids;
}
