package com.example.linkup.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${app.jwtSecret}") // 从配置文件注入 JWT 密钥
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}") // 从配置文件注入 Token 过期时间（毫秒）
    private int jwtExpirationMs;

    private Key key; // 加密密钥对象

    /**
     * 初始化方法：将字符串密钥转换为安全的 Key 对象
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * 生成 JWT Token
     *
     * @param authentication 用户认证信息
     * @return 生成的 Token 字符串
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // 设置用户名为 Token 主题
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // 设置过期时间
                .signWith(key, SignatureAlgorithm.HS512) // 使用 HS512 算法签名
                .compact();
    }

    /**
     * 验证 JWT Token 是否有效
     *
     * @param token JWT Token
     * @return true=有效，false=无效
     */
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setAllowedClockSkewSeconds(60) // 允许60秒的时间偏差
                    .setSigningKey(key) // 使用密钥验证签名
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            System.err.println("无效的 JWT 签名: " + e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            System.err.println("无效的 JWT 格式: " + e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            System.err.println("JWT 已过期: " + e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            System.err.println("不支持的 JWT 类型: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            System.err.println("JWT 参数错误: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 从 JWT Token 中提取用户名
     *
     * @param token JWT Token
     * @return 用户名（即 Token 主题）
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}