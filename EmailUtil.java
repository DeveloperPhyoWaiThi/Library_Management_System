package com.sample.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;

@Component
public class EmailUtil {
	
	private String EMAIL_BODY="Please find you iternary attached";
	private String EMAIL_SUBJECT="Iternary for your transaction";
	
	@Autowired
	JavaMailSender sender;
	
	public void sendIternary(String toAddress,String filePath) {
		
		MimeMessage mm=sender.createMimeMessage();
		
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mm,true);
			helper.setTo(toAddress);
			helper.setSubject(EMAIL_SUBJECT);
			helper.setText(EMAIL_BODY);
			helper.addAttachment("Iternary", new File(filePath));
			sender.send(mm);
		} 
		catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
