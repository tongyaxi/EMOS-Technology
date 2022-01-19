package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "利用できる会議室の検証用フォーム")
public class SearchFreeMeetingRoomForm {

    @Schema(description = "日付")
    @NotBlank(message = "日付はNULLまたは空文字列が不可")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$", message = "日付を正しく入力してください。")
    private String date;

    @Schema(description = "開始時間")
    @NotBlank(message = "開始時間はNULLまたは空文字列が不可")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):(00|30)$", message = "開始時間を正しく入力してください。")
    private String start;

    @Schema(description = "終了時間")
    @NotBlank(message = "終了時間はNULLまたは空文字列が不可")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):(00|30)$", message = "終了時間を正しく入力してください。")
    private String end;
}