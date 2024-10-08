package com.sample.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sample.dto.TransactionDto;
import com.sample.entity.Book;
import com.sample.entity.Transaction;
import com.sample.entity.User;
import com.sample.repository.UserRepo;
import com.sample.service.BookService;
import com.sample.service.CategoryService;
import com.sample.service.TransactionService;
import com.sample.service.UserService;
import com.sample.util.EmailUtil;
import com.sample.util.PDFGenerator;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class BookController {
	
		@Autowired
		CategoryService categoryService;
		
	 	@Autowired
	    private BookService bookService;
	 	
	 	@Autowired
	 	private TransactionService tService;
	 	
	 	@Autowired
	 	UserService userService;
	 	
	 	@Autowired
	 	PDFGenerator pdfGenerator;
	 	
	 	@Autowired
	 	EmailUtil emailUtil;

	 	@GetMapping("/")
		public String home(Model model,@RequestParam(defaultValue="",value="keyword", required = false) String keyword,
				                       @RequestParam(defaultValue = "0",value="page") int page,
				                       @RequestParam(value="categoryId", required = false) Long categoryId,
				                       HttpServletRequest request,
				                       HttpSession session) {
	 		Pageable pageable=PageRequest.of(page, 8);
	 		model.addAttribute("categoryId",categoryId);
	 		model.addAttribute("keyword", keyword);
	 		Page<Book> books;
	 		
	 		if (keyword.isEmpty() && categoryId == null) {
	 			
	 			books = bookService.getActiveBook(pageable);
	
	 		}
	 		else if(keyword.isEmpty() && categoryId != null)
	 		{
	 			if(categoryId == 0)
	 			{
	 				books = bookService.getActiveBook(pageable);
	 			}
	 			else
	 			{
	 				books=bookService.getBooksByCategory(categoryId, pageable);
	 			}
	 			
	 		}
	 		else if((!keyword.isEmpty() && categoryId == null)||(!keyword.isEmpty() && categoryId == 0) )
	 		{
	 			 books=bookService.searchBooks(keyword, pageable);
	 		}
	 		
	 		else
	 		{
	 			books=bookService.getBooksByKeywordAndCategory(categoryId, keyword, pageable);
	 		}
	 		
	 		if( books==null || books.isEmpty()) {
				model.addAttribute("emptyBooks", true);
			}
			else {
				
				model.addAttribute("books", books);
				
			}
	 		model.addAttribute("keyword", keyword);
	 		model.addAttribute("categories",categoryService.getAllCategories() );
	 		model.addAttribute("page", "home");

		    return "index";
		}
	 	
	    @GetMapping("/user/books/{bookId}")
	    public String getBookDetails(@PathVariable(value = "bookId") Long bookId, Model model, HttpSession session, RedirectAttributes reAttributes) {
	    	
	    	
	        Book book = bookService.getBookById(bookId);
	        model.addAttribute("book", book);
	        
	        return "book_detail"; 
	    }
	    
	    @RequestMapping("/user/borrow/{bookId}")
	    public String borrowDetails( @PathVariable(value="bookId") Long bookId, Model model, HttpSession session, RedirectAttributes reAttributes) {
	    	
	    	User user= (User) session.getAttribute("user");
			if (tService.countActiveTransactions(user.getUserId())==3 || user.getTransactionCounts()>3) {
				reAttributes.addFlashAttribute("alertType", "alert-danger");
			    reAttributes.addFlashAttribute("alertMessage", "Your borrowing limit is full.");
				return "redirect:/";
			}
	    	Book book = bookService.getBookById(bookId);
	        model.addAttribute("book", book);
	        
	        LocalDate today= LocalDate.now();
	        
	        model.addAttribute("today", today);
	        
	    	return "borrowBookDetails";
	    }
	    
	    @RequestMapping("/user/completeBorrowBook")
		public String completeBorrowBook(TransactionDto t,HttpSession session,ModelMap model, RedirectAttributes reAttributes) throws IOException {

			User user= userService.findById(t.getUserId());
	
 			Book book=bookService.getBookById(t.getBookId());
 
 			bookService.changeBookStatus(true, book);
			
		    Transaction tr=tService.saveTransaction(t, user, book);
	
			reAttributes.addFlashAttribute("alertType", "alert-success");
			reAttributes.addFlashAttribute("alertMessage", "Your request has been sent to the administrator. Please wait to be notified.");
			    
			return "redirect:/";
			
			
		}
	    
	    @RequestMapping("/user/returnBook")
	    public String returnBook() {
	    	return "returnBook";
	    }
	    
	    @RequestMapping("/user/completeReturnBook")
		public String completeReturnBook(@RequestParam(value="transactionId") Long t_id, ModelMap model,HttpSession session, RedirectAttributes reAttributes) {
			
	    	Transaction t=tService.getTransactionById(t_id);
	    	
			if(t==null || !t.getAdminResponse().equals("Grant")) {
				
				reAttributes.addFlashAttribute("alertType", "alert-danger");
			    reAttributes.addFlashAttribute("alertMessage", "Your transaction Id not found.");
				return "redirect:/user/returnBook";
			}
			
			if(t.getAdminResponse().equals("Grant") && t.getBorrowStatus().equals("Pending"))
	    	{
	    		tService.changeReturnRequest(t);
	    		User updatedUser = userService.decreaseTransactionCounts(t.getUser());
	    		session.setAttribute("user", updatedUser);
	    	}
			
			reAttributes.addFlashAttribute("alertType", "alert-success");
		    reAttributes.addFlashAttribute("alertMessage", "Your request has been sent to the system.");
			
			
			return "redirect:/";
		}
	    
	    
	    
}
