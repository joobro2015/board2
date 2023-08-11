package com.realboard.realstudy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Webconfig implements WebMvcConfigurer {
    private String resourcePath = "/upload/**"; // view 에서 접근할 경로
    private String savePath = "file:///C:/springboot_img/"; //실제 파일경로
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler(resourcePath)
                .addResourceLocations(savePath);
    }
    // 이렇게 처리해야 detail.html의 upload경로에 맞게 src접근가능

}
