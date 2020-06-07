package com.yq.entity.dto;

import com.yq.entity.User;


/**
 * 返回用户基本信息
 */


public class UserDto extends User {

    private Integer fans;//粉丝数
    private Integer productions;//作品数
    private Integer collects;//收藏数
    private Integer attentions;//关注数

    public Integer getFans() {
        return fans;
    }

    public void setFans(Integer fans) {
        this.fans = fans;
    }

    public Integer getProductions() {
        return productions;
    }

    public void setProductions(Integer productions) {
        this.productions = productions;
    }

    public Integer getCollects() {
        return collects;
    }

    public void setCollects(Integer collects) {
        this.collects = collects;
    }

    public Integer getAttentions() {
        return attentions;
    }

    public void setAttentions(Integer attentions) {
        this.attentions = attentions;
    }

}
