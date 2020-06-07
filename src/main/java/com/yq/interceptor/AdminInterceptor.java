package com.yq.interceptor;

import com.yq.entity.Role;
import com.yq.entity.User;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 管理员拦截器，拦截管理员路径下面的请求
 */
public class AdminInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        //判断是否是管理员
        if(user!=null){
            List<Role> roles = user.getRoles();
            for(Role r:roles){
                if("admin".equals(r.getRoleName())){
                    //是管理员
                    return true;
                }
            }
        }
        sendMessage(request,response,"Sorry，无权访问...");
        return false;
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
