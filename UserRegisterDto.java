package com.sample.dto;

import org.springframework.web.multipart.MultipartFile;

import com.sample.entity.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRegisterDto {
	
	@NotEmpty(message = "Please enter user name")
	private String name;
	
	@NotEmpty(message = "Please enter email")
	@Email(message="Please enter email format correctly")
	private String email;
	
	@NotEmpty(message = "Please enter password")
	private String password;
	
	@NotEmpty(message="Please enter confirmation password")
	private String confirmPassword;
	
	@NotEmpty(message="Please enter phone number")
	private String phoneNo;
	
	@NotNull(message="Please select gender")
	private Gender gender;
	
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

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

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	
}
