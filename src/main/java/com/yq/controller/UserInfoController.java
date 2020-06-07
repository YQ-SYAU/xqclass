package com.yq.controller;


import com.yq.entity.User;
import com.yq.entity.dto.UserDto;
import com.yq.service.CourseService;
import com.yq.service.OneSubjectService;
import com.yq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;


/**
 * 用户信息
 */
@Controller
@RequestMapping("/user/info")
public class UserInfoController {

    @Autowired
    UserService userService;
    @Autowired
    CourseService courseService;
    @Autowired
    OneSubjectService oneSubjectService;


    /**
     * 用户查找自己的信息
     */
    @GetMapping("/find")
    public String find(HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        if(user!=null){
            UserDto userDto = userService.findByUser(user.getId());
            model.addAttribute("userDto",userDto);
        }
        return "user/u-info";
    }
    /**
     * 修改用户信息，如果头像和收款码为空则代表不更新
     */
    @PostMapping("/update")
    public String update(MultipartFile avatarFile,MultipartFile codeFile,User user,HttpSession session){
        User sessionUser = (User) session.getAttribute("user");
        user.setId(sessionUser.getId());
        User newUser = userService.update(avatarFile, codeFile, user);
        session.setAttribute("user",newUser);
        return "redirect:/user/info/find";
    }
}
