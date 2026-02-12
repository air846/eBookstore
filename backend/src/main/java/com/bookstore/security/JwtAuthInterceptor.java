package com.bookstore.security;

import com.bookstore.common.BizException;
import com.bookstore.common.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
// 用户端 JWT 鉴权拦截器
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 允许 CORS 预检请求直接通过
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BizException(401, "未登录或token缺失");
        }
        try {
            // 解析 token 并写入上下文
            String token = authHeader.substring(7);
            Claims claims = jwtTokenProvider.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            Integer role = claims.get("role", Integer.class);
            UserContext.set(userId, role);
            return true;
        } catch (JwtException ex) {
            throw new BizException(401, "token无效或已过期");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
