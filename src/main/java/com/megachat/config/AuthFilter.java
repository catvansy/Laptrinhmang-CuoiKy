package com.megachat.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
        "/",
        "/index.html",
        "/landing.html",
        "/login.html",
        "/register.html",
        "/forgot-password.html",
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/forgot-password",
        "/api/auth/reset-password"
    );

    private static final Set<String> STATIC_EXTENSIONS = Set.of(
        ".js", ".css", ".png", ".jpg", ".jpeg", ".gif", ".svg",
        ".ico", ".woff", ".woff2", ".ttf", ".map", ".html"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String requestUri = request.getRequestURI();
        String path = requestUri.substring(contextPath.length());

        if (isPublicPath(path) || isStaticResource(path) || request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        boolean isLoggedIn = session != null && session.getAttribute("userId") != null;

        if (isLoggedIn) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Bạn cần đăng nhập\"}");
        } else {
            response.sendRedirect(contextPath + "/login.html");
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.contains(path);
    }

    private boolean isStaticResource(String path) {
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex == -1) {
            return false;
        }
        String extension = path.substring(dotIndex).toLowerCase();
        return STATIC_EXTENSIONS.contains(extension);
    }
}

