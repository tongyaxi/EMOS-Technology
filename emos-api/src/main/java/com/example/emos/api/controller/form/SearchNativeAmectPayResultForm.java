package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "Navtiveの支払結果を検索する用フォーム")
public class SearchNativeAmectPayResultForm {

    @Schema(description = "罰金記録IDはNULLまたは空文字列が不可")
    @NotNull(message = "罰金記録ID")
    @Min(value = 1, message = "罰金記録IDは1以上が必要")
    private Integer amectId;
}
