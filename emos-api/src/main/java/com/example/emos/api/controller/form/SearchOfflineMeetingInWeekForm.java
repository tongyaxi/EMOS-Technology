package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "会議室の週スケジュール検証用フォーム")
public class SearchOfflineMeetingInWeekForm {

    @Schema(description = "会議室名")
    @NotBlank(message = "会議室名はNULLまたは空文字列が不可")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,20}$", message = "会議室名を正しく入力してください。")
    private String name;

    @Schema(description = "日付")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$", message = "日付を正しく入力してください。")
    private String date;

    @Schema(description = "MOLD")
    @NotBlank(message = "MOLDはNULLまたは空文字列が不可")
    @Pattern(regexp = "^全て会議$|^自分会議$", message = "MOLDを正しく入力してください。")
    private String mold;


}
