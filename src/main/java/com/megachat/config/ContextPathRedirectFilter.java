package com.megachat.config;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Context Path Redirect Filter - Xử lý redirect từ context path root
 * Đảm bảo khi truy cập /megachat sẽ redirect đến /megachat/index.html
 */
@Component
@Order(1) // Chạy trước AuthFilter
public class ContextPathRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String requestUri = request.getRequestURI();
        
        // Tính toán path sau context path
        String pathAfterContext = requestUri;
        if (!contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
            pathAfterContext = requestUri.substring(contextPath.length());
        }
        
        // Nếu path sau context path là empty hoặc chỉ có "/"
        // Ví dụ: /megachat hoặc /megachat/ sẽ có pathAfterContext là "" hoặc "/"
        if (pathAfterContext.isEmpty() || pathAfterContext.equals("/")) {
            // Redirect đến /megachat/index.html
            String redirectUrl = contextPath + "/index.html";
            // Sử dụng 302 redirect để đảm bảo browser follow redirect
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", redirectUrl);
            response.sendRedirect(redirectUrl);
            return;
        }
        
        // Tiếp tục filter chain
        filterChain.doFilter(request, response);
    }
}

