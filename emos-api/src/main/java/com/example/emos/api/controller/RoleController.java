package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbRole;
import com.example.emos.api.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/role")
@Tag(name="RoleController", description = "役割管理WebAPI")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/searchAllRole")
    @Operation(summary = "全役割を検索する")
    public R searchAllRole() {

        ArrayList<HashMap> allRoles = roleService.searchAllRole();
        return R.ok().put("allRoles", allRoles);
    }

    @Operation(summary = "役割情報をページデータで検索")
    @PostMapping("/searchRoleByPage")
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    public R searchRoleByPage(@Valid @RequestBody SearchRoleByPageForm form){
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        PageUtils pages = roleService.searchRoleByPage(param);
        return R.ok().put("page",pages);
    }

    @Operation(summary = "役割を新規する")
    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "ROLE:INSERT"}, mode = SaMode.OR)
    public R insert(@Valid @RequestBody InsertRoleForm insertRoleForm) {

        TbRole tbRole = new TbRole();
        tbRole.setRoleName(insertRoleForm.getRoleName());
        tbRole.setDesc(insertRoleForm.getDesc());
        tbRole.setPermissions(JSONUtil.parseArray(insertRoleForm.getPermissions()).toString());

        int rows = roleService.insert(tbRole);
        return R.ok().put("rows",rows);
    }

    @Operation(summary = "役割をIDで検索する")
    @PostMapping("/searchRoleById")
    @SaCheckPermission(value = {"ROOT","ROLE:SELECT"}, mode = SaMode.OR)
    public R searchRoleById(@Valid @RequestBody SearchRoleByIdForm form){
        HashMap role = roleService.searchRoleById(form.getId());
        return R.ok(role);
    }

    @Operation(summary = "役割を更新する")
    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT","ROLE:UPDATE"}, mode = SaMode.OR)
    public R update(@Valid @RequestBody UpdateRoleForm form){
        TbRole tbRole = new TbRole();

        tbRole.setId(form.getId());
        tbRole.setRoleName(form.getRoleName());
        tbRole.setDesc(form.getDesc());
        tbRole.setPermissions(JSONUtil.parseArray(form.getPermissions()).toString());
        int rows = roleService.update(tbRole);

        // 当該の役割情報を成功に修正して、かつ、当該の役割は持っている権限は修正される場合
        if(rows == 1 && form.getChanged()){
            ArrayList<Integer> userList = roleService.searchUserIdByRoleId(form.getId());
            userList.forEach(userId -> {
                StpUtil.logoutByLoginId(userId);
            });
        }
        return R.ok().put("rows",rows);
    }

    @Operation(summary = "役割を削除する")
    @PostMapping("/deleteRoleByIds")
    @SaCheckPermission(value = {"ROOT","ROLE:DELETE"}, mode = SaMode.OR)
    public R delete(@RequestBody @Valid DeleteRoleByIdsForm form){
        int rows = roleService.deleteRoleByIds(form.getIds());
        return R.ok().put("rows", rows);
    }
}
