package com.example.onlinemusic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 在 @SpringBootApplication 中添加 (exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class}) 的原因:
 *     当启动类, 没有加这个过滤的时候不能进行登录!
 *     这是因为在 SpringBoot 中, 默认的 SpringSecurity 生效了(此时的接口都是被保护的), 我们需要通过验证才能正常的访问,
 *     此时通过上述配置即可禁用默认的登录验证.
 * 我们只是用到了依赖下面的 BCryptPasswordEncoder 类, 并没有用 SpringSecurity 框架!
 */
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class OnlineMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineMusicApplication.class, args);
    }

}
