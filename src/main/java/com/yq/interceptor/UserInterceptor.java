package com.yq.interceptor;

import com.yq.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 用户拦截器（本质Aop思想），用于拦截需要用户登录的一些请求
 * 无论是过滤器还是拦截器都属于AOP（面向切面编程）思想的具体实现
 *
 * JDK1.8以后，接口是可以写方法的实现
 */
public class UserInterceptor extends HandlerInterceptorAdapter {

    //控制器执行之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从session中获取用户
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user==null){
            //用户未登录
            sendMessage(request,response,"未登录,无法访问...");
            return false;
        }
        return true;
    }

    /**
     * 响应数据给前端
     */
    public static void sendMessage(HttpServletRequest request,HttpServletResponse response, String msg) throws IOException, ServletException {
        request.setAttribute("msg",msg);
        request.getRequestDispatcher("/msg").forward(request,response);
        //response.sendRedirect("/msg");
    }
}
