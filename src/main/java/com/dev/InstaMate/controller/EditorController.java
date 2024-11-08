package com.dev.InstaMate.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.dev.InstaMate.model.EditorContent;
import com.dev.InstaMate.service.EditorContentService;
import com.dev.InstaMate.service.ImageUploadService;

@Controller
@RequestMapping("/admin/blog")
public class EditorController {

    private final EditorContentService editorContentService;
    private final ImageUploadService imageUploadService;

    public EditorController(EditorContentService editorContentService, ImageUploadService imageUploadService) {
        this.editorContentService = editorContentService;
        this.imageUploadService = imageUploadService;
    }

    // HTML 콘텐츠 저장
    @PostMapping("/save")
    public ResponseEntity<String> saveContent(@RequestBody Map<String, String> payload) {
        String title = payload.get("title");
        String tag = payload.get("tag");
        String content = payload.get("content");
        String value = payload.get("uniqueValue");

        editorContentService.saveContent(title, tag, content, value);

        return ResponseEntity.ok("저장이 완료되었습니다.");
    }

    @DeleteMapping("/delete-images/{id}")
    public ResponseEntity<Void> deleteExistingImages(@PathVariable Long id) {
        editorContentService.deleteImagesByBlogId(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateContent(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        String title = payload.get("title");
        String tag = payload.get("tag");
        String content = payload.get("content");

        // UUID 조회
        String uniqueValue = editorContentService.getUniqueValueById(id);

        // 업데이트된 콘텐츠 저장
        editorContentService.updateContent(id, title, tag, content);

        // UUID와 빈 이미지 경로 리스트 반환
        Map<String, Object> response = new HashMap<>();
        response.put("uniqueValue", uniqueValue);
        response.put("message", "수정이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/delete-unused-images/{id}")
    public ResponseEntity<String> deleteUnusedImages(@PathVariable Long id, @RequestBody Map<String, List<String>> payload) {
        List<String> usedImageNames = payload.get("serverImageNames");
        String uniqueValue = editorContentService.getUniqueValueById(id);
        editorContentService.deleteUnusedImages(uniqueValue, usedImageNames);
        return ResponseEntity.ok(uniqueValue);
    }

    
    @PostMapping("/upload-updated-images/{uuid}")
    public ResponseEntity<List<String>> uploadUpdatedImages(
            @PathVariable String uuid,
            @RequestParam("files") List<MultipartFile> files) {

        List<String> imagePaths = imageUploadService.uploadImagesWithUUID(uuid, files);
        return ResponseEntity.ok(imagePaths);
    }
    
    @GetMapping("/edit/{id}")
    public String editContent(@PathVariable Long id, Model model) {
        EditorContent content = editorContentService.getContent(id);
        
        // 데이터가 존재하지 않을 경우 예외 처리
        if (content == null) {
            throw new IllegalArgumentException("Content not found for id: " + id);
        }
        
        // 모델에 데이터 추가
        model.addAttribute("title", content.getTitle());
        model.addAttribute("tag", content.getTag());
        model.addAttribute("content", content.getContent());
        model.addAttribute("id", content.getId());
        
        return "administration/blog/blogUpdateForm";
    }
    
    // 특정 콘텐츠 불러오기
    @GetMapping("/content/{id}")
    public ResponseEntity<EditorContent> getContent(@PathVariable Long id) {
        EditorContent content = editorContentService.getContent(id);
        return ResponseEntity.ok(content);
    }
    
    // 이미지 업로드 요청 처리
    @PostMapping("/images/upload")
    public ResponseEntity<Map<String, Object>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        // 파일 목록이 비어있지 않을 경우 처리
        if (files != null && !files.isEmpty()) {
            // 한 번의 서비스 호출로 모든 이미지를 처리
            Map<String, Object> response = imageUploadService.uploadImages(files);
            return ResponseEntity.ok(response);
        } else {
            // 파일 목록이 없는 경우, 빈 응답 반환
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("uniqueValue", "");
            emptyResponse.put("imagePaths", Collections.emptyList());
            return ResponseEntity.ok(emptyResponse);
        }
    }

}