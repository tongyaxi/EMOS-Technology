package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbUser;
import com.example.emos.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * @RestController: 说明Controller接收或者返回的数据都是JSON格式的
 * @RequestMapping： 给Controller分配的相对路径
 * @Tag/@Operation: 用于SpringDoc，不加的话在Swagger页面看不到web方法，无法进行调试
 * @Valid: SpringBoot在往Form中保存数据的时候，就可以进行后端表单验证
 * @RequestBody: 接收JSON字符串
 * @SaCheckLogin: (SA-Token)判断用户是否登录，登录过的user可以修改密码
 */
@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "ユーザー管理WebAPI")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "ログイン機能")
    public R login(@Valid @RequestBody LoginForm loginForm) {
        // Hutool工具类(https://www.hutool.cn/)
        HashMap param = JSONUtil.parse(loginForm).toBean(HashMap.class);
        Integer userId = userService.login(param);
        R r = R.ok().put("result", userId != null?true:false);

        if (userId != null) {
            // https://sa-token.dev33.cn/
            // 权限认证框架SA-Token，根据userId等信息生成令牌字符串，缓存到Redis中，以cookie方式存储，返回给浏览器
            // 等到下次请求，顺带cookie里的token信息返回给后端，后端验证cookie中token信息是否OK
            // (true:登录成功过执行后续操作。false:没有登陆过或者失效，重定向至登录画面)
            StpUtil.setLoginId(userId);
            Set<String> permissions = userService.searchUserPermissions(userId);
            // 新版chrome不支持前端ajax withCredentials，导致ajax无法提交cookie，所以要把生成的token返回个前端
            // 前端保存在storage中，每次在ajax的header上提交token。
            String token = StpUtil.getTokenInfo().getTokenValue();
            r.put("permissions",permissions).put("token",token);
        }
        return r;
    }

    /**
     * 登陆成功后加载用户的基本信息
     */
    @GetMapping("/loadUserInfo")
    @Operation(summary = "ユーザー基本情報の検索")
    @SaCheckLogin
    public R loadUserInfo() {
        int userId = StpUtil.getLoginIdAsInt();
        HashMap summary = userService.searchUserSummary(userId);
        return R.ok(summary);
    }

    @GetMapping("/logout")
    @Operation(summary = "ログアウト")
    public R logout() {
        // 1.删除Redis中缓存的信息2.使浏览器保存的cookie过期
        StpUtil.logout();
        return R.ok();
    }

    @PostMapping("/updatePassword")
    @SaCheckLogin
    @Operation(summary = "パスワード変更")
    public R updatePassword(@RequestBody @Valid UpdatePasswordForm updatePasswordForm) {
        // 根据令牌解析出用户的userId(因为生成令牌时是用userId生成的，所以可逆向解析)
        int userId = StpUtil.getLoginIdAsInt();
        // JDK新用法，new的时候赋值
        HashMap hashMap = new HashMap(){{
            put("userId",userId);
            put("password", updatePasswordForm.getPassword());
        }};
        int rows = userService.updatePassword(hashMap);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchUserByPage")
    @Operation(summary = "ユーザー情報をページデータで検索")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public R searchUserByPage(@RequestBody @Valid SearchUserByPageForm searchUserByPageForm) {
        int page = searchUserByPageForm.getPage();
        int length = searchUserByPageForm.getLength();
        int start = (page - 1) * length;
        HashMap param = JSONUtil.parse(searchUserByPageForm).toBean(HashMap.class);
        param.put("start", start);
        PageUtils pageUtils = userService.searchUserByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/insert")
    @Operation(summary = "ユーザー新規")
    @SaCheckPermission(value = {"ROOT","USER:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertUserForm insertUserForm) {
        TbUser tbUser = JSONUtil.parse(insertUserForm).toBean(TbUser.class);
        tbUser.setStatus((byte)1);
        tbUser.setRole(JSONUtil.parseArray(insertUserForm.getRole()).toString());
        tbUser.setCreateTime(new Date());
        int rows = userService.insert(tbUser);
        return  R.ok().put("rows",rows);
    }

    @PostMapping("/searchUserById")
    @Operation(summary = "IDによるユーザーを検索する")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public R searchUserById(@Valid @RequestBody SearchUserByIdForm form) {
        HashMap map = userService.searchUserById(form.getUserId());
        return R.ok(map);
    }

    @Operation(summary = "ユーザーを変更する")
    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT","USER:UPDATE"}, mode = SaMode.OR)
    public R update(@Valid @RequestBody UpdateUserForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        // 需要对role做处理。Integer数组➡JsonArray对象.toString()，存入数据库，因为数据库此类型格式是Json
        param.replace("role", JSONUtil.parseArray(form.getRole()).toString());
        int rows = userService.update(param);
        if(rows == 1) {
            // 修改资料后，将用户踢下线，因为修改过后的信息可能设计到权限修改，如果不踢下线，存储在浏览器中storage中的权限信息是旧的
            StpUtil.logoutByLoginId(form.getUserId());
        }
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "ユーザーを削除する")
    @PostMapping("/delete")
    @SaCheckPermission(value = {"ROOT", "USER:DELETE"}, mode = SaMode.OR)
    public R delete(@Valid @RequestBody DeleteUserByIdsForm form) {
        // 判断是否删除的是自己的用户
        Integer userId = StpUtil.getLoginIdAsInt();
        if(ArrayUtil.contains(form.getIds(), userId)) {
            return R.error("自分のユーザー情報を削除しないでください。");
        }
        int rows = userService.deleteUserByIds(form.getIds());
        // 修改成功后将用户踢下线
        if(rows > 0) {
            for(int id : form.getIds()) {
                StpUtil.logoutByLoginId(id);
            }
        }
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "ユーザー名と部門情報を検索する")
    @PostMapping("/searchNameAndDept")
    @SaCheckLogin
    public R searchNameAndDept(@Valid @RequestBody SearchNameAndDeptForm form) {
        HashMap map = userService.searchNameAndDept(form.getId());
        return R.ok(map);
    }

    @Operation(summary = "全てのユーザーを検索する")
    @GetMapping("/searchAllUser")
    @SaCheckLogin
    public R searchAllUser() {
        ArrayList<HashMap> list = userService.searchAllUser();
        return R.ok().put("list", list);
    }

    /**
     * 生成登陆二维码的字符串
     */
    @GetMapping("/createQrCode")
    @Operation(summary = "生成二维码Base64格式的字符串")
    public R createQrCode() {
        HashMap map = userService.createQrCode();
        return R.ok(map);
    }

    /**
     * 检测登陆验证码
     *
     * @param form
     * @return
     */
    @PostMapping("/checkQrCode")
    @Operation(summary = "检测登陆验证码")
    public R checkQrCode(@Valid @RequestBody CheckQrCodeForm form) {
        boolean bool = userService.checkQrCode(form.getCode(), form.getUuid());
        return R.ok().put("result", bool);
    }

    @PostMapping("/wechatLogin")
    @Operation(summary = "微信小程序登陆")
    public R wechatLogin(@Valid @RequestBody WechatLoginForm form) {
        HashMap map = userService.wechatLogin(form.getUuid());
        boolean result = (boolean) map.get("result");
        if (result) {
            int userId = (int) map.get("userId");
            StpUtil.setLoginId(userId);
            Set<String> permissions = userService.searchUserPermissions(userId);
            map.remove("userId");
            map.put("permissions", permissions);
        }
        return R.ok(map);
    }
}
