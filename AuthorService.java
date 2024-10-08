package com.sample.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sample.entity.Author;
import com.sample.entity.Book;
import com.sample.repository.AuthorRepo;
import com.sample.repository.BookRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthorService {

	@Autowired
	AuthorRepo authorRepo;
	
	@Autowired
	BookRepository bookRepo;
	
	public Long countAuthors() {
		return authorRepo.countByDelFlagFalse();
		
	}
	
	public List<Author> getAuthors()
	{
		return authorRepo.findAll();
	}
	
	
	public List<Author> findAllById(List<Long> authorId) {
		
		return authorRepo.findAllById(authorId);
	}
	
	public void createAuthor(Author a) {
		Author author=new Author();
		author.setAddress(a.getAddress());
		author.setAuthorName(a.getAuthorName());
		author.setBio(a.getBio());
		author.setDob(a.getDob());
		author.setEmail(a.getEmail());
		author.setGender(a.getGender());
		
		authorRepo.save(author);
	}
	
	public Page<Author> getAllAuthorsByPagination(Pageable page){
    	return authorRepo.findAll(page);
    }
	
	public Author getAuthorById(Long authorId) {
		return authorRepo.findById(authorId).orElseThrow();
	}
	
	@Transactional
	public void deleteAuthor(Author a) {
			
			a.setDelFlag(true);
			bookRepo.softDeleteBooksByAuthorId(a.getAuthorId());
			authorRepo.save(a);
			
			
	
	}
	
	public Page<Author> getActiveAuthors(Pageable pageable) {
        return authorRepo.findActiveAuthors(pageable);
    }
	
	public void updateAuthor(Long authorId,Author a) {
		Author author=authorRepo.findById(authorId).orElseThrow();
		author.setAddress(a.getAddress());
		author.setAuthorName(a.getAuthorName());
		author.setBio(a.getBio());
		author.setDob(a.getDob());
		author.setEmail(a.getEmail());
		author.setGender(a.getGender());
		
		authorRepo.save(author);
	}
	public List<String> getAuthorNamesByIds(List<Long> authorIds) {
        return authorRepo.findAuthorNamesByIds(authorIds);
    }
}
