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
import com.sample.entity.Category;
import com.sample.entity.Gender;
import com.sample.entity.User;
import com.sample.service.CategoryService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AdminCategoryController {

	@Autowired
	CategoryService categoryService;
	
	@RequestMapping("/admin/categoryList")
	public String authorList(@RequestParam(defaultValue = "0",value="page") int page,Model model, HttpSession session)
	{
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}
		Pageable pageable = PageRequest.of(page,5,Sort.by(Sort.Direction.DESC, "categoryId"));

		Page<Category> categories=categoryService.getActiveCategorys(pageable);
		if(categories.isEmpty()) {
			model.addAttribute("emptyCategories", true);
		}
		else {
			model.addAttribute("categories", categories);
		}
		
		return "categorylist";
		
	}
	
	@RequestMapping("/admin/categoryActionForm")
	public String authorActionForm(@RequestParam(value="categoryId", required=false) Long categoryId, @RequestParam("mode") String mode, Model model, HttpSession session) {
		
		Category category= null;
			if(categoryId != null) {
				category=categoryService.findById(categoryId);
			}

		  model.addAttribute("mode", mode);
		  
		  if (mode.equals("detail")) 
		  { 
			  model.addAttribute("category", category); 
		  }
		  else if(mode.equals("delete"))
		  { 
			  model.addAttribute("category", category);
			  
		  }
		  else if(mode.equals("update")) {
			 
			  model.addAttribute("category", category);
			  
		  }
		  else if (mode.equals("add")) {
			  
			  model.addAttribute("category", new Category());
			  
		  }
		 
		return "category";
	}
	@RequestMapping("/admin/categoryActions")
	public String authorActions(@RequestParam(value="categoryId", required = false) Long categoryId,
			@RequestParam("mode") String mode,
			@Valid @ModelAttribute("category") Category category, BindingResult result,
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
		Category category1 = null;
		if(categoryId != null) {
			category1=categoryService.findById(categoryId);
		}

		if(mode.equals("delete"))
		{
			categoryService.deleteCategory(category1);
			reAttributes.addFlashAttribute("alertType", "alert-success");
		    reAttributes.addFlashAttribute("alertMessage", "The "+category1.getCategoryName()+" category is deleted.");
		}
		else if(mode.equals("update"))
		{
			categoryService.updateCategory(categoryId, category);
			reAttributes.addFlashAttribute("alertType", "alert-success");
		    reAttributes.addFlashAttribute("alertMessage", "The category data is updated.");
			
		}
		else if(mode.equals("add"))
		{
			if (result.hasErrors())
			{
				return "category";
			}
			else {
				Category c=categoryService.addCategory(category);
				if(c==null)
				{
					reAttributes.addFlashAttribute("alertType", "alert-danger");
				    reAttributes.addFlashAttribute("alertMessage", "The category '"+category.getCategoryName()+"' is already exists.");
				}
				else
				{
					
				    reAttributes.addFlashAttribute("alertType", "alert-success");
				    reAttributes.addFlashAttribute("alertMessage", "The new category '"+c.getCategoryName()+"' is added.");
				}
				
			}
		}
		return "redirect:/admin/categoryList";
		
	}
}
