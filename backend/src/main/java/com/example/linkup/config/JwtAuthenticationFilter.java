package com.example.linkup.config;

import com.example.linkup.util.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, @Lazy UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/chatroom")) {
            filterChain.doFilter(request, response); // 放行 WebSocket 请求
            return;
        }
        try {
            // 1. 从请求头中提取 Token
            String jwt = parseJwt(request);

            // 2. 验证 Token 是否有效
            if (jwt != null) {
                try {
                    if (jwtUtils.validateJwtToken(jwt)) {
                        // 3. 从 Token 中提取用户名
                        String username = jwtUtils.getUsernameFromJwtToken(jwt);

                        // 4. 加载用户信息
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        // 5. 创建认证对象并设置到 SecurityContext 中
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (ExpiredJwtException ex) {
                    logger.warn("JWT 令牌已过期: {}");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 返回 401
                    response.getWriter().write("{\"error\": \"JWT 令牌已过期\"}");
                    return;
                }
            }
        } catch (Exception e) {
            logger.error("无法设置用户认证信息: {}", e);
        }

        // 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中解析 JWT Token
     *
     * @param request HTTP 请求对象
     * @return Token 字符串（无 "Bearer " 前缀）
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue(); // 找到 JWT Cookie 并返回
                }
            }
        }
        return null;
    }
}
