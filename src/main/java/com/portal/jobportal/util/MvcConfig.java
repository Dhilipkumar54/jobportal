package com.portal.jobportal.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = "photos";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory(UPLOAD_DIR,registry);
    }

    private void exposeDirectory(String uploadDir, ResourceHandlerRegistry registry) {
        //Converts String to path
        Path path = Paths.get(uploadDir);
        //Maps request with /photos/**
        registry.addResourceHandler("/"+uploadDir+"/**").addResourceLocations("file:"+path.toAbsolutePath()+"/");
    }
}
