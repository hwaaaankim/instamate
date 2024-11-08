package com.dev.InstaMate.controller;


import org.apache.commons.codec.EncoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dev.InstaMate.model.Client;
import com.dev.InstaMate.model.EditorContent;
import com.dev.InstaMate.repository.EditorContentRepository;
import com.dev.InstaMate.service.ClientService;
import com.dev.InstaMate.service.EditorContentService;
import com.dev.InstaMate.service.SmsService;

@Controller
public class IndexController {

	@Autowired
	EditorContentService editorContentService;
	
	@Autowired
	EditorContentRepository editorContentRepository;
	
	@Autowired
	ClientService clientService;

	@Autowired
	SmsService smsService;
	
	@GetMapping({"", "/index", "/"})
	public String index() {
		return "front/index";
	}
	
	@RequestMapping(value = {"/blogList"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String blogList(
            Model model,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false, defaultValue = "ALL") String subject
    ) {
        // 서비스 레이어를 통해 블로그 데이터를 가져옴 (comment 필드에 <p> 태그 내용이 담겨 있음)
        Page<EditorContent> blogs = editorContentService.getAllBlogs(pageable, subject);

        int startPage = Math.max(1, blogs.getPageable().getPageNumber() - 4);
        int endPage = Math.min(blogs.getTotalPages(), blogs.getPageable().getPageNumber() + 4);
        
        model.addAttribute("subject", subject);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("blogs", blogs);
        return "front/blog/blogList";
    }
	
	@GetMapping("/blogDetail/{id}")
    public String blogDetail(@PathVariable Long id, Model model) {
        
		editorContentService.incrementClicks(id);
		
		EditorContent content = editorContentService.getContent(id);
	    Long previousId = editorContentService.getPreviousContentId(id);
	    Long nextId = editorContentService.getNextContentId(id);

	    model.addAttribute("content", content);
	    model.addAttribute("previousId", previousId);
	    model.addAttribute("nextId", nextId);
        
        return "front/blog/blogDetail";
    }

	
	@PostMapping("/clientInsert")
	@ResponseBody
	public String clientInsert(
			Client client,
			Model model
			) throws EncoderException {
		
		smsService.sendMessage("010-7615-7991", "인스타메이트 디비 들어왔습니다.");
		StringBuffer sb = new StringBuffer();
		String msg = "";
		
		if(clientService.clientInsert(client)) {
			msg = "お問い合わせを受け付け致しました。 確認次第ご連絡させていただきます！";
		}else {
			msg = "エラーが発生しました。ラインでのお問い合わせをご利用ください！";
		}
		
		sb.append("alert('" + msg + "');");
		sb.append("location.href='/index'");
		sb.append("</script>");
		sb.insert(0, "<script>");
		return sb.toString();
	}
}
