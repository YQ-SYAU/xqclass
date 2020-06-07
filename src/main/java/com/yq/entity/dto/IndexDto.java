package com.yq.entity.dto;

import com.yq.entity.Banner;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 封装首页数据
 */
@Data
public class IndexDto {

    //热门课程
    private List<SimpleCourse> hotCourses=new ArrayList<>();
    //精品课程
    private List<SimpleCourse2> bestCourses=new ArrayList<>();
    //新品课程
    private List<SimpleCourse> newCourses=new ArrayList<>();
    //分类
    private List<OneSubjectDto> subjects=new ArrayList<>();
    //轮播图
    private List<Banner> banners=new ArrayList<>();
}
