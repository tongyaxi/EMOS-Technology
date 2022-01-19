package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "罰金タイプの更新検証用フォーム")
public class UpdateAmectTypeByIdForm {

    @Schema(description = "罰金タイプID")
    @NotNull(message = "罰金タイプIDはNULLまたは空文字列が不可")
    @Min(value = 1, message = "罰金タイプIDは1以上が必要")
    private Integer id;

    @Schema(description = "罰金タイプ")
    @NotBlank(message = "罰金タイプはNULLまたは空文字列が不可")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,10}$", message = "罰金タイプを正しく入力してください。")
    private String type;

    @Schema(description = "罰金金額")
    @NotBlank(message = "罰金金額はNULLまたは空文字列が不可")
    @Pattern(regexp = "(^[1-9]([0-9]+)?(\\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9])?$)", message = "罰金金額を正しく入力してください。")
    private String money;
}
