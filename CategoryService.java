package com.sample.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sample.entity.Author;
import com.sample.entity.Category;
import com.sample.repository.BookRepository;
import com.sample.repository.CategoryRepo;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {
	
	@Autowired
	CategoryRepo categoryRepo;
	
	@Autowired
	BookRepository bookRepo;
	
	public Long countCategories() {
		return categoryRepo.countByDelFlagFalse();
	}
	
	public List<Category> getAllCategories(){
		return categoryRepo.findAll();
	}
	
	public Page<Category> getAllAuthorsByPagination(Pageable page){
    	return categoryRepo.findAll(page);
    }
	
	public Category findById(Long id) {
		
		Category category = categoryRepo.findByCategoryIdAndDelFlag(id, false);
		return category;
	}
	
	@Transactional
	public void deleteCategory(Category c) {
		c.setDelFlag(true);
		bookRepo.softDeleteBooksByCategoryId(c.getCategoryId());
		categoryRepo.save(c);
	}
	
	public Page<Category> getActiveCategorys(Pageable pageable) {
        return categoryRepo.findActiveCategorys(pageable);
    }
	
	public void updateCategory(Long categoryId,Category category) {
		Category c=categoryRepo.findById(categoryId).orElseThrow();
		
		c.setCategoryId(category.getCategoryId());
		c.setCategoryName(category.getCategoryName());
		
		categoryRepo.save(c);
		
	}
	
	public Category addCategory(Category category) {
		
		if(!categoryRepo.existsByCategoryName(category.getCategoryName()))
		{
			Category c=new Category();
			c.setCategoryId(category.getCategoryId());
			c.setCategoryName(category.getCategoryName());
			
			return categoryRepo.save(c);
		}
		
		return null;
	}
}
