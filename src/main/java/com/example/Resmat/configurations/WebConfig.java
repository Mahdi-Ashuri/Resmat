package com.example.Resmat.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.Resmat.securite.AuthInterceptor;
import com.example.Resmat.services.PhotoService;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final PhotoService photoService;

    public WebConfig(AuthInterceptor authInterceptor,
                     PhotoService photoService) {

        this.authInterceptor = authInterceptor;
        this.photoService = photoService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/connexion",
                        "/inscription",
                        "/error",
                        "/favicon.ico",
                        "/css/**",
                        "/js/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/photos/**")
                .addResourceLocations(
                        photoService.getDossier()
                                .toUri()
                                .toString());
    }
}