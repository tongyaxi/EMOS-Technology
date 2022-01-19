package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "罰金記録削除要フォーム")
public class DeleteAmectByIdsForm {

    @Schema(description = "罰金記録ID")
    @NotEmpty(message = "罰金記録IDはNULL又は空文字列が不可")
    private Integer[] ids;
}
