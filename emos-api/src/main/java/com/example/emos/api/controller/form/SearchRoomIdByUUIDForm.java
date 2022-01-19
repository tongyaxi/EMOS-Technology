package com.example.emos.api.controller.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "オンライン会議のRoomIdを検索する検証用フォーム")
public class SearchRoomIdByUUIDForm {

    @NotBlank(message = "UUIDはNULLまたは空文字列が不可")
    @Schema(description = "UUID")
    private String uuid;
}
