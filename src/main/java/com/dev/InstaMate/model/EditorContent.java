package com.dev.InstaMate.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name="tb_editor")
public class EditorContent {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="EDITOR_ID")
    private Long id;

	@Lob
    @Column(name = "EDITOR_CONTENT", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name="EDITOR_TITLE")
    private String title; // 콘텐츠 제목 등 추가 정보가 필요한 경우
    
    @Column(name="EDITOR_DATE")
    private Date date;
    
    @Column(name="EDITOR_CLICKS")
    private int clicks;
    
    @Column(name="EDITOR_TAG")
    private String tag;
    
    @Column(name="EDITOR_AUTHOR")
    private String author;
    
    @Column(name="EDITOR_UNIQUE_VALUE")
    private String value;
    
    @Column(name="EDITOR_REP")
    private String imageUrl;
    
    @Transient
    private String comment;
}
