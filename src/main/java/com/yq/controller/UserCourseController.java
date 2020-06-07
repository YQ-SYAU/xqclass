package com.yq.controller;

import com.yq.config.MyConst;
import com.yq.entity.*;
import com.yq.entity.dto.CourseDto;
import com.yq.entity.dto.OneSubjectDto;
import com.yq.entity.dto.TwoSubjectDto;
import com.yq.exception.CustomException;
import com.yq.service.*;
import com.yq.utils.FileSizeUtil;
import com.yq.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 用户中心---课程管理
 */
@Controller
@RequestMapping("/user")
public class UserCourseController {

    @Autowired
    UserService userService;
    @Autowired
    OneSubjectService oneSubjectService;
    @Autowired
    CourseService courseService;
    @Autowired
    ChapterService chapterService;
    @Autowired
    VideoService videoService;
    @Autowired
    MediaService mediaService;

    //=====================================课程列表==========================

    /**
     * 根据课程id和用户id删除课程
     */
    @PostMapping("course/delById")
    public String delCourseById(Integer courseId,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        courseService.delCourseById(courseId,user.getId());
        //查询最新的数据
        List<CourseDto> courseList=courseService.findAllCourses(user);
        model.addAttribute("courseList",courseList);
        return "user/u-courseList :: courseList";
    }
    /**
     * 查询用户的所有课程(发布和未发布)
     */
    @GetMapping("/course/findAllCourses")
    public String findAllCourses(HttpSession session,Model model){
        User user= (User) session.getAttribute("user");
        List<CourseDto> courseList=courseService.findAllCourses(user);
        model.addAttribute("courseList",courseList);
        model.addAttribute("user",user);
        return "user/u-courseList";
    }

