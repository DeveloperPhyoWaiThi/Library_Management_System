package com.sample.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sample.entity.Author;

public interface AuthorRepo extends JpaRepository<Author, Long> {

		Long countByDelFlagFalse();
	    
	    @Query("SELECT a FROM Author a WHERE a.delFlag = false")
	    Page<Author> findActiveAuthors(Pageable pageable);
        
	    @Query("SELECT a.authorName FROM Author a WHERE a.authorId IN :authorIds")
	    List<String> findAuthorNamesByIds(@Param("authorIds") List<Long> authorIds);
	

}
