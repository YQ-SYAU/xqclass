package com.yq.exception;

import com.yq.entity.dto.OneSubjectDto;
import com.yq.service.OneSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 异常捕获类
 */
@ControllerAdvice
public class ExceptionCatch {

    @Autowired
    OneSubjectService oneSubjectService;

    @ExceptionHandler(Exception.class)
    public ModelAndView exception(Exception e){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("msg");
        //判断时系统异常还是自定义异常
        if(e instanceof CustomException){
            CustomException customException= (CustomException) e;
            mv.addObject("msg",customException.getMsg() );
        }else {
            mv.addObject("msg","系统出现了未知异常，请联系管理员。QQ：285038165");
            System.out.println("异常信息："+e.getMessage());
        }
        //查询分类
        List<OneSubjectDto> subjects = oneSubjectService.findAllSubject();
        mv.addObject("subjects",subjects);
        return mv;
    }
}
