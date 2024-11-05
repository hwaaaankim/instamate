package com.dev.InstaMate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dev.InstaMate.model.EditorContent;
import com.dev.InstaMate.service.EditorContentService;

@Controller
public class IndexController {

	@Autowired
	EditorContentService editorContentService;
	
	@GetMapping({"", "/index", "/"})
	public String index() {
		return "front/index";
	}
	
	@GetMapping("/blogList")
	public String blogList() {
		return "front/blogList";
	}
	
	@GetMapping("/blogDetail/{id}")
    public String blogDetail(@PathVariable Long id, Model model) {
        
        EditorContent content = editorContentService.getContent(id);
        model.addAttribute("content", content);
        
        return "front/blog/blogDetail";
    }
}
