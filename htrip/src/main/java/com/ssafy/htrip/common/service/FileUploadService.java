package com.ssafy.htrip.common.service;

import com.ssafy.htrip.common.config.WebMvcConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadService {

    @Value("${file.upload.directory:uploads}")
    private String uploadDirectory;

    private final WebMvcConfig webMvcConfig;

    public FileUploadService(WebMvcConfig webMvcConfig) {
        this.webMvcConfig = webMvcConfig;
    }

    public String uploadFile(MultipartFile file, String subdirectory) throws IOException {
        validateFile(file);
        // 1. 오늘 날짜로 폴더 생성 (YYYY/MM/DD)
        LocalDate now = LocalDate.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uploadPath = uploadDirectory + "/" + subdirectory + "/" + datePath;

        // 2. 디렉토리 생성
        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 3. 파일명 생성 (UUID + 원본 확장자)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String storedFilename = UUID.randomUUID().toString() + extension;

        // 4. 파일 저장
        Path filePath = Paths.get(uploadPath, storedFilename);
        Files.write(filePath, file.getBytes());

        // 5. 상대 경로 반환 (웹에서 접근 가능한 경로)
        return subdirectory + "/" + datePath + "/" + storedFilename;
    }

    // 파일 유효성 검사 메서드
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // 파일 크기 검사
        if (!webMvcConfig.isValidFileSize(file.getSize())) {
            throw new IllegalArgumentException("파일 크기가 제한을 초과했습니다.");
        }

        // 파일 확장자 검사
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !webMvcConfig.isValidFileExtension(originalFilename)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDirectory, filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filePath, e);
        }
    }
}