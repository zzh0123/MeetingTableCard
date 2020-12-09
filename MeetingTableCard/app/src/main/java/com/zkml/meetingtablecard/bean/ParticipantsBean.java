package com.zkml.meetingtablecard.bean;

/**
 * @author: zzh
 * data : 2020/12/9
 * description：
 */
public class ParticipantsBean {

    private String userId;

    private String realName;

    private boolean outsiderFlag;

    private String phone;

    public void setUserId(String userId){
        this.userId = userId;
    }
    public String getUserId(){
        return this.userId;
    }
    public void setRealName(String realName){
        this.realName = realName;
    }
    public String getRealName(){
        return this.realName;
    }
    public void setOutsiderFlag(boolean outsiderFlag){
        this.outsiderFlag = outsiderFlag;
    }
    public boolean getOutsiderFlag(){
        return this.outsiderFlag;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getPhone(){
        return this.phone;
    }
}
