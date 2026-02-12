package com.bookstore.security;

import com.bookstore.common.BizException;
import com.bookstore.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
// 管理端权限拦截器：仅管理员角色可访问
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 允许 CORS 预检请求
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        Integer role = UserContext.getRole();
        if (role == null || role != 1) {
            throw new BizException(403, "无管理权限");
        }
        return true;
    }
}
