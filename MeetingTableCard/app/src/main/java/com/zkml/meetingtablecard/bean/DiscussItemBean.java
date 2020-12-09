package com.zkml.meetingtablecard.bean;

/**
 * @author: zzh
 * data : 2020/12/8
 * description：
 */
public class DiscussItemBean {
    private String id;

    private String comment;

    private String userId;

    private String username;

    private String commentTime;

    private String selfFlag;

    private int totalLikeCount;

    private boolean currentLikeFlag;

    private String headImgUrl;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setComment(String comment){
        this.comment = comment;
    }
    public String getComment(){
        return this.comment;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
    public String getUserId(){
        return this.userId;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setCommentTime(String commentTime){
        this.commentTime = commentTime;
    }
    public String getCommentTime(){
        return this.commentTime;
    }
    public void setSelfFlag(String selfFlag){
        this.selfFlag = selfFlag;
    }
    public String getSelfFlag(){
        return this.selfFlag;
    }
    public void setTotalLikeCount(int totalLikeCount){
        this.totalLikeCount = totalLikeCount;
    }
    public int getTotalLikeCount(){
        return this.totalLikeCount;
    }
    public void setCurrentLikeFlag(boolean currentLikeFlag){
        this.currentLikeFlag = currentLikeFlag;
    }
    public boolean getCurrentLikeFlag(){
        return this.currentLikeFlag;
    }
    public void setHeadImgUrl(String headImgUrl){
        this.headImgUrl = headImgUrl;
    }
    public String getHeadImgUrl(){
        return this.headImgUrl;
    }
}
