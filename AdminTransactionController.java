package com.sample.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sample.dto.TransactionDto;
import com.sample.dto.TransactionShowDto;
import com.sample.entity.Author;
import com.sample.entity.Book;
import com.sample.entity.Transaction;
import com.sample.entity.User;
import com.sample.service.BookService;
import com.sample.service.TransactionService;
import com.sample.service.UserService;
import com.sample.util.EmailService;
import com.sample.util.EmailUtil;
import com.sample.util.PDFGenerator;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminTransactionController {

	@Autowired
	TransactionService tService;
	
	@Autowired
	BookService bookService;
	
	@Autowired
	UserService userService;
	
	@Autowired
 	PDFGenerator pdfGenerator;
 	
 	@Autowired
 	EmailUtil emailUtil;
 	
 	@Autowired
 	EmailService emailService;
 	
 	
	
	@RequestMapping("/admin/borrowHistorylist")
	public String authorList(@RequestParam(value="page",defaultValue = "0") int page,@RequestParam(value="status",defaultValue = "") String keyword,Model model, HttpSession session)
	{
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}
		 Pageable pageable = PageRequest.of(page,5,Sort.by(Sort.Direction.DESC, "transactionId"));
		 Page<Transaction> transactions;
		 
		 if(!keyword.isEmpty() && !keyword.equals("all"))
		 {
			 transactions = tService.getAllTransactionsByStatus(keyword,"Grant", pageable);
		 }
		 else {
			 
			 transactions = tService.getAllTransactions(pageable);
			 
		 }
		 if(transactions.isEmpty()) 
		 {
			model.addAttribute("emptyTransaction", true);
		 }
		 else 
		 {
			 	List<TransactionShowDto> transactionDtos = new ArrayList<>();
			    
			    LocalDate today = LocalDate.now();
			    
			    for (Transaction transaction : transactions) {
			        TransactionShowDto dto = new TransactionShowDto();
			        dto.setTransactionId(transaction.getTransactionId());
			        dto.setUserName(transaction.getUser().getName());
			        dto.setBookTitle(transaction.getBook().getTitle());
			        dto.setBorrowDate(transaction.getBorrowDate());
			        dto.setDueDate(transaction.getDueDate());
			        dto.setStatus(transaction.getBorrowStatus());
			        
			        long daysBetween = ChronoUnit.DAYS.between(today, transaction.getDueDate());
			        boolean isOverdue = transaction.getDueDate().isBefore(today) && "Pending".equals(transaction.getBorrowStatus());
			        
			        
			        dto.setNearDue(daysBetween <= 2 && "Pending".equals(transaction.getBorrowStatus()));
			        dto.setOverDue(isOverdue);
			        transactionDtos.add(dto);
			    }
			model.addAttribute("transactions", transactionDtos);
		 }
		 model.addAttribute("status", keyword);
		 model.addAttribute("transactionsPage", transactions);
		 return "historyList";
		
	}
	
	@RequestMapping("/admin/requestList")
	public String borrowRequestList(@RequestParam(value="page",defaultValue = "0") int page, @RequestParam(value="keyword", required = false) String keyword,Model model, HttpSession session) 
	{
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}
		Pageable pageable = PageRequest.of(page,5,Sort.by(Sort.Direction.DESC, "transactionId"));
		Page<Transaction> transactions;
		
		if(keyword.equals("pending"))
		{
			transactions = tService.getRequests(keyword, pageable);
		}
		else
		{
			transactions = tService.getReturnRequests(pageable);
		}
				
		 if(transactions.isEmpty()) 
		 {
			model.addAttribute("emptyTransaction", true);
		 }
		 else 
		 {
			
			model.addAttribute("transactionsPage", transactions);
		 }
		 
		 model.addAttribute("status", keyword);
		 return "borrowRequestsList";
	}
	
	
	  @RequestMapping("/admin/requestAction") 
	  public String requestActions(@RequestParam(value="mode") String keyword, @RequestParam(value="transactionId") Long transactionId, RedirectAttributes reAttributes, @RequestParam(value="return",required=false) Boolean returnFlg, HttpSession session, Model model) throws IOException {
	  
		  Transaction t= tService.getTransactionById(transactionId);
		  Book book=bookService.getBookById(t.getBook().getBookId());
		  User user=t.getUser();
		  User updatedUser=new User();
		  long daysLate=0;
		  
		  if(keyword.equals("grant") && returnFlg != null) 
		  {
			  
				  Transaction t1=tService.changeStatus("Complete", t);

				  
				  bookService.changeBookStatus(false,book);
				  
				  LocalDate returnDate=t1.getActualReturnDate();
				  LocalDate dueDate=t1.getDueDate();

				  if(returnDate.isAfter(dueDate)) 
				  { 
					  daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
					  String overdueContent = emailService.generateOverdueEmail(t,daysLate);
					  emailService.sendEmail(t.getUser().getEmail(), "Overdue Book Return Notice", overdueContent);
				  }
				  else
				  {
					  String content = emailService.generateReturnConfirmationEmail(t);
					  emailService.sendEmail(t.getUser().getEmail(), "Book Return Confirmation", content);
				  }
				  
				  reAttributes.addFlashAttribute("alertType", "alert-success");
				  reAttributes.addFlashAttribute("alertMessage", "Notification email has been sent to the user.");
				  keyword="return";
				 
		  }
		  else if (keyword.equals("grant"))
		  {
				tService.updateTransaction("Grant",t);

				String filePath="C:/Users/GIC/Documents/transactions/transaction"+t.getTransactionId()+".pdf";
				
				pdfGenerator.generateItinerary(t, filePath);
				
				String content = emailService.generateBorrowingConfirmationEmail(t);
				emailService.sendEmail(t.getUser().getEmail(), "Book Borrow Confirmation", content);
				
				User user1=userService.changeTransactionCounts(user);

				reAttributes.addFlashAttribute("alertType", "alert-success");
			    reAttributes.addFlashAttribute("alertMessage", "Notification email has been sent to the user.");
			    keyword="pending";
		  } 
		  else if(keyword.equals("deny"))
		  {
			  model.addAttribute("transactionId", t.getTransactionId());
			  return "requestDeny";
		  }
		  
		  
		  return "redirect:/admin/requestList?keyword=" + keyword;
	  
	  }

	  
	  @RequestMapping("/admin/cancelRequest")
	  public String cancelRequest(@RequestParam("reason") String reason,@RequestParam("transactionId") Long id, RedirectAttributes reAttributes, HttpSession session)
	  {
		  Transaction t= tService.getTransactionById(id);
		  
		  String content = emailService.generateBorrowCancellationEmail(t, reason);
		  emailService.sendEmail(t.getUser().getEmail(), "Book Borrow Request Cancelation", content);
		  
		  bookService.changeBookStatus(false, t.getBook());
		  
		  tService.borrowCancel(t);
		  
		  reAttributes.addFlashAttribute("alertType", "alert-success");
		  reAttributes.addFlashAttribute("alertMessage", "Notification email has been sent to the user.");
		  
		  return "redirect:/admin/requestList?keyword=pending";
	  }
	  
	  @RequestMapping("/admin/notifyUser")
	  public String notifyUser(@RequestParam("transactionId") Long transactionId,@RequestParam("mode") String mode, RedirectAttributes reAttributes)
	  {
		  Transaction t= tService.getTransactionById(transactionId);
		  String message;
		  if(mode.equals("overDue"))
		  {
			  Long daysLate = ChronoUnit.DAYS.between(t.getDueDate(), LocalDate.now());
			  String content = emailService.generateOverdueEmail(t, daysLate);
			  emailService.sendEmail(t.getUser().getEmail(), "Overdue Book Notification", content);
		  }
		  else
		  {
			  String content = emailService.generateNearDueDateNotification(t);
			  emailService.sendEmail(t.getUser().getEmail(), "Overdue Book Notification", content);
		  }
		  
		  reAttributes.addFlashAttribute("alertType", "alert-success");
		  reAttributes.addFlashAttribute("alertMessage", "Notification email has been sent to the user.");
		  
		  return "redirect:/admin/borrowHistorylist";
	  }
	  
	 
}
