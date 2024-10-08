package com.sample.dto;

import java.time.LocalDate;

public class TransactionShowDto {

	private Long transactionId;
	
	private String userName;
	
	private String bookTitle;
	
	private String status;
	
	private LocalDate borrowDate;
	
	private LocalDate dueDate;
	
	private Boolean overDue;
	
	private Boolean nearDue;
	

	public Boolean getOverDue() {
		return overDue;
	}

	public void setOverDue(Boolean overDue) {
		this.overDue = overDue;
	}

	public Boolean getNearDue() {
		return nearDue;
	}

	public void setNearDue(Boolean nearDue) {
		this.nearDue = nearDue;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(LocalDate borrowDate) {
		this.borrowDate = borrowDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	
}
