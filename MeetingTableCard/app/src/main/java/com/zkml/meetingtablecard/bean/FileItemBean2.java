package com.zkml.meetingtablecard.bean;

import java.util.List;

/**
 * @author: zzh
 * data : 2020/12/7
 * description：
 */
public class FileItemBean2 {
    private String fileName;
    private String type;
    private String date;
    private List<String> urls;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
