package com.yq.entity.dto;

/**
 * 业务类型（存储redis时使用）
 */
public enum BType {

    LIKED_COURSE("课程点赞"),
    LIKED_COURSE_REPORT("课程点赞数"),

    ATTENTION_AUTH("关注作者"),
    ATTENTION_AUTH_REPORT("关注作者数"),

    COLLECT_COURSE("课程收藏"),
    COLLECT_COURSE_REPORT("课程收藏数"),

    USER_FAN("用户粉丝"),
    USER_FAM_REPORT("用户粉丝数");

    private String bType;
    BType(String bType){
        this.bType=bType;
    }
}
