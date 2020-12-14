package com.zkml.meetingtablecard.bean;

/**
 * @author: zzh
 * data : 2020/12/11
 * description：
 */
public class AgendaDateBean {
    private String week;
    private String monthDay;
    private boolean isSelected;

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(String monthDay) {
        this.monthDay = monthDay;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
