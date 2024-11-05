package com.dev.InstaMate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImageUploadService {

    @Value("${upload.path}") // 프로파일에 따라 달라지는 경로
    private String uploadPath;

    public Map<String, String> uploadImage(String base64Image) {
        // Base64 인코딩된 이미지 데이터에서 이미지 바이트 추출
        byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);

        // UUID 생성
        String uniqueValue = UUID.randomUUID().toString();
        
        // uniqueValue 디렉토리 생성
        Path directoryPath = Paths.get(uploadPath, uniqueValue);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath); // 디렉토리 생성
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패: " + e.getMessage());
            }
        }
        
        // 파일명 설정
        String fileName = "blog-image.png";
        Path filePath = directoryPath.resolve(fileName); // uniqueValue 하위에 파일 저장 경로 생성

        try {
            // 파일 저장
            Files.write(filePath, imageBytes);

            // 이미지 경로와 UUID 반환
            Map<String, String> response = new HashMap<>();
            response.put("filePath", "/administration/upload/" + uniqueValue + "/" + fileName); // 프론트엔드 접근 경로
            response.put("uniqueValue", uniqueValue); // UUID 반환
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }
    }

}