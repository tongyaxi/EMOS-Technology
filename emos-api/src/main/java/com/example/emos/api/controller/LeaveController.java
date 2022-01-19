package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.DeleteLeaveByIdForm;
import com.example.emos.api.controller.form.InsertLeaveForm;
import com.example.emos.api.controller.form.SearchLeaveByIdForm;
import com.example.emos.api.controller.form.SearchLeaveByPageForm;
import com.example.emos.api.db.pojo.TbLeave;
import com.example.emos.api.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

@RestController
@RequestMapping("/leave")
@Tag(name = "LeaveController", description = "休暇管理WebAPI")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/searchLeaveByPage")
    @Operation(summary = "休暇管理をページデータで検索")
    @SaCheckLogin
    public R searchLeaveByPage(@Valid @RequestBody SearchLeaveByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("start", start);
        param.put("myId", StpUtil.getLoginIdAsInt());
        if (!(StpUtil.hasPermission("LEAVE:SELECT")||StpUtil.hasPermission("ROOT"))) {
            param.put("userId", StpUtil.getLoginIdAsInt());
        }
        PageUtils pageUtils = leaveService.searchLeaveByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @Operation(summary = "休暇申請を追加する")
    @PostMapping("/insert")
    @SaCheckLogin
    public R insert(@Valid @RequestBody InsertLeaveForm form){

        // 終了時間は開始時間より小さいかを検証する
        DateTime startTime = DateUtil.parse(form.getStart());
        DateTime endTime = DateUtil.parse(form.getEnd());
        if(startTime.isAfterOrEquals(endTime)){
            return R.error("開始時間は終了時間の以下が必要");
        }

        // 当休暇申請は既存の休暇申請に比べて、申請日付上に衝突があるかをチェックする。
        HashMap param = new HashMap(){{
            put("userId", StpUtil.getLoginIdAsInt());
            put("start", form.getStart());
            put("end", form.getEnd());
        }};
        if(leaveService.searchContradiction(param)){
            return R.error("当休暇申請は既存の休暇申請に比べて、申請日付上に衝突がある。");
        }

        // daysを取得する
        long hours = startTime.between(endTime, DateUnit.HOUR);
        String days = new BigDecimal(hours).divide(new BigDecimal(24), 1, RoundingMode.CEILING).toString();

        if(days.contains(".0")){
            days = days.replace(".0", "");
        }
        if(days.equals("0")){
            days = "0.1";
        }

        TbLeave tbLeave = JSONUtil.parse(form).toBean(TbLeave.class);
        tbLeave.setDays(days);
        tbLeave.setUserId(StpUtil.getLoginIdAsInt());
        int rows = leaveService.insert(tbLeave);
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "休暇申請を削除する")
    @PostMapping("/deleteLeaveById")
    @SaCheckLogin
    public R deleteLeaveById(@Valid @RequestBody DeleteLeaveByIdForm form) {
        HashMap param = new HashMap() {{
            put("id", form.getId());
            put("userId", StpUtil.getLoginIdAsInt());
        }};
        int rows = leaveService.deleteLeaveById(param);
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "休暇記録をIDで検索する")
    @PostMapping("/searchLeaveById")
    @SaCheckLogin
    public R searchLeaveById(@Valid @RequestBody SearchLeaveByIdForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        if (!(StpUtil.hasPermission("LEAVE:SELECT")||StpUtil.hasPermission("ROOT"))) {
            param.put("userId", StpUtil.getLoginIdAsInt());
        }
        HashMap map = leaveService.searchLeaveById(param);
        return R.ok(map);
    }
}
