package com.sample.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.sample.entity.Transaction;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public String generateReturnConfirmationEmail(Transaction t) {
        return "Dear " + t.getUser().getName() + ",\n\n" +
               "We have successfully processed the return of the following book:\n" +
               "- Book Title: " + t.getBook().getTitle() + "\n" +
               "- Borrowed Date: " + t.getBorrowDate() + "\n" +
               "- Return Date: " + t.getDueDate() + "\n\n" +
               "Thank you for using our library services.\n\n" +
               "Best regards,\nReadSphere Admin";
    }

    public String generateOverdueEmail(Transaction t,Long daysLate) {
        return "Dear " + t.getUser().getName() + ",\n\n" +
               "This is to inform you that we have processed the return of the following overdue book:\n" +
               "- Book Title: " + t.getBook().getTitle() + "\n" +
               "- Borrowed Date: " + t.getBorrowDate() + "\n" +
               "- Due Date: " + t.getDueDate() + "\n" +
               "- Return Date: " + t.getActualReturnDate() + "\n\n" +
               "A late fee of " + (daysLate*100) + " dollas has been applied to your account.\n\n" +
               "Please settle the fee as soon as possible.\n\n" +
               "Best regards,\nReadSphere Admin";
    }
    
    public String generateBorrowingConfirmationEmail(Transaction t) {
        return "Dear " + t.getUser().getName() + ",\n\n" +
               "We are pleased to confirm that you have successfully borrowed the following book from our library:\n" +
               "- Book Title: " + t.getBook().getTitle() + "\n" +
               "- Borrowed Date: " + t.getBorrowDate() + "\n" +
               "- Due Date: " + t.getDueDate() + "\n\n" +
               "Please ensure the book is returned by the due date to avoid any late fees.\n" +
               "Enjoy reading!\n\n" +
               "Best regards,\nReadSphere Admin";
    }
    
    public String generateBorrowCancellationEmail(Transaction t, String reason) {
        return "Dear " + t.getUser().getName() + ",\n\n" +
               "We regret to inform you that your request to borrow the following book has been canceled due to:\n" +
        		reason +".\n"+
               "- Book Title: " + t.getBook().getTitle() + "\n" +
               "If this cancellation was made in error, please contact the library staff for assistance.\n\n" +
               "Best regards,\nReadSphere Admin";
    }
    
    public String generateNearDueDateNotification(Transaction t) {
        return "Dear " + t.getUser().getName() + ",\n\n" +
               "This is a friendly reminder that the due date for the following book is approaching soon:\n" +
               "- Book Title: " + t.getBook().getTitle() + "\n" +
               "- Due Date: " + t.getDueDate()+ "\n\n" +
               "Please return the book on or before the due date to avoid any late fees.\n" +
               "Thank you for using our library services.\n\n" +
               "Best regards,\nReadSphere Admin";
    }
    
    public String generateOverdueNotification(Transaction t,Long daysLate) {
        return "Dear " + t.getUser().getName() + ",\n\n" +
               "Our records indicate that the following book is overdue:\n" +
               "- Book Title: " + t.getBook().getTitle() + "\n" +
               "- Due Date: " + t.getDueDate() + "\n\n" +
               "A late fee of " + (daysLate*100) + " dollas has been applied to your account.\n\n" +
               "Please return the book as soon as possible to avoid further charges.\n\n" +
               "Best regards,\nReadSphere Admin";
    }
}
