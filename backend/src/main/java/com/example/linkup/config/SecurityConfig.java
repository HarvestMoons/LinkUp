package com.example.linkup.config;

import com.example.linkup.service.CustomUserDetailsService;
import com.example.linkup.service.UserService;
import com.example.linkup.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final CustomUserDetailsService customUserDetailsService;
        private final PasswordEncoder passwordEncoder;

        public SecurityConfig(@Lazy CustomUserDetailsService customUserDetailsService,
                        @Lazy PasswordEncoder passwordEncoder) {
                this.customUserDetailsService = customUserDetailsService;
                this.passwordEncoder = passwordEncoder;
        }

        // 使用 Spring 自动注入的方式配置 AuthenticationManager
        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder
                                .userDetailsService(customUserDetailsService)
                                .passwordEncoder(passwordEncoder); // 使用密码编码器
                return authenticationManagerBuilder.build();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .formLogin().disable()
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/register", "/login").permitAll() // 注册和登录页面不需要认证
                                                .requestMatchers("/home").authenticated() // home 页面需要认证
                                                .anyRequest().permitAll() // 其他页面不受限制
                                )
                                /*
                                 * // 配置表单登录
                                 * .formLogin(form -> form
                                 * .loginPage("/login") // 登录页面 URL
                                 * .defaultSuccessUrl("/home", true) // 登录成功后的默认跳转 URL
                                 * .permitAll() // 允许所有用户访问登录页面
                                 * )
                                 * // 配置登出
                                 * .logout(logout -> logout
                                 * .logoutUrl("/logout") // 设置登出请求的 URL
                                 * .logoutSuccessUrl("/login") // 登出成功后跳转到登录页面
                                 * )
                                 */
                                // 禁用 CSRF（可以根据需要启用）
                                .csrf(AbstractHttpConfigurer::disable);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
