package com.yq.controller.admin;

import com.yq.entity.dto.UserDto;
import com.yq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 后台用户管理
 */
@Controller
@RequestMapping("/admin/user")
public class UserControlle {

    @Autowired
    UserService userService;
    /**
     * 查询所有用户
     */
    @GetMapping("/findAll")
    public String findAll(Model model){
        List<UserDto> users=userService.findAll();
        model.addAttribute("users",users);
        return "admin/a-user";
    }
}
