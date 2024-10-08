package com.sample.entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity

@AllArgsConstructor
@NoArgsConstructor
public class Author {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long authorId;
	
	@NotEmpty(message = "Please enter author name.")
	private String authorName;
	
	@NotEmpty(message="Please enter address.")
	private String address;
	
	@NotEmpty(message="Please enter email.")
	@Email(message = "Please enter email format correctly.")
	private String email;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Please enter date of birth.")
	private LocalDate dob;
	
	@NotEmpty(message = "Please enter short biography of author.")
	private String bio;
	
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Please enter gender of author.")
	private Gender gender;
	
	private boolean delFlag;
	
	public Long getAuthorId() {
		return authorId;
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



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public LocalDate getDob() {
		return dob;
	}



	public void setDob(LocalDate dob) {
		this.dob = dob;
	}



	public String getBio() {
		return bio;
	}



	public void setBio(String bio) {
		this.bio = bio;
	}



	public Gender getGender() {
		return gender;
	}



	public void setGender(Gender gender) {
		this.gender = gender;
	}



	public Set<Book> getBooks() {
		return books;
	}



	public void setBooks(Set<Book> books) {
		this.books = books;
	}



	@ManyToMany(mappedBy = "authors", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	    private Set<Book> books = new HashSet<>();

	public boolean isDelFlag() {
		return delFlag;
	}



	public void setDelFlag(boolean delFlag) {
		this.delFlag = delFlag;
	}
	
	
	
}
