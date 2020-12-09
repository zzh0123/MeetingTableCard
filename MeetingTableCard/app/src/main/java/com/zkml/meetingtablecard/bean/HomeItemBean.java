package com.zkml.meetingtablecard.bean;

/**
 * @author: zzh
 * data : 2020/12/3
 * description：
 */
public class HomeItemBean {
    private String name;
    private int iconRes;

    public HomeItemBean(String name, int iconRes) {
        this.name = name;
        this.iconRes = iconRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}
