package com.sample.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.sample.dto.CreateBookDto;
import com.sample.entity.Author;
import com.sample.entity.Book;
import com.sample.entity.Category;
import com.sample.repository.BookRepository;

import jakarta.transaction.Transactional;

@Service
public class BookService {

	@Autowired
	BookRepository bookRepo;

	@Autowired
	AuthorService authorService;

	@Autowired
	CategoryService categoryService;

	public Book getBookById(Long bookId) {
		return bookRepo.findByBookIdAndDelFlag(bookId,false);
	}

	public Page<Book> getBooksByStatus(Boolean status,Pageable page) {
		return bookRepo.findByStatusAndDelFlag(status,false, page);
	}


	public Page<Book> getBooksByCategory(Long id, Pageable page)
	{
		Category category=categoryService.findById(id);
		if (category!= null)
		{
			return bookRepo.findByCategoryIdAndDelFlag(id,false, page);
		}
		return null;
		
	}
	
	public Page<Book> getBooksByKeywordAndCategory(Long id,String keyword, Pageable page)
	{
		return bookRepo.findBooksByKeywordAndCategory(keyword, id,false,page);
	}
	
	public Page<Book> searchBooks(String keyword, Pageable page) {
		return bookRepo.searchBooks(keyword,false, page);
	}

	public void changeBookStatus(Boolean status,Book book) {
		book.setStatus(status);
		bookRepo.save(book);
	}

	public Long countBooks() {		
	    return bookRepo.countByDelFlagFalse();
	}

	public Long countBorrowedBooks() {
	    return bookRepo.countByStatusTrueAndDelFlagFalse();
	}

	public Long countRemainingBooks() {
	    return bookRepo.countByStatusFalseAndDelFlagFalse();
	}

	public void deleteBook(Book book) {
		book.setDelFlag(true);
		bookRepo.save(book);
	}
	
	public Page<Book> getActiveBook(Pageable pageable) {
        return bookRepo.findActiveBooks(pageable);
    }

	
	public void addOrUpdateBook(CreateBookDto bookDto, MultipartFile img,String imgName, String removedAuthors, String mode) throws IOException {

		if(!img.isEmpty()) 
		{
			String uploadDir = "C:/Users/GIC/Downloads/Library-Management-System-Zip/Library-Management-System/uploads/";

			File directory = new File(uploadDir);

			imgName = img.getOriginalFilename();
			File upl = new File(directory, imgName);
			upl.createNewFile();

			try (FileOutputStream fout = new FileOutputStream(upl);
					BufferedOutputStream stream = new BufferedOutputStream(fout)) {
				stream.write(img.getBytes());
			}
		}
		
		Book book;
		if(mode.equals("add"))
		{
			 book = new Book();
		}
		else
		{
			book=bookRepo.findById(bookDto.getBookId()).orElseThrow();
		}
		
		if (removedAuthors != null && !removedAuthors.isEmpty()) 
		{
	        List<Long> removedAuthorIds = Arrays.stream(removedAuthors.split(","))
	                                             .map(Long::valueOf)
	                                             .collect(Collectors.toList());
	        
			 for(Long id:removedAuthorIds) {
				 bookRepo.deleteAuthor(book.getBookId(), id);
			 }
	        
	    }
		

		List<Author> authors = authorService.findAllById(bookDto.getAuthorIds());
		Category category = categoryService.findById(bookDto.getCategoryId());

		
		for (Author a : authors) {

			if (!book.getAuthors().contains(a)) {
		        book.getAuthors().add(a);
		    }
		}

		book.setBookId(bookDto.getBookId());
		book.setTitle(bookDto.getTitle());
		book.setBookISBNNumber(bookDto.getBookISBNNumber());
		book.setPrice(bookDto.getPrice());
		book.setStatus(false);
		book.setPages(bookDto.getPages());
		book.setPublishedDate(bookDto.getPublishedDate());
		book.setDescription(bookDto.getDescription());
		book.setImage(imgName);
		book.setCategory(category);

		bookRepo.save(book);

	}

	

}
