package com.sample.dto;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class UserLoginDto {
	
	@NotEmpty(message = "Please enter user name.")
	private String name;
	
	@NotEmpty(message = "Please enter email")
	@Email(message="Please enter email format correctly.")
	private String email;
	
	@NotEmpty(message="Please enter password")
	@Size(min=8, message="Password must be length of eight.")
	private String password;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
