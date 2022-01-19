package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbMeetingDao {

    public ArrayList<HashMap> searchOfflineMeetingByPage(HashMap param);
    public long searchOfflineMeetingCount(HashMap param);

    // 查询会议参加者是否是同一部门
    public boolean searchMeetingMembersInSameDept(String uuid);

    public int updateMeetingInstanceId(HashMap param);

    public int insert(TbMeeting meeting);

    // 周日历信息
    public ArrayList<HashMap> searchOfflineMeetingInWeek(HashMap param);
    // 会议信息
    public HashMap searchMeetingInfo(long id);
    // 当前会议信息
    public HashMap searchCurrentMeetingInfo(long id);

    public int deleteMeetingApplication(HashMap param);

    public HashMap searchMeetingById(HashMap param);

    public ArrayList<HashMap> searchOnlineMeetingByPage(HashMap param);

    public long searchOnlineMeetingCount(HashMap param);

    public ArrayList<HashMap> searchOnlineMeetingMembers(HashMap param);

    public long searchCanCheckinMeeting(HashMap param);
    public int updateMeetingPresent(HashMap param);
}