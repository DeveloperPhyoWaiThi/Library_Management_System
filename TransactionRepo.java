package com.sample.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sample.entity.Transaction;
import com.sample.entity.User;
import java.util.List;


public interface TransactionRepo extends JpaRepository<Transaction, Long>{

	public Transaction findByTransactionId(Long id);
	
	long countByBorrowStatus(String status);
	
	long countByAdminResponse(String adminResponse);
	
	Page<Transaction> findByUser(User user, Pageable pageable);
	
	@Query("SELECT t FROM Transaction t WHERE t.borrowStatus = :keyword AND t.adminResponse = :response")
	Page<Transaction> findByBorrowStatusAndAdminResponse(@Param("keyword") String keyword,@Param("response") String response,Pageable pageable);

	@Query("SELECT t FROM Transaction t WHERE t.adminResponse = :response")
	Page<Transaction> findByAdminResponse(@Param("response") String response, Pageable pageable);
	
	Page<Transaction> findByReturnRequest(Boolean returnRequest, Pageable pageable);
	
	@Query("SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId " +
	           "AND (t.adminResponse = 'pending' OR t.adminResponse = 'grant')")
	long countActiveTransactionsByUserId(@Param("userId") Long userId);
	
	long countByAdminResponseAndReturnRequest(String adminResponse, Boolean returnRequest);
}