    //=====================================添加课程==========================
    /**
     * 添加小节
     */
    @PostMapping("/course/saveVideo")
    public String saveVideo(Video video,Integer courseId,Integer mediaId,HttpSession session){
        User user = (User) session.getAttribute("user");
        //为了安全，需要判断当前操作的用户是否有该课程
        boolean res = userService.hasCourse(user,courseId);
        if(!res){
            //没有该课程
            throw new CustomException("Sorry,你没有该课程");
        }
        videoService.saveVideo(video,mediaId);

        return "redirect:/user/course/findAll/"+courseId;
    }
    /**
     * 更新小节
     */
    @PostMapping("/course/updateVideo")
    public String updateVideo(Video video,Integer courseId,Integer mediaId,HttpSession session){
        User user = (User) session.getAttribute("user");
        //为了安全，需要判断当前操作的用户是否有该课程
        boolean res = userService.hasCourse(user,courseId);
        if(!res){
            //没有该课程
            throw new CustomException("Sorry,你没有该课程");
        }

        videoService.updateVideo(video,mediaId);

        return "redirect:/user/course/findAll/"+courseId;
    }
    /**
     * 删除小节
     */
    @GetMapping("/course/delVideo/{id}/{courseId}")
    public String delVideo(@PathVariable("id")Integer id,@PathVariable("courseId")Integer courseId,HttpSession session){
        User user = (User) session.getAttribute("user");
        //为了安全，需要判断当前操作的用户是否有该课程
        boolean res = userService.hasCourse(user,courseId);
        if(!res){
            //没有该课程
            throw new CustomException("Sorry,你没有该课程");
        }
        videoService.delById(id);
        return "redirect:/user/course/findAll/"+courseId;
    }
    /**
     * 删除章节
     */
    @GetMapping("/course/delChapter/{id}/{courseId}")
    public String delChapter(@PathVariable("id")Integer id,@PathVariable("courseId")Integer courseId,HttpSession session){
        User user = (User) session.getAttribute("user");
        //为了安全，需要判断当前操作的用户是否有该课程
        boolean res = userService.hasCourse(user,courseId);
        if(!res){
            //没有该课程
            throw new CustomException("Sorry,你没有该课程");
        }
        chapterService.delById(id);
        return "redirect:/user/course/findAll/"+courseId;
    }
    /**
     * 保存章节或更新章节
     */
    @PostMapping("course/saveChapter")
    public String saveChapter(Chapter chapter,HttpSession session,HttpServletRequest request){

        Integer courseId = chapter.getCourse().getId();
        request.setAttribute("courseId",courseId);
        chapterService.save(chapter);
        //请求转发会导致  刷新页面重复提交表单数据
        //重定向为get请求
        return "redirect:/user/course/findAll/"+courseId;
    }
    /**
     * 查询章节和小节信息  并且查询已经处理好的视频
     */
    @GetMapping("course/findAll/{courseId}")
    public String findAll(@PathVariable("courseId")Integer courseId,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        //为了安全，需要判断当前操作的用户是否有该课程
        boolean res = userService.hasCourse(user,courseId);
        if(!res){
            //没有该课程
            throw new CustomException("Sorry,你没有该课程");
        }
        List<Chapter> chapters=chapterService.findByCourseId(courseId);
        model.addAttribute("chapters",chapters);
        model.addAttribute("courseId",courseId);
        model.addAttribute("user",user);
        //查询用户已经处理好的视频列表
        List<Media> mediaList=mediaService.findAllOK(user);
        model.addAttribute("mediaList",mediaList);
        return "user/u-videoPublish2";
    }
    /**
     *查询分类信息  到  课程基础信息编辑页面
     */
    @GetMapping("course/findSubjects")
    public String findSubjects(HttpSession session,Model model)  {
        List<OneSubjectDto> oneSubjects = oneSubjectService.findAllSubject();
        //获取一级分类中的第一个的二级分类在前端显示
        if(oneSubjects!=null &&oneSubjects.size()>0){
            List<TwoSubjectDto> twoSubjects= oneSubjects.get(0).getTwoSubjects();
            model.addAttribute("twoSubjects",twoSubjects);
        }
        model.addAttribute("oneSubjects",oneSubjects);
        Course course = new Course();
        model.addAttribute("course",course);
        User user = (User) session.getAttribute("user");
        model.addAttribute("user",user);
        return "user/u-videoPublish1";
    }
    /**
     * 保存课程基本信息
     */
    @PostMapping("course/saveBaseInfo")
    public String saveBaseInfo(MultipartFile reqCoverFile, MultipartFile reqDataFile, Course course,HttpSession session) throws IOException {
        //保存课程封面
        if(!reqCoverFile.isEmpty()){
            String originalFilename = reqCoverFile.getOriginalFilename();
            String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
            //判断课程封面是否图片类型的文件
            if(!(".jpeg".equals(extendName)||".jpg".equals(extendName)||".png".equals(extendName)||".gif".equals(extendName))){
                throw new CustomException("Sorry,课程封面只支持.jpg、.png、.gif格式文件");
            }
            //获取文件名称
            String coverName = UUIDUtils.getId()+extendName;
            //文件目录
            String coverPath = MyConst.F_SYSTEM+MyConst.IMG_PATH;
            //目录不存在创建
            File coverFile = new File(coverPath);
            if(!coverFile.exists()){
                coverFile.mkdirs();
            }
            reqCoverFile.transferTo(new File(coverFile,coverName));
            course.setCover(MyConst.IMG_PATH+coverName);
            course.setCoverName(originalFilename);
        }
        //保存课程资料
       if(!reqDataFile.isEmpty()){
            //判断文件大小，总文件大小只支持1G
           long size = reqCoverFile.getSize();
           //此处为1G-5m
           if(size>=1073736704){
               throw new CustomException("Sorry,文件大小只能小于1个G");
           }

           String originalFilename1 = reqDataFile.getOriginalFilename();
           String extendName1 = originalFilename1.substring(originalFilename1.lastIndexOf("."));
           String dataName = UUIDUtils.getId()+extendName1;
           //获取当前项目在磁盘的路径  String dataPath =ResourceUtils.getURL("classpath:").getPath()+"/static"+MyConst.DATA_PATH;
           String dataPath = MyConst.F_SYSTEM+MyConst.DATA_PATH;
           File dataFile = new File(dataPath);
           if(!dataFile.exists()){
               dataFile.mkdirs();
           }
           reqDataFile.transferTo(new File(dataFile,dataName));
           course.setData(MyConst.DATA_PATH+dataName);
           course.setDataName(originalFilename1);
           course.setDataSize(FileSizeUtil.getSize(reqDataFile.getSize()));
       }

       //设置课程的作者
        User user = (User) session.getAttribute("user");
        course.setUser(user);
        //获取保存成功的课程id
        Integer courseId = courseService.saveCourse(course);
        //把课程id带到下一个页面

        return "redirect:/user/course/findAll/"+courseId;
    }
    //-------------------------------------上一把下一步模块----------------------------
    /**
     *  P2上一步   根据课程id和用户查找课程
     */
    @GetMapping("/course/findCourse/{id}")
    public String findCourseById(@PathVariable("id")Integer id,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        Course course = courseService.findByIdAndUser(id,user);
        List<OneSubjectDto> oneSubjects = oneSubjectService.findAllSubject();
        //获取一级分类中的第一个的二级分类在前端显示
       if(oneSubjects!=null &&oneSubjects.size()>0){
           List<TwoSubjectDto> twoSubjects= oneSubjects.get(0).getTwoSubjects();
           model.addAttribute("twoSubjects",twoSubjects);
       }
        model.addAttribute("oneSubjects",oneSubjects);
        model.addAttribute("course",course);
        model.addAttribute("user",user);
        return "user/u-videoPublish1";
    }
    /**
     * P2下一步 根据课程id查询课程信息————课程预览
     */
    @GetMapping("/course/findInfo/{id}")
    public String findInfo(@PathVariable("id") Integer courseId,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        //为了安全，需要判断当前操作的用户是否有该课程
        boolean res = userService.hasCourse(user,courseId);
        if(!res){
            //没有该课程
            throw new CustomException("Sorry,你没有该课程");
        }
        Course course = courseService.findById(courseId);
        model.addAttribute("course",course);
        model.addAttribute("user",user);
        return "user/u-videoPublish3";
    }
    /**
     * P3 发布课程
     */
    @GetMapping("/course/publish/{id}")
    public String publish(@PathVariable("id")Integer courseId,HttpSession session, RedirectAttributes redirectAttributes){
        User user = (User) session.getAttribute("user");
        //为了安全，需要判断当前操作的用户是否有该课程
        boolean res = userService.hasCourse(user,courseId);
        if(!res){
            //没有该课程
            throw new CustomException("Sorry,你没有该课程");
        }
        courseService.publish(courseId);
        //重定向携带数据
        redirectAttributes.addFlashAttribute("msg","发布课程成功");

        //重定向过去，不然到那个页面，删除该课程在刷新页面出错。
        return "redirect:/user/course/findAllCourses";
    }
}
