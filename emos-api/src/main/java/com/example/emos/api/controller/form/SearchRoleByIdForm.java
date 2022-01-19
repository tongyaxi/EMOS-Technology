package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "役割をIDで検索用フォーム")
public class SearchRoleByIdForm {

    @Schema(description = "役割ID")
    @NotNull(message = "IDはNULLが不可")
    @Min(value = 1, message = "ユーザーIDは1以上が必要")
    private Integer id;
}
