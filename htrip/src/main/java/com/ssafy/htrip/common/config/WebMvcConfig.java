package com.ssafy.htrip.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.directory:uploads}")
    private String uploadDirectory;

    @Value("${file.upload.allowed-extensions:jpg,jpeg,png,gif}")
    private String allowedExtensionsString;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;

    private List<String> allowedExtensions;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드 디렉토리 초기화
        File uploadDir = new File(uploadDirectory);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 정적 리소스 핸들러 설정
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDirectory + "/");
    }

    @Bean
    public MultipartResolver multipartResolver() {
        // 허용된 확장자 초기화
        allowedExtensions = Arrays.asList(allowedExtensionsString.split(","));

        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver() {
            @Override
            public boolean isMultipart(HttpServletRequest request) {
                String method = request.getMethod().toLowerCase();
                if (!method.equals("post") && !method.equals("put")) {
                    return false;
                }

                String contentType = request.getContentType();
                return contentType != null && contentType.toLowerCase().startsWith("multipart/");
            }
        };

        return resolver;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        // 최대 파일 크기 설정
        factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));

        // 요청당 최대 크기 설정 (파일 여러 개의 총합)
        factory.setMaxRequestSize(DataSize.ofBytes(maxFileSize * 5));

        // 임시 저장 디렉토리 설정
        factory.setLocation(System.getProperty("java.io.tmpdir"));

        return factory.createMultipartConfig();
    }

    // 파일 유효성 검사를 위한 유틸리티 메서드
    public boolean isValidFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return false;
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return allowedExtensions.contains(extension);
    }

    // 파일 크기 검사를 위한 유틸리티 메서드
    public boolean isValidFileSize(long fileSize) {
        return fileSize <= maxFileSize;
    }
}