package com.yq.controller;

import com.yq.entity.Course;
import com.yq.entity.dto.OneSubjectDto;
import com.yq.entity.dto.TwoSubjectDto;
import com.yq.service.CourseService;
import com.yq.service.OneSubjectService;
import com.yq.service.TwoSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 查询分类下的各种课程（最热、最新）
 */
@Controller
@RequestMapping("/subject")
public class CourseListController {

    @Autowired
    CourseService courseService;
    @Autowired
    TwoSubjectService twoSubjectService;
    @Autowired
    OneSubjectService oneSubjectService;

    /**
     * 分页查询一级分类下的课程 默认查询最新课程
     * @param id 一级分类id
     * @return 课程列表
     */
    @GetMapping("/{id}")
    public String one(@PageableDefault(size=8,sort={"createTime"},direction = Sort.Direction.DESC)Pageable pageable, @PathVariable("id") Integer id,Model model){
        Page<Course> page=courseService.findCoursePageByOne(id,pageable);
        List<OneSubjectDto> subjects = oneSubjectService.findAllSubject();
        OneSubjectDto oneSubjectDto =oneSubjectService.findById(id);
        model.addAttribute("subjects",subjects);
        model.addAttribute("page",page);
        model.addAttribute("oneSubjectDto",oneSubjectDto);
        model.addAttribute("oneId",id);
        return "courseList";
    }
    /**
     * 查询二级分类下的课程 默认查询最新课程
     */
    @GetMapping("/{oneId}/{twoId}")
    public String two(@PageableDefault(size=8,sort={"createTime"},direction = Sort.Direction.DESC)Pageable pageable,@PathVariable("oneId") Integer oneId,@PathVariable("twoId") Integer twoId, Model model){
        Page<Course> page=courseService.findCoursePageByTwo(twoId,pageable);
        List<OneSubjectDto> subjects = oneSubjectService.findAllSubject();
        //路径导航
        OneSubjectDto oneSubjectDto =oneSubjectService.findById(oneId);
        TwoSubjectDto twoSubjectDto=twoSubjectService.findById(twoId);
        model.addAttribute("subjects",subjects);
        model.addAttribute("page",page);
        model.addAttribute("oneSubjectDto",oneSubjectDto);
        model.addAttribute("twoSubjectDto",twoSubjectDto);
        model.addAttribute("twoId",twoId);
        return "courseList";
    }
}
