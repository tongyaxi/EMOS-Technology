package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Schema(description = "会議室の更新検証用フォーム")
@Data
public class UpdateMeetingRoomForm {

    @Schema(description = "会議室ID")
    @NotNull(message = "会議室IDはNULLまたは空文字列が不可")
    private Integer id;

    @Schema(description = "会議室名称")
    @NotBlank(message = "会議室名称はNULLまたは空文字列が不可")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,20}$",message = "会議室名称を正しく入力してください。")
    private String name;

    @Schema(description = "最大人数")
    @NotNull(message = "最大人数はNULLまたは空文字列が不可")
    @Range(min = 1, max = 99999,message = "最大人数は1~9999の間が必要")
    private String max;

    @Length(max = 20,message = "備考は20以上の文字列が不可")
    @Schema(description = "備考")
    private String desc;

    @Schema(description = "ステータス")
    @NotNull(message = "ステータスはNULLまたは空文字列が不可")
    @Range(min = 0,max = 1,message = "ステータスは0または1が必須")
    private Short status;
}
