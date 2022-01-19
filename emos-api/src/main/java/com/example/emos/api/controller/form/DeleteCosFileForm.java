package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "TencentCloudCOSファイルを削除する用フォーム")
public class DeleteCosFileForm {

    @NotEmpty(message = "pathesはNULL又は空文字列が不可")
    private String[] pathes;
}
