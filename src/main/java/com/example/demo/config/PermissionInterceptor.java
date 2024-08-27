package com.example.demo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.error.PermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Override
    @Transactional
    // Khi query lấy role thì ko có permission do dùng fetch.lazy
    // cần query xuống database một lần nữa để lấy permission
    // @Transactional để vẫn giữ phiên đăng nhập, 2 lần query
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email != null && !email.isEmpty()) {
            User u = userService.handleGetUserByUsername(email);
            if (u != null) {
                Role r = u.getRole();
                if (r != null) {
                    List<Permission> permissions = r.getPermissions();
                    boolean isAllow = permissions.stream()
                            .anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    if (isAllow == false) {
                        throw new PermissionException("Bạn không có quyền truy cập");
                    }

                } else {
                    throw new PermissionException("Bạn không có quyền truy cập");
                }
            }
        }
        return true;
    }
}
