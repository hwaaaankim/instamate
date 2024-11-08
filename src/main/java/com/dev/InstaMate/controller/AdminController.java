package com.dev.InstaMate.controller;

import java.text.ParseException;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dev.InstaMate.model.Client;
import com.dev.InstaMate.repository.ClientRepository;
import com.dev.InstaMate.service.ClientService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	ClientRepository clientRepository;
	
	@Autowired
	ClientService clientService;
	
	@RequestMapping(value = {"/clientManager", "", "/"}, method = {RequestMethod.POST, RequestMethod.GET})
	public String clientManager(
			HttpServletRequest request, 
			Model model, 
			RedirectAttributes redirect,
			@PageableDefault(size = 10) Pageable pageable, 
			@RequestParam(required = false) String searchType,
			@RequestParam(required = false) String searchWord,
			@RequestParam(required = false) String startDate, 
			@RequestParam(required = false) String endDate
			)throws ParseException {
		Page<Client> clients = null;


        switch (searchType != null ? searchType : "none") {
            case "name":
                clients = (searchWord == null || searchWord.isEmpty()) ?
                        clientRepository.findByOrderByInquiryDateDesc(pageable) :
                        clientRepository.findByNameOrderByInquiryDateDesc(pageable, searchWord);
                break;
            case "phone":
                clients = (searchWord == null || searchWord.isEmpty()) ?
                        clientRepository.findByOrderByInquiryDateDesc(pageable) :
                        clientRepository.findByPhoneOrderByInquiryDateDesc(pageable, searchWord);
                break;
            case "email":
                clients = (searchWord == null || searchWord.isEmpty()) ?
                        clientRepository.findByOrderByInquiryDateDesc(pageable) :
                        clientRepository.findByEmailOrderByInquiryDateDesc(pageable, searchWord);
                break;
            case "period":
                clients = clientService.findByInquiryDate(pageable, startDate, endDate);
                break;
            default:
                clients = clientRepository.findByOrderByInquiryDateDesc(pageable);
        }

		int startPage = Math.max(1, clients.getPageable().getPageNumber() - 4);
		int endPage = Math.min(clients.getTotalPages(), clients.getPageable().getPageNumber() + 4);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("clients", clients);
		model.addAttribute("searchType", searchType);
		return "administration/client/clientManager";
	}
	
	@GetMapping("/clientDetail/{id}")
	public String clientDetail(
			@PathVariable Long id, 
			HttpServletRequest request, 
			RedirectAttributes redirect,
			Model model
			) {
		Client c = clientRepository.findById(id).get();
		clientService.changeSign(c);
		model.addAttribute("client", c);
		return "administration/client/clientManageF";
	}

	@PostMapping("/clientUpdate")
	@ResponseBody
	public String clientUpdate(
			Client client
			) throws ParseException {

		StringBuffer sb = new StringBuffer();
		String msg = "";

		try {
			clientService.clientUpdate(client);
			msg = "수정이 완료 되었습니다";
		} catch (Exception e) {
			msg = "에러가 발생하였습니다. 다시 시도 해 주세요";
		}

		sb.append("alert('" + msg + "');");
		sb.append("location.href='/admin/clientManager'");
		sb.append("</script>");
		sb.insert(0, "<script>");

		return sb.toString();
	}

	@GetMapping("/clientDelete/{id}")
	@ResponseBody
	public String clientDelete(
			Client client, 
			HttpServletRequest request, 
			RedirectAttributes redirect
			)
			throws ParseException {

		StringBuffer sb = new StringBuffer();
		String msg = "";

		try {
			clientRepository.delete(client);
			msg = "삭제가 완료 되었습니다";
		} catch (Exception e) {
			msg = "에러가 발생하였습니다. 다시 시도 해 주세요";
		}

		sb.append("alert('" + msg + "');");
		sb.append("location.href='/admin/clientManager'");
		sb.append("</script>");
		sb.insert(0, "<script>");

		return sb.toString();
	}
	
	@GetMapping("/blogInsertForm")
	public String blogInsertForm() {
		
		return "administration/blog/blogInsertForm";
	}
	
	@GetMapping("/blogManager")
	public String blogManager() {
		
		return "administration/blog/blogManager";
	}
}
