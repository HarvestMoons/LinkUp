package com.example.linkup.controller;

import com.example.linkup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    /*
     * // 显示注册页面
     * 
     * @GetMapping("/register")
     * public String registerPage() {
     * return "register"; // 返回注册页面
     * }
     * 
     * // 显示登录页面
     * 
     * @GetMapping("/login")
     * public String loginPage() {
     * return "login"; // 返回登录页面
     * }
     * 
     * @GetMapping("/home")
     * public String home() {
     * return "home"; // 返回 home.html 视图
     * }
     */
    // 处理用户注册
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        userService.registerUser(username, password); // 调用服务层处理注册逻辑
        return "redirect:/login"; // 注册成功后跳转到登录页面
    }

}
