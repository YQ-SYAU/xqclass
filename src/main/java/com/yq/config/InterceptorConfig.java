package com.yq.config;



import com.sun.org.apache.bcel.internal.generic.NEW;
import com.yq.interceptor.AdminInterceptor;
import com.yq.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    /**
     *添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns("/user/**","/course/comment");
        registry.addInterceptor(new AdminInterceptor()).addPathPatterns("/admin/**");
    }
}
