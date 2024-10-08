package com.sample.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sample.dto.TransactionDto;
import com.sample.entity.Book;
import com.sample.entity.Transaction;
import com.sample.entity.User;
import com.sample.repository.TransactionRepo;

@Service
public class TransactionService {
	
	@Autowired
	TransactionRepo tRepo;
	
	public Transaction saveTransaction(TransactionDto t,User user, Book book) {
		
		Transaction tr=new Transaction();
		tr.setBorrowDate(t.getBorrowDate());
		tr.setBook(book);
		tr.setAdminResponse("Pending");
		tr.setUser(user);
	    tr.setReturnRequest(false);
		
		return tRepo.save(tr);
	}
	
	public Transaction getTransactionById(Long id) {
		return tRepo.findByTransactionId(id);
	}
	
	public Transaction changeStatus(String status,Transaction t) {
		t.setBorrowStatus(status);
		t.setReturnRequest(false);
		return tRepo.save(t);
	}
	
	public Long countTransactions() {
		return tRepo.countByAdminResponse("Grant");
	}
	
	public Long countCompleteTransactions() {
		return tRepo.countByBorrowStatus("Complete");
	}
	
	public Long countPendingTransacions() {
		return tRepo.countByBorrowStatus("Pending");
	}
	
	public Page<Transaction> getTransactionsByUser(User user, Pageable pageable) {
        return tRepo.findByUser(user, pageable);
    }
	
	public Page<Transaction> getAllTransactions(Pageable pageable){
		return tRepo.findByAdminResponse("Grant",pageable);
	}
	
	public Page<Transaction> getAllTransactionsByStatus(String keyword,String response,Pageable pageable)
	{
		return tRepo.findByBorrowStatusAndAdminResponse(keyword,response, pageable);
	}
	
	public Page<Transaction> getRequests(String response,Pageable pageable)
	{
		return tRepo.findByAdminResponse(response, pageable);
	}
	
	public void updateTransaction(String response,Transaction t)
	{
		LocalDate dueDate=t.getBorrowDate().plusDays(7);
		t.setDueDate(dueDate);
		t.setAdminResponse(response);
		t.setBorrowStatus("Pending");
		t.setTransactionId(t.getTransactionId());
		
		tRepo.save(t);
	}
	
	public void changeReturnRequest(Transaction t)
	{
		t.setReturnRequest(true);
		t.setActualReturnDate(LocalDate.now());
		t.setTransactionId(t.getTransactionId());
		
		tRepo.save(t);
	}
	
	public Page<Transaction> getReturnRequests(Pageable pageable)
	{
		return tRepo.findByReturnRequest(true, pageable);
	}
	
	public void borrowCancel(Transaction t)
	{
		t.setAdminResponse("Cancel");
		t.setTransactionId(t.getTransactionId());
		
		tRepo.save(t);
	}
	
	public long countActiveTransactions(Long userId) {
        return tRepo.countActiveTransactionsByUserId(userId);
    }
	
	public long countBorrowRequests()
	{
		return tRepo.countByAdminResponse("pending");
	}
	
	public long countReturnRequests()
	{
		return tRepo.countByAdminResponseAndReturnRequest("grant",true);
	}

}
