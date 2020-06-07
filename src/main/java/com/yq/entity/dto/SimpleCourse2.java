package com.yq.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * 专门用来封装精品课程
 */
@Data
public class SimpleCourse2 {
    Integer courseId;//课程id
    String name;//课程名称
    String nickName;//用户昵称
    String cover;//课程封面图
    Integer userId;//用户id
    Integer like;//课程点赞数
    String twoSubject;//二级分类名称
    String oneSubject;//一级分类名称
    Date createTime;//课程发布时间
}
