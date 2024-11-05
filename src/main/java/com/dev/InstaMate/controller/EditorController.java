package com.dev.InstaMate.controller;

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
    public ResponseEntity<String> updateContent(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        String title = payload.get("title");
        String tag = payload.get("tag");
        String content = payload.get("content");

        editorContentService.updateContent(id, title, tag, content);

        return ResponseEntity.ok("수정이 완료되었습니다.");
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
    @PostMapping("/image/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestBody Map<String, String> request) {
        String base64Image = request.get("image");
        Map<String, String> response = imageUploadService.uploadImage(base64Image);
        return ResponseEntity.ok(response);
    }
}