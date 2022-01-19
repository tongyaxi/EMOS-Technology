package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "罰金情報のページデータ検証用フォーム")
public class SearchAmectByPageForm {

    @Schema(description = "氏名")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,10}$", message = "氏名を正しく入力してください。")
    private String name;

    @Schema(description = "部門ID")
    @Min(value = 1, message = "部門IDは1の以上が必要")
    private Integer deptId;

    @Schema(description = "罰金タイプID")
    @Min(value = 1, message = "罰金タイプIDは1の以上が必要")
    private Integer typeId;

    @Schema(description = "開始時間")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$", message = "開始時間を正しく入力してください。")
    private String startDate;

    @Schema(description = "終了時間")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$", message = "終了時間を正しく入力してください。")
    private String endDate;

    @Schema(description = "払いステータス")
    @Min(value = 1, message = "払いステータスは1の以上が必要")
    private Byte status;

    @Schema(description = "ページ数")
    @NotNull(message = "ページ数はNULLが不可")
    @Min(value = 1,message = "ページ数は1以上が必要")
    private Integer page;

    @Schema(description = "ページ毎のレコード数")
    @NotNull(message = "レコード数はNULLが不可")
    @Range(min = 10, max = 50, message = "レコード数は10~50の間が必要")
    private Integer length;
}
