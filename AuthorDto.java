package com.sample.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
public class AuthorDto {
	
	private Long authorId;
	private String authorName;
	
	public Long getAuthorId() {
		return authorId;
	}
	
	public AuthorDto() {
		super();
	}

	public AuthorDto(Long authorId, String authorName) {
		super();
		this.authorId = authorId;
		this.authorName = authorName;
	}
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

}