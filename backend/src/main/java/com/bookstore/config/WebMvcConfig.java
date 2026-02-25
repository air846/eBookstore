package com.bookstore.config;

import com.bookstore.security.AdminAuthInterceptor;
import com.bookstore.security.JwtAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final AdminAuthInterceptor adminAuthInterceptor;

    @Value("${app.file-storage.base-dir:./uploads}")
    private String fileStorageBaseDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/mp/login",
                        "/api/user/register",
                        "/api/admin/login",
                        "/api/book/list",
                        "/api/book/detail/**",
                        "/api/category/tree",
                        "/api/carousel/list",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                );

        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path bookUploadDir = Paths.get(fileStorageBaseDir).toAbsolutePath().normalize().resolve("books");
        String location = bookUploadDir.toUri().toString();
        registry.addResourceHandler("/uploads/books/**")
                .addResourceLocations(location.endsWith("/") ? location : location + "/");
    }
}
