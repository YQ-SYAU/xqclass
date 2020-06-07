package com.yq.controller;

import com.yq.config.MyConst;
import com.yq.entity.Comment;
import com.yq.entity.User;
import com.yq.entity.dto.BType;
import com.yq.entity.dto.CourseDto;
import com.yq.entity.dto.OneSubjectDto;
import com.yq.service.CommentService;
import com.yq.service.CourseService;
import com.yq.service.OneSubjectService;
import com.yq.service.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 课程详情
 */
@Controller
@RequestMapping("/course")
public class CourseInfoController {

    @Autowired
    OneSubjectService oneSubjectService;
    @Autowired
    CourseService courseService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;

    /**
     * 课程资料下载    或在线打开（目前只能打开存放在项目中的文件）
     */
    @GetMapping("/download")
    public void download(String dataName,String dataPath,String openStyle ,HttpServletResponse response) throws Exception{

//        openStyle=openStyle==null?"attachment":openStyle;

        //获取文件输入流
        FileInputStream fileInputStream = new FileInputStream(MyConst.F_SYSTEM+dataPath);
        //附件下载(attachment) 附件打开（inline）  对文件名编码
        response.setHeader("content-disposition",openStyle+";fileName="+URLEncoder.encode(dataName,"UTF-8"));
        //获取响应输出流
        ServletOutputStream outputStream = response.getOutputStream();
        //文件拷贝
        IOUtils.copy(fileInputStream,outputStream);
        //关流
        IOUtils.closeQuietly(fileInputStream);
        IOUtils.closeQuietly(outputStream);
    }

    /**
     * 查询课程详细信息
     * id 课程id
     */
    @GetMapping("/info/{id}")
    public String courseInfo(@PathVariable("id")Integer id,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        CourseDto courseDto = courseService.findCourseInfo(id);
        //判断用户是否已经点赞、关注和收藏
        //获取课程作者
        Integer authId = courseDto.getUserDto().getId();
        if(user!=null){
            Integer userId=user.getId();
            boolean like = courseService.isLiked(String.valueOf(id),String.valueOf(userId));
            boolean attention = userService.isAttentioned(userId,authId);
            Boolean collect = userService.isCollectd(userId, id);
            model.addAttribute("like",like);
            model.addAttribute("attention",attention);
            model.addAttribute("collect",collect);
        }else{
            model.addAttribute("like",false);
            model.addAttribute("attention",false);
            model.addAttribute("collect",false);
        }
        //需要缓存分类信息
        List<OneSubjectDto> subjectDtos = oneSubjectService.findAllSubject();
        model.addAttribute("user",user);
        model.addAttribute("courseDto",courseDto);
        model.addAttribute("subjects",subjectDtos);
        List<Comment> comments = commentService.findAllByCId(id);
        model.addAttribute("comments",comments);
        return "courseInfo";
    }

    /**
     * 保存评论
     */
    @PostMapping("/comment")
    public String comment(Comment comment,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        commentService.save(comment,user.getId());
        //查询最新的评论内容
        Integer cId = comment.getCourse().getId();
        return "redirect:/course/comment/"+cId;
    }
    /**
     * 根据课程id   查询  评论列表
     */
    @GetMapping("/comment/{courseId}")
    public String findCommentsByCId(@PathVariable("courseId")Integer courseId,Model model){
        List<Comment> comments = commentService.findAllByCId(courseId);
        model.addAttribute("comments",comments);
        return "courseInfo :: commentList";
    }

    //=========================================点赞、关注、收藏================================
    /**
     * 对课程点赞/取消赞
     */
    @PostMapping("/like")
    public String like(Integer userId,Integer courseId,Model model){
        boolean like = courseService.like(BType.LIKED_COURSE, String.valueOf(userId), String.valueOf(courseId));
        //获取最新点赞数
        Integer countLike = courseService.countLike(BType.LIKED_COURSE, String.valueOf(courseId));
        model.addAttribute("like",!like);
        CourseDto courseDto = new CourseDto();
        courseDto.setLike(countLike);
        model.addAttribute("courseDto",courseDto);
        return "courseInfo::like";
    }

    /**
     * 关注和取关
     * @param userId 用户id
     * @param authId 作者id
     * @return
     */
    @PostMapping("/attention")
    public String attention(Integer authId,Integer userId,Integer courseId,Model model){
        //取关或关注
        boolean attention = userService.attention(BType.ATTENTION_AUTH,authId,userId);
        //查询新数据
        CourseDto courseDto = courseService.findCourseInfo(courseId);
        model.addAttribute("attention",!attention);
        model.addAttribute("courseDto",courseDto);
        return "courseInfo::auth";
    }
    /**
     * 收藏和取消收藏
     */
    @PostMapping("/collect")
    public String collect(Integer userId,Integer courseId,Model model){
        //收藏和取消收藏
        boolean collect = userService.collect(BType.COLLECT_COURSE,userId,courseId);
        model.addAttribute("collect",!collect);
        return "courseInfo::collect";
    }
}
