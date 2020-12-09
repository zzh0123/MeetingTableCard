package com.zkml.meetingtablecard.bean;

import java.io.Serializable;

/**
 * @author: zzh
 * data : 2020/12/3
 * description：
 */
public class MeetingItemBean implements Serializable {
    private String meetingApplyId;

    private String applyNo;

    private String meetingRoomId;

    private String conferenceName;

    private String beginTime;

    private String endTime;

    private String address;

    private String organName;

    private String applyRealName;

    private String applyRealPhone;

    private int useQty;

    private int meetingType;

    private String applyStaffId;

    private String applyUserFlag;

    public void setMeetingApplyId(String meetingApplyId){
        this.meetingApplyId = meetingApplyId;
    }
    public String getMeetingApplyId(){
        return this.meetingApplyId;
    }
    public void setApplyNo(String applyNo){
        this.applyNo = applyNo;
    }
    public String getApplyNo(){
        return this.applyNo;
    }
    public void setMeetingRoomId(String meetingRoomId){
        this.meetingRoomId = meetingRoomId;
    }
    public String getMeetingRoomId(){
        return this.meetingRoomId;
    }
    public void setConferenceName(String conferenceName){
        this.conferenceName = conferenceName;
    }
    public String getConferenceName(){
        return this.conferenceName;
    }
    public void setBeginTime(String beginTime){
        this.beginTime = beginTime;
    }
    public String getBeginTime(){
        return this.beginTime;
    }
    public void setEndTime(String endTime){
        this.endTime = endTime;
    }
    public String getEndTime(){
        return this.endTime;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return this.address;
    }
    public void setOrganName(String organName){
        this.organName = organName;
    }
    public String getOrganName(){
        return this.organName;
    }
    public void setApplyRealName(String applyRealName){
        this.applyRealName = applyRealName;
    }
    public String getApplyRealName(){
        return this.applyRealName;
    }
    public void setApplyRealPhone(String applyRealPhone){
        this.applyRealPhone = applyRealPhone;
    }
    public String getApplyRealPhone(){
        return this.applyRealPhone;
    }
    public void setUseQty(int useQty){
        this.useQty = useQty;
    }
    public int getUseQty(){
        return this.useQty;
    }
    public void setMeetingType(int meetingType){
        this.meetingType = meetingType;
    }
    public int getMeetingType(){
        return this.meetingType;
    }
    public void setApplyStaffId(String applyStaffId){
        this.applyStaffId = applyStaffId;
    }
    public String getApplyStaffId(){
        return this.applyStaffId;
    }
    public void setApplyUserFlag(String applyUserFlag){
        this.applyUserFlag = applyUserFlag;
    }
    public String getApplyUserFlag(){
        return this.applyUserFlag;
    }
}
