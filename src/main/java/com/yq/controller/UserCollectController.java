package com.yq.controller;

import com.yq.entity.User;
import com.yq.entity.dto.BType;
import com.yq.entity.dto.CollectCourse;
import com.yq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Map;


/**
 * 用户收藏
 */
@Controller
@RequestMapping("/user/collect")
public class UserCollectController {

    @Autowired
    UserService userService;
    /**
     * 查询用户收藏的所有课程
     */
    @GetMapping("/findAll")
    public String findAll(HttpSession session, Model model){
        User user = (User) session.getAttribute("user");
        if(user==null){
            return "u-collect";
        }
        Map<Integer,CollectCourse> courses=userService.findUserCollectCourses(user.getId());
        model.addAttribute("courses",courses);
        model.addAttribute("user",user);
        return "user/u-collect";
    }
    /**
     * 取消收藏
     */
    @PostMapping("/delete")
    public String delete(Integer courseId,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        Integer userId=user.getId();
        userService.deleteCollect(BType.COLLECT_COURSE,userId,courseId);
        //查询最新数据返回
        Map<Integer,CollectCourse> courses=userService.findUserCollectCourses(userId);
        model.addAttribute("courses",courses);
        return "user/u-collect::collectList";
    }
}
