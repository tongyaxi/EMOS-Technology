package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Schema(description = "立替金新規用フォーム")
public class InsertReimForm {

    @Schema(description = "立替金項目")
    @NotBlank(message = "立替金項目はNULLまたは空文字列が不可")
    private String content;

    @Schema(description = "立替金金額")
    @NotNull(message = "立替金金額はNULLまたは空文字列が不可")
    private BigDecimal amount;

    @Schema(description = "借金金額")
    @NotNull(message = "借金金額はNULLまたは空文字列が不可")
    private BigDecimal anleihen;

    @Schema(description = "残高")
    @NotNull(message = "残高はNULLまたは空文字列が不可")
    private BigDecimal balance;

    @Schema(description = "タイプID")
    @NotNull(message = "タイプIDはNULLまたは空文字列が不可")
    @Min(value = 1, message = "タイプIDは1以上が必要")
    private Byte typeId;

}

