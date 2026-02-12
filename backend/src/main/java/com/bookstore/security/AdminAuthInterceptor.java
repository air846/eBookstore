package com.bookstore.security;

import com.bookstore.common.BizException;
import com.bookstore.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Integer role = UserContext.getRole();
        if (role == null || role != 1) {
            throw new BizException(403, "无管理权限");
        }
        return true;
    }
}
