package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "罰金記録の更新検証用フォーム")
public class UpdateAmectForm {

    @Schema(description = "罰金記録ID")
    @NotNull(message = "罰金記録IDはNULLまたは空文字列が不可")
    @Min(value = 1, message = "罰金記録IDは1以上が必要")
    private Integer id;

    @Schema(description = "罰金金額")
    @NotBlank(message = "罰金金額はNULLまたは空文字列が不可")
    @Pattern(regexp = "(^[1-9]([0-9]+)?(\\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9])?$)", message = "罰金金額を正しく入力してください。")
    private String amount;

    @Schema(description = "罰金タイプ")
    @NotNull(message = "罰金タイプはNULLまたは空文字列が不可")
    @Min(value = 1, message = "罰金タイプは1以上が必要")
    private Byte typeId;

    @Schema(description = "罰金理由")
    @NotBlank(message = "罰金理由はNULLまたは空文字列が不可")
    @Length(max = 200, message = "罰金理由の入力文字列は200以下が必要")
    private String reason;
}
