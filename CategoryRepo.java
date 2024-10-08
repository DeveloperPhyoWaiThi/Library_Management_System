package com.sample.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sample.entity.Category;
import java.util.List;


public interface CategoryRepo extends JpaRepository<Category, Long>{
	
	Long countByDelFlagFalse();
	
	Category findByCategoryIdAndDelFlag(Long categoryId, boolean delFlag);
	
	@Query("SELECT c FROM Category c WHERE c.delFlag = false")
    Page<Category> findActiveCategorys(Pageable pageable);
	
	boolean existsByCategoryName(String name);
}
