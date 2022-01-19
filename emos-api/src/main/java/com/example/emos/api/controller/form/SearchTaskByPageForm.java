package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "審査事項のページデータ検証用フォーム")
public class SearchTaskByPageForm {

    @Schema(description = "申請者")
    @Pattern(regexp = "^[\\e4e00-\\u9fa5]{2,20}$", message = "申請者を正しく入力してください。")
    private String creatorName;

    @Schema(description = "審査タイプ")
    @Pattern(regexp = "^员工请假$|^会议申请$|^报销申请$", message = "審査タイプを正しく入力してください。")
    private String type;

    @Schema(description = "WorkFlow InstanceId")
    @Pattern(regexp = "^[0-9a-zA-Z\\-]{36}$", message = "WorkFlow InstanceIdを正しく入力してください。")
    private String instanceId;

    @Schema(description = "申請ステータス")
    @NotBlank(message = "申請ステータスはNULLまたは空文字列が不可")
    @Pattern(regexp = "^待审批$|^已审批$|^已结束$", message = "申請ステータスを正しく入力してください。")
    private String status;

    @Schema(description = "ページ数")
    @NotNull(message = "ページ数はNULLが不可")
    @Min(value = 1, message = "ページ数は1以上が必要")
    private Integer page;

    @Schema(description = "ページ毎のレコード数")
    @NotNull(message = "レコード数はNULLが不可")
    @Range(min = 10, max = 100, message = "レコード数は10~100の間が必要")
    private Integer length;
}
