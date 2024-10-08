package com.sample.dto;

import java.util.Date;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.sample.entity.Author;
import com.sample.entity.Book;
import com.sample.entity.Category;
import com.sample.repository.AuthorRepo;
import com.sample.repository.BookRepository;
import com.sample.service.BookService;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookDto {
	
	private Long bookId;
	
	@NotEmpty(message = "Please enter title.")
	private String title;
	
//	@NotNull(message = "Please choose category Id.")
	private Long categoryId;

	@NotEmpty(message = "Please choose author.")
	private List<Long> authorIds;
	
	@NotEmpty(message = "Please enter book ISBN.")
	private String bookISBNNumber;
	
	private Integer price;
	
	@NotNull(message = "Please enter no of pages.")
	private Integer pages;
	
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	@NotNull(message = "Please enter published date.")
	private LocalDate publishedDate;
	
	@NotEmpty(message = "Please enter book description.")
	private String description;

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public List<Long> getAuthorIds() {
		return authorIds;
	}

	public void setAuthorIds(List<Long> authorIds) {
		this.authorIds = authorIds;
	}

	public String getBookISBNNumber() {
		return bookISBNNumber;
	}

	public void setBookISBNNumber(String bookISBNNumber) {
		this.bookISBNNumber = bookISBNNumber;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public LocalDate getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(LocalDate publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}