package com.sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sample.entity.Author;
import com.sample.entity.Gender;
import com.sample.entity.Transaction;
import com.sample.entity.User;
import com.sample.service.TransactionService;
import com.sample.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AdminUserController {

	@Autowired
	UserService userService;
	
	@Autowired
	TransactionService tService;
	
	@RequestMapping("/admin/userlist")
	public String authorList(@RequestParam(defaultValue = "0",value="page") int page,Model model, HttpSession session)
	{
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}
		Pageable pageable = PageRequest.of(page,5,Sort.by(Sort.Direction.DESC, "userId"));
		Page<User> users=userService.getAllUsersByPagination(pageable);
		if(users.isEmpty()) {
			model.addAttribute("emptyUsers", true);
		}
		else {
			model.addAttribute("users", users);
		}
		
		return "userList";
		
	}
	
	@RequestMapping("/admin/userActionForm")
	public String userActionForm(@RequestParam(value="userId", required=false) Long userId, @RequestParam(value="mode", required = false) String mode,@RequestParam(defaultValue = "0",value="page") int page, Model model, HttpSession session) {

		//		to check authorization
		User user1=(User) session.getAttribute("user");
		if(user1 == null || !user1.getIsAdmin())
		{
			return "accessDenied";
		}
		
		User user = null;
			if(userId != null) {
				user=userService.findById(userId);
			}

		  model.addAttribute("mode", mode);
		  Gender[] genders = Gender.values();
		  model.addAttribute("genders", genders);
		  
		  if (mode.equals("detail")) 
		  { 
			  model.addAttribute("user", user); 
		  }
		  else if(mode.equals("delete"))
		  { 
			  model.addAttribute("user", user);
			  
		  }
		  else if(mode.equals("viewHistory")){
			  
			  Pageable pageable = PageRequest.of(page,5,Sort.by(Sort.Direction.DESC, "transactionId"));
			  Page<Transaction> transactions = tService.getTransactionsByUser(user, pageable);

			  model.addAttribute("transactionsPage", transactions);
			 
			  if(transactions.isEmpty()) {
					model.addAttribute("emptyTransaction", true);
				}
				else {
					model.addAttribute("transactionsPage", transactions);
					model.addAttribute("userId", user.getUserId());
				}
				
			  return "userBorrowHistory";
			  
			  
		  }
		  
		 
		return "user";
	}
	
	@RequestMapping("/admin/userActions")
	public String authorActions(@RequestParam(value="userId", required = false) Long userId,
			@RequestParam("mode") String mode,
			@Valid @ModelAttribute("user") User user, BindingResult result,
			Model model,
			HttpSession session,
			RedirectAttributes reAttributes)
	{
//		to check authorize
		User user2=(User) session.getAttribute("user");
		if(user2 == null || !user2.getIsAdmin())
		{
			return "accessDenied";
		}
		model.addAttribute("mode", mode);
		User user1 = null;
		if(userId != null) {
			user1=userService.findById(userId);
		}

		if(mode.equals("delete"))
		{
			userService.deleteUser(user1);
			reAttributes.addFlashAttribute("alertType", "alert-success");
		    reAttributes.addFlashAttribute("alertMessage", "The user '"+user1.getName()+ "' is deleted.");
		}
		
		return "redirect:/admin/userlist";
		
	}
}
