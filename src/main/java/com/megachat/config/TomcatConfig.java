package com.megachat.config;

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tomcat Configuration - Cấu hình Tomcat embedded server
 * Xử lý welcome files và error pages
 */
@Configuration
public class TomcatConfig {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                // Thêm welcome file
                context.addWelcomeFile("index.html");
                context.addWelcomeFile("index.htm");
                
                // Cấu hình error page cho 404
                ErrorPage errorPage404 = new ErrorPage();
                errorPage404.setErrorCode(404);
                errorPage404.setLocation("/index.html");
                context.addErrorPage(errorPage404);
            }
        };
        return factory;
    }
}

