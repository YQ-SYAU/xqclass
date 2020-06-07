package com.yq.service;

import com.yq.entity.Banner;
import com.yq.entity.dto.IndexDto;
import com.yq.entity.dto.OneSubjectDto;
import com.yq.entity.dto.SimpleCourse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * 为首页数据提供服务
 */
@Service
public class IndexService {

    @Autowired
    CourseService courseService;
    @Autowired
    BannerService bannerService;
    @Autowired
    OneSubjectService oneSubjectService;

    /**
     *查询首页数据
     * @return
     */
    public IndexDto index() {
        IndexDto indexDto = new IndexDto();
        //查询热门课程
        indexDto.setHotCourses(courseService.findCourseHot());
        //查询精品课程
        indexDto.setBestCourses(courseService.findCourseBest());
        //查询新品课程
        indexDto.setNewCourses(courseService.findCourseNew());
        //查询轮播图
        List<Banner> bannerList = bannerService.findAll();
        indexDto.setBanners(bannerList);
        //查询分类
        List<OneSubjectDto> allSubject = oneSubjectService.findAllSubject();
        indexDto.setSubjects(allSubject);
        return indexDto;
    }

    /**
     * 按年份查询课程
     * @return
     */
    public Map<String,List<SimpleCourse>> archive() {
        return courseService.archive();
    }

    /**
     * 查询发布的课程总数
     * @return
     */
    public Integer countCourse() {
        Integer count = courseService.countCourse();

        return count;
    }
}
