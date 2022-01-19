package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "立替金精算削除要フォーム")
public class DeleteReimByIdForm {

    @Schema(description = "立替金精算ID")
    @NotNull(message = "立替金精算IDはNULL又は空文字列が不可")
    @Min(value = 1, message = "立替金精算IDは1以上が必要")
    private Integer id;
}

