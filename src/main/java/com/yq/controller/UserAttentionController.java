package com.yq.controller;

import com.yq.entity.User;
import com.yq.entity.dto.BType;
import com.yq.entity.dto.SimpleUserDto;
import com.yq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户关注
 */
@Controller
@RequestMapping("/user/attention")
public class UserAttentionController {

    @Autowired
    UserService userService;
    /**
     * 查询用户关注列表
     */
    @GetMapping("/findAll")
    public String findAll(HttpSession session, Model model){
        User user = (User) session.getAttribute("user");
        List<SimpleUserDto> list = userService.findUserAttention(user.getId());
        model.addAttribute("user",user);
        model.addAttribute("attentions",list);
        return "user/u-follow";
    }
    /**
     * 取消关注
     */
    @PostMapping("/delete")
    public String delete(Integer authId,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        Integer userId = user.getId();
        userService.attention(BType.ATTENTION_AUTH,authId,userId);
        //查询新数据返回
        List<SimpleUserDto> list = userService.findUserAttention(userId);
        model.addAttribute("attentions",list);
        return "user/u-follow::attentionList";
    }
}
