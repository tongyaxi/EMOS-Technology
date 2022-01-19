package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbAmectType;
import com.example.emos.api.service.AmectTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/amect_type")
@Tag(name = "AmectTypeController", description = "罰金タイプWebAPI")
public class AmectTypeController {
    @Autowired
    private AmectTypeService amectTypeService;

    @GetMapping("/searchAllAmectType")
    @Operation(summary = "罰金タイプを検索する")
    @SaCheckLogin
    public R searchAllAmectType() {
        ArrayList<TbAmectType> list = amectTypeService.searchAllAmectType();
        return R.ok().put("list", list);
    }

    @Operation(summary = "罰金タイプをページデータで検索")
    @PostMapping("/searchAmectTypeByPage")
    @SaCheckPermission(value = {"ROOT"})
    public R searchAmectTypeByPage(@Valid @RequestBody SearchAmectTypeByPageForm form){

        String type = form.getType();
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;

        HashMap param = new HashMap(){{
            put("type", type);
            put("start", start);
            put("length", length);
        }};

        PageUtils pageUtils=  amectTypeService.searchAmectTypeByPage(param);
        return R.ok().put("list", pageUtils);
    }

    @Operation(summary = "罰金タイプを新規する")
    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT"})
    public R insert(@Valid @RequestBody InsertAmectTypeForm form) {
        TbAmectType amectType = JSONUtil.parse(form).toBean(TbAmectType.class);
        int rows = amectTypeService.insert(amectType);
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "罰金タイプをIDで検索する")
    @PostMapping("/searchById")
    @SaCheckPermission(value = {"ROOT"})
    public R searchById(@Valid @RequestBody SearchAmectTypeByIdForm form) {
        HashMap map = amectTypeService.searchById(form.getId());
        return R.ok(map);
    }

    @Operation(summary = "罰金タイプを更新する")
    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT"})
    public R update(@Valid @RequestBody UpdateAmectTypeByIdForm form) {
        HashMap param=JSONUtil.parse(form).toBean(HashMap.class);
        int rows = amectTypeService.update(param);
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "罰金タイプを削除する")
    @PostMapping("/deleteAmectTypeByIds")
    @SaCheckPermission(value = {"ROOT"})
    public R deleteAmectTypeByIds(@Valid @RequestBody DeleteAmectTypeByIdsForm form) {
        int rows = amectTypeService.deleteAmectTypeByIds(form.getIds());
        return R.ok().put("rows", rows);
    }
}
