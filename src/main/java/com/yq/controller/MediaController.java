package com.yq.controller;

import com.yq.entity.Media;
import com.yq.entity.User;
import com.yq.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


/**
 * 用户中心----媒资管理
 */
@Controller
@RequestMapping("/user/media")
public class MediaController {

    @Autowired
    MediaService mediaService;
    @GetMapping("/index")
    public String toUpload(HttpSession session,Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user",user);
        return "user/u-upload";
    }

    /**
     * 文件上传注册（检查文件是否已经上传，和上传的目录是否存在）
     * @param fileMd5 文件的md5值
     * @param fileName 文件名称
     * @param fileSize 分解文件大小
     * @param mimetype 文件类型
     * @param fileExt 文件后缀名
     */
    @PostMapping("/register")
    public void register(String fileMd5,String fileName,long fileSize,String mimetype,String fileExt,HttpServletResponse response,HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");

        boolean flag = mediaService.register(fileMd5, fileName, fileSize, mimetype, fileExt,user);
        response.getWriter().write(""+flag);
    }

    /**
     *检查分块文件是否存在
     */
    @PostMapping("/checkchunk")
    public void checkchunk(String fileMd5, Integer chunk, Integer chunkSize,HttpServletResponse response) throws IOException {
        boolean flag = mediaService.checkchunk(fileMd5, chunk, chunkSize);
        response.getWriter().write(flag+"");

    }
    /**
     * 上传分块
     */
    @PostMapping("/uploadchunk")
    public void uploadchunk(MultipartFile file, String fileMd5, Integer chunk,HttpServletResponse response) throws IOException {

        mediaService.uploadchunk(file,fileMd5,chunk);
        response.getWriter().write(true+"");
    }
    /**
     * 合并分块
     */
    @PostMapping("/mergechunks")
    public void mergechunks(String fileMd5, String fileName, long fileSize, String mimetype, String fileExt,HttpServletResponse response,HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
       boolean flag = mediaService.mergechunks(fileMd5, fileName, fileSize, mimetype, fileExt,user);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(flag+"");
    }

    /**
     * 查询用户上传的视频列表
     */
    @GetMapping("/findAll")
    public String findAll(HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        model.addAttribute("user",user);
        List<Media> mediaList=mediaService.findAll(user);
        model.addAttribute("mediaList",mediaList);
        return "user/u-mediaList";
    }
    /**
     * 根据id删除用户下的一个视频
     */
    @PostMapping("/delete")
    public String delete(Integer id,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        mediaService.deleteById(id,user.getId());
        //查询最新的数据
        List<Media> mediaList=mediaService.findAll(user);
        model.addAttribute("mediaList",mediaList);
        return "user/u-mediaList::mediaList";
    }
    /**
     * 删除全部视频
     */
    @PostMapping("/delAll")
    public String delAll(HttpSession session){
        User user = (User) session.getAttribute("user");
        mediaService.delAll(user);
        return "user/u-mediaList::mediaList";

    }

}
