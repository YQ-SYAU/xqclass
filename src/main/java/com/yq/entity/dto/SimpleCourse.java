package com.yq.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 首页显示的课程相关信息封装对象
 */
@Data
public class SimpleCourse {
    Integer courseId;//课程id
    String name;//课程名称
    String nickName;//用户昵称
    String cover;//课程封面图
    Integer userId;//用户id
    Integer skim;//课程浏览次数
    String twoSubject;//二级分类名称
    String oneSubject;//一级分类名称
    Date createTime;//课程发布时间
}
