package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.ApprovalTaskForm;
import com.example.emos.api.controller.form.ArchiveTaskForm;
import com.example.emos.api.controller.form.SearchApprovalContentForm;
import com.example.emos.api.controller.form.SearchTaskByPageForm;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.service.ApprovalService;
import com.example.emos.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/approval")
@Tag(name="ApprovalController", description = "審査事項WebAPI")
@Slf4j
public class ApprovalController {

    @Autowired
    private UserService userService;
    @Autowired
    private ApprovalService approvalService;

    @Value("${emos.code}")
    private String code;
    @Value("${emos.tcode}")
    private String tcode;
    @Value("${workflow.url}")
    private String workflow;

    @Operation(summary = "審査事項情報をページデータで検索")
    @PostMapping("/searchTaskByPage")
    @SaCheckPermission(value = {"WORKFLOW:APPROVAL","FILE:ARCHIVE"}, mode = SaMode.OR)
    public R searchTaskByPage(@RequestBody @Valid SearchTaskByPageForm form){
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        int userId = StpUtil.getLoginIdAsInt();
        ArrayList<String> roles = userService.searchUserRoles(userId);

        param.put("userId", userId);
        param.put("role", roles);
        PageUtils pageUtils = approvalService.searchTaskByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/searchApprovalContent")
    @Operation(summary = "会議詳細・審査進捗を検索する")
    @SaCheckPermission(value = {"WORKFLOW:APPROVAL", "FILE:ARCHIVE"}, mode = SaMode.OR)
    public R searchApprovalContent(@Valid @RequestBody SearchApprovalContentForm form) {

        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        int userId = StpUtil.getLoginIdAsInt();
        param.put("userId", userId);
        param.put("role", userService.searchUserRoles(userId));
        HashMap content = approvalService.searchApprovalContent(param);
        return R.ok().put("content", content);
    }

    @GetMapping("/searchApprovalBpmn")
    @Operation(summary = "BPMNイメージを取得する")
    @SaCheckPermission(value = {"WORKFLOW:APPROVAL", "FILE:ARCHIVE"}, mode = SaMode.OR)
    public void searchApprovalBpmn(String instanceId, HttpServletResponse response) {
        if (StrUtil.isBlankIfStr(instanceId)) {
            throw new EmosException("Instance IDはNULL又は空文字列が不可");
        }
        HashMap param = new HashMap() {{
            put("code", code);
            put("tcode", tcode);
            put("instanceId", instanceId);
        }};
        String url = workflow + "/workflow/searchApprovalBpmn";
        HttpResponse resp = HttpRequest.post(url).header("content-type", "application/json").body(JSONUtil.toJsonStr(param)).execute();
        if (resp.getStatus() == 200) {
            // 括号里的内容支持包括流以及任何可关闭的资源，数据流会在 try 执行完毕后自动被关闭，而不用我们手动关闭了
            try (InputStream in = resp.bodyStream();
                 BufferedInputStream bis = new BufferedInputStream(in);
                 OutputStream out = response.getOutputStream();
                 BufferedOutputStream bos = new BufferedOutputStream(out)) {
                 IOUtils.copy(bis, bos);
            } catch (Exception e) {
                log.error("例外異常発生", e);
            }
        } else {
            log.error("BPMNイメージ取得に失敗しました。");
            throw new EmosException("BPMNイメージ取得に失敗しました。");
        }

    }

    @PostMapping("/approvalTask")
    @Operation(summary = "会議を審査する")
    @SaCheckPermission(value = {"WORKFLOW:APPROVAL"}, mode = SaMode.OR)
    public R approvalTask(@Valid @RequestBody ApprovalTaskForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        approvalService.approvalTask(param);
        return R.ok();
    }

    @Operation(summary = "アーカイブ任務")
    @PostMapping("/archiveTask")
    @SaCheckPermission(value = {"FILE:ARCHIVE"})
    public R archiveTask(@Valid @RequestBody ArchiveTaskForm form) {
        if (!JSONUtil.isJsonArray(form.getFiles())) {
            return R.error("files不是JSON数组");
        }
        HashMap param = new HashMap() {{
            put("taskId", form.getTaskId());
            put("files", form.getFiles());
            put("userId", StpUtil.getLoginIdAsInt());
        }};
        approvalService.archiveTask(param);
        return R.ok();
    }
}
