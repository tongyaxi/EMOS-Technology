package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "役割のページデータ検証用フォーム")
public class SearchRoleByPageForm {

    @Schema(description = "役割")
    private String roleName;

    @Schema(description = "ページ数")
    @NotNull(message = "ページ数はNULLが不可")
    @Min(value = 1,message = "ページ数は1以上が必要")
    private Integer page;

    @Schema(description = "ページ毎のレコード数")
    @NotNull(message = "レコード数はNULLが不可")
    @Range(min = 10, max = 50, message = "レコード数は10~50の間が必要")
    private Integer length;
}
