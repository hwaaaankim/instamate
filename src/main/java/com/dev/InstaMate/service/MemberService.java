package com.dev.InstaMate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dev.InstaMate.model.Member;
import com.dev.InstaMate.repository.MemberRepository;

@Configuration
@Service
public class MemberService {

	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public Member insertAdmin(Member member) {
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPassword);
		member.setRole("ROLE_ADMIN");
		return memberRepository.save(member);

	}
}

