package com.dev.InstaMate.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.InstaMate.model.EditorContent;

public interface EditorContentRepository extends JpaRepository<EditorContent, Long> {

	Page<EditorContent> findAll(Pageable pageble);

	Optional<EditorContent> findTopByIdLessThanOrderByIdDesc(Long id);

	Optional<EditorContent> findTopByIdGreaterThanOrderByIdAsc(Long id);

	@Query("SELECT e FROM EditorContent e WHERE :subject = 'ALL' OR e.tag = :subject ORDER BY e.id DESC")
	Page<EditorContent> findBySubject(Pageable pageable, @Param("subject") String subject);

}
