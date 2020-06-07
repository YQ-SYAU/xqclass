package com.yq.controller.admin;

import com.yq.entity.dto.CourseDto;
import com.yq.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 后台课程管理
 */

@Controller
@RequestMapping("/admin/course")
public class CourseController {
    @Autowired
    CourseService courseService;

    /**
     *后台查看所有发布的课程
     */
    @GetMapping("/findAll")
    public String findAllPublish(Model model){
        List<CourseDto> courseDtoList = courseService.findAllPublish();
        model.addAttribute("courseDaoList",courseDtoList);
        return "admin/a-course";
    }
    /**
     * 后台删除课程
     */
    @PostMapping("delete")
    public String delete(Integer id,Model model){
        courseService.deleteById(id);
        //查询最新数据
        List<CourseDto> courseDaoList = courseService.findAllPublish();
        model.addAttribute("courseDaoList",courseDaoList);
        return "admin/a-course :: courseList";
    }
}
