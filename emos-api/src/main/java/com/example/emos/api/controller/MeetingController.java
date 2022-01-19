package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.config.tencent.TrtcUtil;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbMeeting;
import com.example.emos.api.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/meeting")
@Tag(name = "MeetingController", description = "会議WebAPI")
@Slf4j
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private TrtcUtil trtcUtil;

    @Value("${tencent.trtc.appId}")
    private int appId;

    @PostMapping("/searchOfflineMeetingByPage")
    @Operation(summary = "オフライン会議情報をページデータで検索")
    @SaCheckLogin
    public R searchOfflineMeetingByPage(@Valid @RequestBody SearchOfflineMeetingByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) *length;
        HashMap hashMap = new HashMap(){
            {
                put("date", form.getDate());
                put("mold", form.getMold());
                put("start", start);
                put("length", form.getLength());
                put("userId", StpUtil.getLoginId());
            }
        };
        PageUtils pageUtils = meetingService.searchOfflineMeetingByPage(hashMap);
        return R.ok().put("page", pageUtils);
    }

    @Operation(summary = "会議室の週スケジュールを検索する")
    @PostMapping("/searchOfflineMeetingInWeek")
    @SaCheckLogin
    public R searchOfflineMeetingInWeek(@Valid @RequestBody SearchOfflineMeetingInWeekForm form) {
        String date = form.getDate();
        DateTime startDate, endDate;
        // 日付で検索する
        if(date != null && date.length() > 0){
            startDate = DateUtil.parseDate(date);
            endDate = startDate.offsetNew(DateField.DAY_OF_WEEK, 6);
        }else{
            startDate = DateUtil.beginOfWeek(new Date());
            endDate = DateUtil.endOfWeek(new Date());
        }
        HashMap param = new HashMap(){{
            put("place", form.getName());
            put("startDate", startDate.toDateStr());
            put("endDate", endDate.toDateStr());
            put("mold", form.getMold());
            put("userId", StpUtil.getLoginIdAsLong());
        }};

        ArrayList<HashMap> list = meetingService.searchOfflineMeetingInWeek(param);
        // 何曜日を生成する。
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_WEEK);
        ArrayList days = new ArrayList();
        range.forEach(one ->{
            JSONObject json = new JSONObject();
            json.set("date",one.toString("MM/dd"));
            json.set("day",one.dayOfWeekEnum());
            days.add(json);
        });

        return R.ok().put("list", list).put("days", days);
    }

    @Operation(summary = "会議情報を検索する")
    @PostMapping("/searchMeetingInfo")
    @SaCheckLogin
    public R searchMeetingInfo(@Valid @RequestBody SearchMeetingInfoForm form) {
        HashMap map = meetingService.searchMeetingInfo(form.getStatus(), form.getId());
        return R.ok(map);
    }

    @Operation(summary = "会議を新規する。")
    @PostMapping("/insert")
    @SaCheckLogin
    public R insert(@Valid @RequestBody InsertMeetingForm form) {
        DateTime start = DateUtil.parse(form.getDate() + " " + form.getStart());
        DateTime end = DateUtil.parse(form.getDate() + " " + form.getEnd());
        if (start.isAfterOrEquals(end)) {
            return R.error("終了時間は開始時間より大きいが必須");
        } else if (new DateTime().isAfterOrEquals(start)) {
            return R.error("開始時間は現在の時刻より小さくて行けない。");
        }
        TbMeeting meeting = JSONUtil.parse(form).toBean(TbMeeting.class);
        meeting.setUuid(UUID.randomUUID().toString(true));
        meeting.setCreatorId(StpUtil.getLoginIdAsInt());
        meeting.setStatus((short) 1);
        int rows = meetingService.insert(meeting);
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "WorkFlowから通知を受け取る")
    @PostMapping("/recieveNotify")
    public R recieveNotify(@Valid @RequestBody RecieveNotifyForm form){
        if(form.getResult().equals("同意")){
            log.debug(form.getUuid()+"の会議を審査するのは成功した。");
        }
        else{
            log.debug(form.getUuid()+"の会議を審査するのは失敗した。");
        }
        return R.ok();
    }

    @Operation(summary = "会議申請を削除する。")
    @PostMapping("/deleteMeetingApplication")
    @SaCheckLogin
    public R deleteMeetingApplication(@Valid @RequestBody DeleteMeetingApplicationForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("creatorId", StpUtil.getLoginIdAsLong());
        param.put("userId", StpUtil.getLoginIdAsLong());
        int rows = meetingService.deleteMeetingApplication(param);
        return R.ok().put("rows", rows);
    }

    @Operation(summary = "オンライン会議情報をページデータで検索")
    @PostMapping("/searchOnlineMeetingByPage")
    @SaCheckLogin
    public R searchOnlineMeetingByPage(@Valid @RequestBody SearchOnlineMeetingByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        HashMap param = new HashMap() {{
            put("date", form.getDate());
            put("mold", form.getMold());
            put("userId", StpUtil.getLoginId());
            put("start", start);
            put("length", length);
        }};
        PageUtils pageUtils = meetingService.searchOnlineMeetingByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @Operation(summary = "ユーザーのサインインを取得する")
    @GetMapping("/searchMyUserSig")
    @SaCheckLogin
    public R searchMyUserSign(){
        int userId = StpUtil.getLoginIdAsInt();
        String userSig = trtcUtil.genUserSig(userId + "");
        return R.ok().put("userSig", userSig).put("userId", userId).put("appId", appId);
    }

    @Operation(summary = "オンライン会議のRoomIdを取得する")
    @PostMapping("/searchRoomIdByUUID")
    @SaCheckLogin
    public R searchRoomIdByUUID(@RequestBody @Valid SearchRoomIdByUUIDForm form){
        Long roomId = meetingService.searchRoomIdByUUID(form.getUuid());
        return R.ok().put("roomId", roomId);
    }

    @Operation(summary = "オンライン会議の参加者を取得する")
    @PostMapping("/searchOnlineMeetingMembers")
    @SaCheckLogin
    public R searchOnlineMeetingMembers(@Valid @RequestBody SearchOnlineMeetingMembersForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("userId", StpUtil.getLoginIdAsInt());
        ArrayList<HashMap> list = meetingService.searchOnlineMeetingMembers(param);
        return R.ok().put("list", list);
    }

    @Operation(summary = "会議のチェックインを実施する")
    @PostMapping("/updateMeetingPresent")
    @SaCheckLogin
    public R updateMeetingPresent(@Valid @RequestBody UpdateMeetingPresentForm form) {
        HashMap param = new HashMap() {{
            put("meetingId", form.getMeetingId());
            put("userId", StpUtil.getLoginIdAsInt());
        }};
        boolean bool = meetingService.searchCanCheckinMeeting(param);
        if (bool) {
            int rows = meetingService.updateMeetingPresent(param);
            return R.ok().put("rows", rows);
        }
        return R.ok().put("rows", 0);
    }
}