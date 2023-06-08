package com.example.onlinemusic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-05-18
 * Time: 17:28
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 添加拦截器: 将自定义拦截器加入到系统配置
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置拦截器规则
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 拦截所有url
                .excludePathPatterns("/js/**.js")
                .excludePathPatterns("/images/**") // 不拦截 images 下所有的元素
                .excludePathPatterns("/css/**.css")
                .excludePathPatterns("/fronts/**")
                .excludePathPatterns("/player/**")
                .excludePathPatterns("/login.html") // 不拦截登录接口
                .excludePathPatterns("/user/login"); // 不拦截登录接口
    }
}
