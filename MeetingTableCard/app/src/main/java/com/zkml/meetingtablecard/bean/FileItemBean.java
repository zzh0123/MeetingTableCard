package com.zkml.meetingtablecard.bean;

import java.util.List;

/**
 * @author: zzh
 * data : 2020/12/7
 * description：
 */
public class FileItemBean {
    private String fileName;

    private List <String> fileUrls;

    private String type;

    private String createTime;

    public void setFileName(String fileName){
        this.fileName = fileName;
    }
    public String getFileName(){
        return this.fileName;
    }
    public void setFileUrls(List<String> fileUrls){
        this.fileUrls = fileUrls;
    }
    public List<String> getFileUrls(){
        return this.fileUrls;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }
    public String getCreateTime(){
        return this.createTime;
    }
}
