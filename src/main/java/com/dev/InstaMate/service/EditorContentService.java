package com.dev.InstaMate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dev.InstaMate.model.EditorContent;
import com.dev.InstaMate.repository.EditorContentRepository;

import jakarta.transaction.Transactional;

@Service
public class EditorContentService {

    private final EditorContentRepository editorContentRepository;
    
    @Value("${upload.path}") // 프로파일에 따라 달라지는 경로
    private String uploadPath;

    public Long getPreviousContentId(Long currentId) {
        return editorContentRepository.findTopByIdLessThanOrderByIdDesc(currentId).map(EditorContent::getId).orElse(null);
    }

    public Long getNextContentId(Long currentId) {
        return editorContentRepository.findTopByIdGreaterThanOrderByIdAsc(currentId).map(EditorContent::getId).orElse(null);
    }
    
    public EditorContentService(EditorContentRepository editorContentRepository) {
        this.editorContentRepository = editorContentRepository;
    }

    public void incrementClicks(Long id) {
        EditorContent content = editorContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content ID: " + id));
        content.setClicks(content.getClicks() + 1);
        editorContentRepository.save(content);
    }
    
    public Page<EditorContent> getAllBlogs(Pageable pageable, String subject) {
        // 데이터베이스에서 모든 블로그 조회
        Page<EditorContent> blogs = editorContentRepository.findBySubject(pageable, subject);

        // 각 블로그의 content에서 <p> 태그 텍스트 추출
        blogs.forEach(blog -> {
            String extractedText = extractTextFromPTags(blog.getContent());
            blog.setComment(extractedText);
        });

        return blogs;
    }

    // <p> 태그 안의 텍스트를 추출하는 메서드
    private String extractTextFromPTags(String htmlContent) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return "";
        }

        Document document = Jsoup.parse(htmlContent);
        Elements pTags = document.select("p");
        StringBuilder extractedText = new StringBuilder();

        for (Element pTag : pTags) {
            if (!pTag.text().trim().isEmpty()) {
                extractedText.append(pTag.text()).append("<br>");
            }
        }

        return extractedText.toString().trim();
    }


    
    public void deleteUnusedImages(String uniqueValue, List<String> usedImageNames) {
        Path directoryPath = Paths.get(uploadPath, uniqueValue);

        try {
            if (Files.exists(directoryPath)) {
                Files.list(directoryPath).forEach(path -> {
                    String fileName = path.getFileName().toString();
                    if (!usedImageNames.contains(fileName)) {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 삭제 중 오류 발생");
        }
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
    
	
	public String getUniqueValueById(Long id) {
	    EditorContent content = editorContentRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Content not found"));
	    return content.getValue();
	}

}