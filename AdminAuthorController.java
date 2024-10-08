package com.sample.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sample.entity.Author;
import com.sample.entity.Book;
import com.sample.entity.Gender;
import com.sample.entity.User;
import com.sample.repository.AuthorRepo;
import com.sample.service.AuthorService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AdminAuthorController {

	@Autowired
	AuthorService authorService;
	
	@RequestMapping("/admin/authorlist")
	public String authorList(@RequestParam(defaultValue = "0",value="page") int page,Model model,HttpSession session)
	{
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}
		Pageable pageable = PageRequest.of(page,5,Sort.by(Sort.Direction.DESC, "authorId"));
		Page<Author> authors=authorService.getActiveAuthors(pageable);
		if(authors.isEmpty()) {
			model.addAttribute("emptyAuthors", true);
		}
		else {
			model.addAttribute("authors", authors);
		}
		
		return "authorList";
		
	}
	
	@RequestMapping("/admin/authorActionForm")
	public String authorActionForm(@RequestParam(value="authorId", required=false) Long authorId, @RequestParam("mode") String mode, Model model, HttpSession session) {
		
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}
		Author author = null;
			if(authorId != null) {
				author=authorService.getAuthorById(authorId);
			}

		  model.addAttribute("mode", mode);
		  Gender[] genders = Gender.values();
		  model.addAttribute("genders", genders);
		  
		  if (mode.equals("detail")) 
		  { 
			  model.addAttribute("author", author); 
		  }
		  else if(mode.equals("delete"))
		  { 
			  model.addAttribute("author", author);
			  
		  }
		  else if(mode.equals("update")) {
			 
			  model.addAttribute("author", author);
			  
		  }
		  else if (mode.equals("add")) {
			  
			  model.addAttribute("author", new Author());
			  
		  }
		 
		return "author";
	}
	
	@RequestMapping("/admin/authorActions")
	public String authorActions(@RequestParam(value="authorId", required = false) Long authorId,
			@RequestParam("mode") String mode,
			@Valid @ModelAttribute("author") Author author1, BindingResult result,
			Model model,
			HttpSession session,
			RedirectAttributes reAttributes)
	{
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}
		model.addAttribute("mode", mode);
		Author author = null;
		if(authorId != null) {
			author=authorService.getAuthorById(authorId);
		}

		if(mode.equals("delete"))
		{
			authorService.deleteAuthor(author);
			reAttributes.addFlashAttribute("alertType", "alert-success");
		    reAttributes.addFlashAttribute("alertMessage", "The author data is deleted.");
		}
		else if(mode.equals("update"))
		{
			authorService.updateAuthor(authorId,author1);
			reAttributes.addFlashAttribute("alertType", "alert-success");
		    reAttributes.addFlashAttribute("alertMessage", "The author data is updated.");
		}
		else if(mode.equals("add"))
		{
			if (result.hasErrors())
			{
				Gender[] genders = Gender.values();
				model.addAttribute("genders", genders);
				
				return "author";
			}
			else {
			authorService.createAuthor(author1);
			reAttributes.addFlashAttribute("alertType", "alert-success");
		    reAttributes.addFlashAttribute("alertMessage", "The new author data is added.");
			}
		}
		return "redirect:/admin/authorlist";
		
	}
	
	
}
