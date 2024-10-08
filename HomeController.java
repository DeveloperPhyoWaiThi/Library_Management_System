package com.sample.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.sample.entity.Book;

import com.sample.service.BookService;

@Controller
public class HomeController {
	
	 @GetMapping("/user/about") 
	 public String about(Model model) { 
		 model.addAttribute("page", "about");
		 return "about"; 
		 }
	  
	 @GetMapping("/user/contact")
	 public String showContactForm(Model model) {
		 model.addAttribute("page", "contact");
         return "contact";
	    }

	  
}
