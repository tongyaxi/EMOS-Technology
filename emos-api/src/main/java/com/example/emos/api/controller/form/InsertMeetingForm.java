package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "会議新規用フォーム")
public class InsertMeetingForm {

    @Schema(description = "会議課題")
    @NotBlank(message = "会議課題はNULLまたは空文字列が不可")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,30}$", message = "会議課題を正しく入力してください。")
    private String title;

    @Schema(description = "日付")
    @NotBlank(message = "日付はNULLまたは空文字列が不可")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$", message = "日付を正しく入力してください")
    private String date;

    @Schema(description = "会議場所")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,20}$", message = "会議場所を正しく入力してください。")
    private String place;

    @Schema(description = "開始時間")
    @NotBlank(message = "開始時間はNULLまたは空文字列が不可")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):(00|30)$", message = "開始時間を正しく入力してください。")
    private String start;

    @Schema(description = "終了時間")
    @NotBlank(message = "終了時間はNULLまたは空文字列が不可")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):(00|30)$", message = "終了時間を正しく入力してください。")
    private String end;

    @Schema(description = "会議タイプ")
    @NotNull(message = "会議タイプはNULLまたは空文字列が不可")
    @Range(min = 1, max = 2, message = "会議タイプを正しく入力してください。")
    private Byte type;

    @Schema(description = "参加者")
    @NotBlank(message = "参加者はNULLまたは空文字列が不可")
    private String members;

    @Schema(description = "備考")
    @NotBlank(message = "備考はNULLまたは空文字列が不可")
    @Length(min = 1, max = 200)
    private String desc;
}
