package com.dev.InstaMate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dev.InstaMate.model.EditorContent;
import com.dev.InstaMate.repository.EditorContentRepository;

import jakarta.transaction.Transactional;

@Service
public class EditorContentService {

    private final EditorContentRepository editorContentRepository;
    
    @Value("${upload.path}") // 프로파일에 따라 달라지는 경로
    private String uploadPath;

    public EditorContentService(EditorContentRepository editorContentRepository) {
        this.editorContentRepository = editorContentRepository;
    }

    @Transactional
    public void updateContent(Long id, String title, String tag, String content) {
        EditorContent existingContent = editorContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        existingContent.setTitle(title);
        existingContent.setTag(tag);
        existingContent.setContent(content);

        editorContentRepository.save(existingContent);
    }

    public void deleteImagesByBlogId(Long id) {
        EditorContent content = editorContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        String uniqueValue = content.getValue();
        Path imageDirectory = Paths.get(uploadPath, uniqueValue);

        try {
            if (Files.exists(imageDirectory)) {
                Files.list(imageDirectory).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 삭제 중 오류 발생");
        }
    }
    
    @Transactional
    public EditorContent saveContent(String title, String tag, String content, String value) {
        EditorContent editorContent = new EditorContent();
        editorContent.setTitle(title);
        editorContent.setTag(tag);
        editorContent.setContent(content);
        editorContent.setDate(new Date());
        editorContent.setAuthor("INSTAMATE");
        editorContent.setClicks(0);
        editorContent.setValue(value);
        return editorContentRepository.save(editorContent);
    }

    public EditorContent getContent(Long id) {
        return editorContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found for id: " + id));
    }
}