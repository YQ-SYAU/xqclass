package com.yq.controller;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.oauth.Oauth;
import com.yq.entity.Course;
import com.yq.entity.Role;
import com.yq.entity.User;
import com.yq.entity.dto.IndexDto;
import com.yq.entity.dto.OneSubjectDto;
import com.yq.entity.dto.SimpleCourse;
import com.yq.entity.dto.UserDto;
import com.yq.exception.CustomException;
import com.yq.service.CourseService;
import com.yq.service.IndexService;
import com.yq.service.OneSubjectService;
import com.yq.service.UserService;
import com.yq.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 首页  和 归档
 */
@Controller
public class IndexController {

    @Autowired
    IndexService indexService;
    @Autowired
    OneSubjectService oneSubjectService;
    @Autowired
    CourseService courseService;
    @Autowired
    UserService userService;

    /**
     *查询首页数据   先从缓存查询
     */
    @GetMapping("/")
    public String index(Model model){
        IndexDto indexDto=indexService.index();
        model.addAttribute("indexDto",indexDto);
        return "index";
    }
    /**
     * 归档查询
     */
    @GetMapping("/archive")
    public String archive(HttpSession session,Model model){
        Map<String,List<SimpleCourse>> archiveMap=indexService.archive();
        Integer courseCount = indexService.countCourse();
        List<OneSubjectDto> subjects = oneSubjectService.findAllSubject();
        User user = (User) session.getAttribute("user");
        model.addAttribute("user",user);
        model.addAttribute("archiveMap",archiveMap);
        model.addAttribute("courseCount",courseCount);
        model.addAttribute("subjects",subjects);
        return "archive";
    }
    /**
     * 搜索
     */
    @PostMapping("/search")
    public String search(@PageableDefault(size=12,sort = {"createTime"},direction = Sort.Direction.DESC)Pageable pageable,
                         @RequestParam String query,Model model){
        List<OneSubjectDto> subjects = oneSubjectService.findAllSubject();
        Page<Course> page=courseService.searchCourse("%"+query+"%",pageable);
        model.addAttribute("page",page);
        model.addAttribute("subjects",subjects);
        model.addAttribute("query",query);
        model.addAttribute("courseCount",10);
        return "search";
    }

    /**
     * 查看他人信息
     */
    @GetMapping("/findOther/{uId}")
    public String findOther(@PathVariable("uId")Integer uId, HttpSession session, Model model){
        UserDto userDto = userService.findByUser(uId);
        List<Course> courses = courseService.findPublishByUId(uId);
        List<OneSubjectDto> subjects = oneSubjectService.findAllSubject();
        User user = (User) session.getAttribute("user");
        model.addAttribute("user",user);
        model.addAttribute("userDto",userDto);
        model.addAttribute("courses",courses);
        model.addAttribute("subjects",subjects);
        return "userInfo";
    }
    /**
     * 请求QQ登录
     */
    @GetMapping("/loginByQQ")
    public void loginByQQ(String uri,HttpServletRequest request,HttpServletResponse response,HttpSession session){
        response.setContentType("text/html;charset=utf-8");
        try {
            //保存URI
            session.setAttribute("URI",uri);
            //跳转到qq登录界面
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (QQConnectException|IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * QQ登录回调地址   获取用户信息
     *
     * @return
     */
    @RequestMapping("/callback")
    public String connection(HttpServletRequest request, HttpSession session) throws IOException {
        //获取URI
        String uri = (String) session.getAttribute("URI");
        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            String accessToken = null, openID = null;
            long tokenExpireIn = 0L;
            if ("".equals(accessTokenObj.getAccessToken())) {
                System.out.println("登录失败:没有获取到响应参数");
               // return "accessTokenObj=>" + accessTokenObj + "; accessToken" + accessTokenObj.getAccessToken();
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();

                request.getSession().setAttribute("demo_access_token", accessToken);
                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj = new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();

                //根据openID查找用户
                User user = userService.findByOpenId(openID);
               if(user!=null){
                    //判断是不是管理员
                   for(Role role:user.getRoles()){
                       if("admin".equals(role.getRoleName())){
                           //是管理员
                           user.setFlag(1);
                       }
                   }
                   //已存在，将用户信息放入session
                   session.setAttribute("user",user);
                    //return "redirect:/";
                   return "redirect:"+uri;
               }
                //获取QQ用户信息的url
                String userInfo_url="https://graph.qq.com/user/get_user_info?access_token="+accessToken+"&oauth_consumer_key=101870135&" +
                        "openid="+openID;
                //将获取的信息转为map
                Map<String,Object> map = HttpUtils.doGet(userInfo_url);
                Double ret = (Double) map.get("ret");
                if (ret==0) {
                    //获取成功
                    User save = userService.save(map, openID);
                    //存入session
                    session.setAttribute("user",save);
                } else {
                   throw new CustomException("很抱歉，我们没能正确获取到您的信息，原因是： "+map.get("msg"));
                }
            }
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
        //跳转到首页
        return "redirect:"+uri;
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        //把用户从session中干掉
        session.removeAttribute("user");
        return "redirect:/";
    }
    /**
     * 用户无权访问调到提示页面
     */
    @GetMapping("/msg")
    public String msg(HttpServletRequest request,Model model){
        List<OneSubjectDto> subjects = oneSubjectService.findAllSubject();
         model.addAttribute("subjects",subjects);
        String msg = (String) request.getAttribute("msg");
        model.addAttribute("msg",msg);
        return "msg";
    }

}
