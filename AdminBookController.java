package com.sample.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.sample.dto.AuthorDto;
import com.sample.dto.CreateBookDto;
import com.sample.entity.Author;
import com.sample.entity.Book;
import com.sample.entity.Category;
import com.sample.entity.Gender;
import com.sample.entity.Transaction;
import com.sample.entity.User;
import com.sample.repository.BookRepository;
import com.sample.service.AuthorService;
import com.sample.service.BookService;
import com.sample.service.CategoryService;
import com.sample.service.TransactionService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class AdminBookController {
	
	@Autowired
	BookService bookService;
	
	@Autowired
	AuthorService authorService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	BookRepository bookRepo;
	
	@RequestMapping("/admin/booklist")
	public String showBookList(@RequestParam(defaultValue = "0",value = "page") int page,@RequestParam(value="status",required = false, defaultValue = "") String keyword, Model model, HttpSession session)
	{
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}
		
		Pageable pageable = PageRequest.of(page,5,Sort.by(Sort.Direction.DESC, "bookId"));
		Page<Book> books;
		
		if(!keyword.isEmpty() && !keyword.equals("all"))
		{
			if(keyword.equals("0"))
			{
				books= bookService.getBooksByStatus(false, pageable);
			}
			else
			{
				books= bookService.getBooksByStatus(true, pageable);
			}
			
		}
		else {
			books = bookService.getActiveBook(pageable);
		}
		
		if(books.isEmpty()) {
			model.addAttribute("emptyBooks", true);
		}
		else {
			model.addAttribute("books", books);
		}
		model.addAttribute("status", keyword);
		return "booklist";
	}
	
	
	@RequestMapping("/admin/bookActionForm")
	public String authorActionForm(@RequestParam(value="bookId", required=false) Long bookId, @RequestParam("mode") String mode, Model model, HttpSession session) {
		
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}
		
		Book book = null;
		String authorsString = null;
		List<Long> authorIds = null;
		List<String> authorNames=null;
		if(bookId != null) {
			
			book=bookService.getBookById(bookId);
				
			authorIds = bookRepo.findAuthorByBookId(bookId);
		    authorNames=authorService.getAuthorNamesByIds(authorIds);
		    authorsString = String.join(", ", authorNames);
		      
		}

	      List<AuthorDto> authorDtos= authorService.getAuthors().stream()
	                .map(author-> new AuthorDto(author.getAuthorId(), author.getAuthorName()))
	                .collect(Collectors.toList());

		  model.addAttribute("mode", mode);

		  if (mode.equals("detail")) 
		  { 
			model.addAttribute("book", book);
			model.addAttribute("authorsString", authorsString);
			if (book.getPublishedDate() != null) {
	              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	              String formattedDate = book.getPublishedDate().format(formatter);
	              model.addAttribute("formattedPublishedDate", formattedDate);
	          }
			 
		  }
		  else if(mode.equals("delete"))
		  { 
		    model.addAttribute("book", book);
			model.addAttribute("authorsString", authorsString);
			if (book.getPublishedDate() != null) {
	              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	              String formattedDate = book.getPublishedDate().format(formatter);
	              model.addAttribute("formattedPublishedDate", formattedDate);
	          }
		  }
		  else if(mode.equals("update")) {
			  
			  model.addAttribute("book", book);
			  model.addAttribute("authors", authorDtos);
			  model.addAttribute("authorIds", authorIds);
			  model.addAttribute("categories", categoryService.getAllCategories());
			  
			  if (book.getPublishedDate() != null) {
	              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	              String formattedDate = book.getPublishedDate().format(formatter);
	              model.addAttribute("formattedPublishedDate", formattedDate);
	          }
			
		  }
		  
		  
       else if (mode.equals("add")) {
          model.addAttribute("book", new Book());
          model.addAttribute("authors", authorDtos);
          model.addAttribute("categories", categoryService.getAllCategories());
      }
		 
		return "bookActionForm";
	}
	
	@RequestMapping("/admin/bookActions")
	public String authorActions(@RequestParam(value="bookId", required = false) Long bookId,
			@RequestParam("mode") String mode,
			@RequestParam(value="removedAuthors", required = false) String removedAuthors,
			@Valid @ModelAttribute("book") CreateBookDto book, BindingResult result,
			@RequestPart(value="image", required = false) MultipartFile img,
			Model model,
			HttpSession session,
			RedirectAttributes reAttributes) throws IOException
	{
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}

		Book book1 = null;
		List<Long> authorIds = null;
		List<String> authorNames=null;
		String imgName=null;
		
		List<AuthorDto> authorDtos= authorService.getAuthors().stream()
                .map(author-> new AuthorDto(author.getAuthorId(), author.getAuthorName()))
                .collect(Collectors.toList());
		
		if(bookId != null) {
			
			book1=bookService.getBookById(bookId);
			authorIds = bookRepo.findAuthorByBookId(bookId);
		    authorNames=authorService.getAuthorNamesByIds(authorIds);
		}

		if(mode.equals("delete"))
		{
			bookService.deleteBook(book1);
			reAttributes.addFlashAttribute("alertType", "alert-success");
		    reAttributes.addFlashAttribute("alertMessage", "The book is deleted.");
		}
		else if(mode.equals("update")) {
			
			if(result.hasErrors()) {
				if(book.getAuthorIds().isEmpty())
				{
					model.addAttribute("categoryErr", "Please choose category Id.");
					
				}
				if(book.getCategoryId() == null)
				{
					model.addAttribute("authorErr", "Please choose author Id.");
				}
				
				model.addAttribute("book", book1);
				model.addAttribute("mode", mode);
				model.addAttribute("authors", authorDtos);
				model.addAttribute("authorIds", authorIds);
				model.addAttribute("categories", categoryService.getAllCategories());
				return "bookActionForm";
			}
			else {
				if(img.isEmpty()) {
					imgName=book1.getImage();
				}
				
				bookService.addOrUpdateBook(book, img,imgName,removedAuthors,mode);
				reAttributes.addFlashAttribute("alertType", "alert-success");
			    reAttributes.addFlashAttribute("alertMessage", "The book is updated.");
			}
		}
		else if(mode.equals("add"))
		{
			if(result.hasErrors() || img.isEmpty()) {
				
				if(book.getAuthorIds().isEmpty())
				{
					model.addAttribute("authorErr", "Please choose author Id.");
					
				}
				if(book.getCategoryId() == null)
				{
					
					model.addAttribute("categoryErr", "Please choose category Id.");
					
				}
				
				model.addAttribute("mode", mode);
		        model.addAttribute("authors", authorDtos);
		        model.addAttribute("categories", categoryService.getAllCategories());
		        model.addAttribute("photoErr", "Please choose book cover.");
				return "bookActionForm";
			}
			else {
				
				bookService.addOrUpdateBook(book, img,imgName,removedAuthors,mode);
				reAttributes.addFlashAttribute("alertType", "alert-success");
			    reAttributes.addFlashAttribute("alertMessage", "The book is added.");
			}
		}
		return "redirect:/admin/booklist";
		
	}
	
}