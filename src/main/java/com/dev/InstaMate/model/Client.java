package com.dev.InstaMate.model;

import java.util.Date;

import org.springframework.lang.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name="tb_client")
@Entity
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="CLIENT_ID")
	private Long id;
	
	@Column(name="CLIENT_NAME")
	private String name;
	
	@Column(name="CLIENT_PHONE")
	private String phone;
	
	@Column(name="CLIENT_EMAIL")
	@Nullable
	private String email;
	
	@Column(name="CLIENT_LINE")
	private String line;
	
	@Column(name="CLIENT_COMMENT")
	@Nullable
	private String comment;
	
	@Column(name="CLIENT_INQUIRYDATE")
	private Date inquiryDate;
	
	@Column(name="CLIENT_CORRECTDATE")
	private Date correctDate;
	
	@Column(name="CLIENT_SIGN")
	private Boolean sign;
	
}






















