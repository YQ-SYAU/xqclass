package com.yq.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

//封装收藏课程
@Data
@EqualsAndHashCode
public class CollectCourse {
    Integer courseId;//课程id
    String name;//课程名称
    String cover;//课程封面图
    String twoSubject;//二级分类名称
    String oneSubject;//一级分类名称
    Date createTime;//课程发布时间
}
