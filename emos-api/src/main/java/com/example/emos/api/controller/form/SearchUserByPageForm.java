package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "ユーザーのページデータ検証用フォーム")
public class SearchUserByPageForm {

    @Schema(description = "ページ数")
    @NotNull(message = "ページ数はNULLが不可")
    @Min(value = 1, message = "ページ数は1以上が必要")
    private Integer page;

    @Schema(description = "ページ毎のレコード数")
    @NotNull(message = "レコード数はNULLが不可")
    @Range(min = 10, max = 50, message = "レコード数は10~50の間が必要")
    private Integer length;

    @Schema(description = "氏名")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,10}$", message = "氏名を正しく入力してください。")
    private String name;

    @Schema(description = "性别")
    @Pattern(regexp = "^男$|^女$", message = "性別を正しく入力してください。")
    private String sex;

    @Schema(description = "役割")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,10}$", message = "役割を正しく入力してください。")
    private String role;

    @Min(value = 1, message = "部門IDは1以上が必要")
    private Integer deptId;

    @Min(value = 1, message = "statusは1以上が必要")
    private Integer status;
}
