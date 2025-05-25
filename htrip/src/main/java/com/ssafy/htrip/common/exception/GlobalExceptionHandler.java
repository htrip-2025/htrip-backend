package com.ssafy.htrip.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        log.error("파일 크기 초과: {}", ex.getMessage());

        Map<String, String> response = new HashMap<>();
        response.put("message", "파일 크기가 제한을 초과했습니다.");

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, String>> handleMultipartException(MultipartException ex) {
        log.error("파일 업로드 오류: {}", ex.getMessage());

        Map<String, String> response = new HashMap<>();
        response.put("message", "파일 업로드 중 오류가 발생했습니다.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}