package com.yq.controller.admin;

import com.yq.config.MyConst;
import com.yq.entity.Banner;
import com.yq.entity.OneSubject;
import com.yq.entity.TwoSubject;
import com.yq.entity.dto.OneSubjectDto;
import com.yq.exception.CustomException;
import com.yq.service.BannerService;
import com.yq.service.OneSubjectService;
import com.yq.service.TwoSubjectService;
import com.yq.utils.UUIDUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.io.IOException;
import java.util.List;

/**
 * 首页管理模块
 */


@Controller("adminIndexController")
@RequestMapping("/admin/index")
public class IndexController {
    @Autowired
    BannerService bannerService;
    @Autowired
    OneSubjectService oneSubjectService;
    @Autowired
    TwoSubjectService twoSubjectService;

    //==================================分类=========================

    /**
     * 删除一级分类
     */
    @GetMapping("/delOneSubject/{id}")
    public String delOneSubject(@PathVariable("id")Integer id){
        oneSubjectService.delOneSubjectById(id);
        //重新查询数据
        return "redirect:/admin/index/findAllSubject";
    }

    /**
     * 删除二级分类
     */
    @GetMapping("/delTwoSubject/{id}")
    public String delTwoSubject(@PathVariable("id")Integer id){
        twoSubjectService.delTwoSubjectById(id);
        //重新查询数据
        return "redirect:/admin/index/findAllSubject";
    }


    /**
     * 保存一级分类
     */
    @PostMapping("/saveOneSubject")
    public String saveOneSubject(OneSubject oneSubject){
       oneSubjectService.saveOneSubject(oneSubject);
        //重新查询数据
        return "redirect:/admin/index/findAllSubject";
    }
    /**
     * 保存二级级分类
     */

    @PostMapping("/saveTwoSubject")
    public String saveSubject(TwoSubject twoSubject){
        twoSubjectService.saveTwoSubject(twoSubject);
        //为了防止重复提交表单，采用重定向
        return "redirect:/admin/index/findAllSubject";
    }

    /**
     * 查询所有分类
     */

    @GetMapping("/findAllSubject")
    public String findAllSubject(Model model){
        List<OneSubjectDto> subjects=oneSubjectService.findAllSubject();
        model.addAttribute("subjects",subjects);
        //      admin/a-category在拼接在类路径下的请求映射的后面  /admin/a-category全新的路径
        return "admin/a-category";
    }



    //==================================轮播图=========================
    @ApiOperation("添加轮播图")
    @PostMapping("/saveBanner")
    public String saveBanner(MultipartFile multipartFile, Banner banner, Model model) throws IOException {
        if(multipartFile!=null){
           //1.获取原文件名称
           String originalFilename = multipartFile.getOriginalFilename();
           //2.截取后缀名  .文件格式
           String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
            //判断课程封面是否图片类型的文件
            if(!(".jpeg".equals(extendName)||".jpg".equals(extendName)||".png".equals(extendName)||".gif".equals(extendName))){
                throw new CustomException("Sorry,轮播图只支持.jpg、.png、.gif格式文件");
            }
           //3.创建新文件名
           String fileName = UUIDUtils.getId()+extendName;
           //4.设置文件路径
           String imgPath = MyConst.F_SYSTEM+MyConst.IMG_PATH;
           File file = new File(imgPath);
           //5.判断文件夹是否存在，不存在则创建
           if(!file.exists()){
               //创建文件夹
               file.mkdirs();
           }
           //6.将文件写到指定位置
           multipartFile.transferTo(new File(file,fileName));
            //7.设置文件访问的路径
           banner.setUrl(MyConst.IMG_PATH+fileName);
           //8.保存
            bannerService.saveBanner(banner);

       }
        List<Banner> bannerList=bannerService.findAll();
        model.addAttribute("bannerList",bannerList);
        return "admin/a-banner :: Banners";
    }
    @ApiOperation("查询所有的轮播图")
    @GetMapping("/findAllBanner")
    public String findAll(Model model){
        List<Banner> bannerList=bannerService.findAll();
        model.addAttribute("bannerList",bannerList);
        return "admin/a-banner";
    }


    /**
     * 删除轮播图
     */
    @DeleteMapping("delBanner/{id}")
    public String delete(@PathVariable("id")Integer id){
        bannerService.deleteById(id);
        //重新查询一遍
        return "redirect:/admin/index/findAllBanner";
    }
}
