package com.zkml.meetingtablecard.bean;

/**
 * @author: zzh
 * data : 2020/12/7
 * description：
 */
public class AgendaItemBean {
    private String beginTime;

    private String endTime;

    private String matter;

    private String address;

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
    public void setMatter(String matter){
        this.matter = matter;
    }
    public String getMatter(){
        return this.matter;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
