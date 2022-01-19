package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "休暇申請を追加する用フォーム")
public class InsertLeaveForm {

    @Schema(description = "理由")
    @NotBlank(message = "理由はNULLまたは空文字列が不可")
    @Length(max = 200,message = "理由は200の文字列の以下が必要")
    private String reason;

    @Schema(description = "開始時間")
    @NotBlank(message = "開始時間はNULLまたは空文字列が不可")
    private String start;

    @Schema(description = "終了時間")
    @NotBlank(message = "終了時間はNULLまたは空文字列が不可")
    private String end;

    @Schema(description = "タイプ")
    @NotNull(message = "タイプはNULLまたは空文字列が不可")
    @Range(min = 1, max = 2, message = "タイプを正しく入力してください。")
    private Byte type;
}
