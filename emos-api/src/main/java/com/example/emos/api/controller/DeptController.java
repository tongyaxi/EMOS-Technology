package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbDept;
import com.example.emos.api.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/dept")
@Tag(name = "DeptController", description = "部門管理WebAPI")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @Operation(summary = "部門情報をページデータで検索")
    @PostMapping("/searchDeptByPage")
    @SaCheckPermission(value = {"ROOT","DEPT:SELECT"}, mode = SaMode.OR)
    public R searchDeptByPage(@RequestBody @Valid SearchDeptByPageForm form){
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;

        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("start", start);
        PageUtils pageUtils = deptService.searchDeptByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @Operation(summary = "部門を新規する")
    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT","DEPT:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertDeptForm form){
        TbDept tbDept = JSONUtil.parse(form).toBean(TbDept.class);
        int rows = deptService.insert(tbDept);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchById")
    @Operation(summary = "部門情報をIDで検索する")
    @SaCheckPermission(value = {"ROOT", "DEPT:SELECT"}, mode = SaMode.OR)
    public R searchById(@Valid @RequestBody SearchDeptByIdForm form) {
        HashMap map = deptService.searchById(form.getId());
        return R.ok(map);
    }

    @Operation(summary = "部門を更新する")
    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "DEPT:UPDATE"}, mode = SaMode.OR)
    public R update(@Valid @RequestBody UpdateDeptForm form) {
        TbDept dept = new TbDept();
        dept.setId(form.getId());
        dept.setDeptName(form.getDeptName());
        dept.setTel(form.getTel());
        dept.setEmail(form.getEmail());
        dept.setDesc(form.getDesc());
        int rows = deptService.update(dept);
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "部門を削除する")
    @PostMapping("/deleteDeptByIds")
    @SaCheckPermission(value = {"ROOT", "DEPT:DELETE"}, mode = SaMode.OR)
    public R deleteDeptByIds(@Valid @RequestBody DeleteDeptByIdsForm form) {
        int rows = deptService.deleteDeptByIds(form.getIds());
        return R.ok().put("rows", rows);
    }

    @GetMapping("/searchAllDept")
    @Operation(summary = "全ての部門情報を検索する")
    public R searchAllDept() {

        ArrayList<HashMap> allDept = deptService.searchAllDept();
        return R.ok().put("allDept", allDept);
    }

}