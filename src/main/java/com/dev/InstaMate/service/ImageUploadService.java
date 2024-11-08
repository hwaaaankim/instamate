package com.dev.InstaMate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dev.InstaMate.repository.EditorContentRepository;

@Service
public class ImageUploadService {

	@Autowired
	EditorContentRepository editorContentRepository;
	
	@Value("${upload.path}") // 프로파일에 따라 달라지는 경로
	private String uploadPath;

	/**
	 * 여러 개의 Base64 이미지를 받아서 처리하는 메서드
	 *
	 * @param base64Images Base64 형식의 이미지 리스트
	 * @return 이미지 경로 리스트와 UUID를 포함한 응답 데이터
	 */
	public Map<String, Object> uploadImages(List<MultipartFile> files) {
		// UUID 생성 (모든 이미지를 동일한 디렉토리에 저장)
		String uniqueValue = UUID.randomUUID().toString();
		Path directoryPath = Paths.get(uploadPath, uniqueValue);

		// uniqueValue 디렉토리 생성
		if (!Files.exists(directoryPath)) {
			try {
				Files.createDirectories(directoryPath);
			} catch (IOException e) {
				throw new RuntimeException("디렉토리 생성 실패: " + e.getMessage());
			}
		}

		// 응답 데이터를 저장할 리스트
		List<String> imagePaths = new ArrayList<>();

		// 파일 목록 순회
		for (MultipartFile file : files) {
			try {
				// 랜덤 파일명 생성
				String randomFileName = UUID.randomUUID().toString() + ".png";
				Path filePath = directoryPath.resolve(randomFileName);

				// 파일 저장
				Files.copy(file.getInputStream(), filePath);

				// 저장된 이미지 경로 추가 (프론트엔드에서 접근 가능한 URL 경로)
				String filePathUrl = "/administration/upload/" + uniqueValue + "/" + randomFileName;
				imagePaths.add(filePathUrl);

			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
			}
		}

		// 응답 데이터 생성
		Map<String, Object> response = new HashMap<>();
		response.put("uniqueValue", uniqueValue);
		response.put("imagePaths", imagePaths);

		return response;
	}

	public List<String> uploadImagesWithUUID(String uniqueValue, List<MultipartFile> files) {
		System.out.println("uploadImagesWithUUID");
	    List<String> imagePaths = new ArrayList<>();
	    Path directoryPath = Paths.get(uploadPath, uniqueValue);

	    // 디렉토리 생성
	    if (!Files.exists(directoryPath)) {
	        try {
	            Files.createDirectories(directoryPath);
	        } catch (IOException e) {
	            throw new RuntimeException("디렉토리 생성 실패: " + e.getMessage());
	        }
	    }

	    // 파일 저장
	    for (MultipartFile file : files) {
	        try {
	            String randomFileName = UUID.randomUUID().toString() + ".png";
	            Path filePath = directoryPath.resolve(randomFileName);
	            Files.copy(file.getInputStream(), filePath);

	            String filePathUrl = "/administration/upload/" + uniqueValue + "/" + randomFileName;
	            imagePaths.add(filePathUrl);

	        } catch (IOException e) {
	            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
	        }
	    }

	    return imagePaths;
	}

	
	/**
	 * 단일 이미지를 처리하는 메서드 (기존 메서드 유지)
	 *
	 * @param base64Image Base64 형식의 이미지
	 * @return 이미지 경로와 UUID를 포함한 응답 데이터
	 */
	public Map<String, String> uploadImage(String base64Image) {
		// Base64 인코딩된 이미지 데이터에서 이미지 바이트 추출
		byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);

		// UUID 생성
		String uniqueValue = UUID.randomUUID().toString();

		// uniqueValue 디렉토리 생성
		Path directoryPath = Paths.get(uploadPath, uniqueValue);
		if (!Files.exists(directoryPath)) {
			try {
				Files.createDirectories(directoryPath);
			} catch (IOException e) {
				throw new RuntimeException("디렉토리 생성 실패: " + e.getMessage());
			}
		}

		// 파일명 설정
		String fileName = "blog-image.png";
		Path filePath = directoryPath.resolve(fileName);

		try {
			// 파일 저장
			Files.write(filePath, imageBytes);

			// 이미지 경로와 UUID 반환
			Map<String, String> response = new HashMap<>();
			response.put("filePath", "/administration/upload/" + uniqueValue + "/" + fileName);
			response.put("uniqueValue", uniqueValue);
			return response;

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
		}
	}
}