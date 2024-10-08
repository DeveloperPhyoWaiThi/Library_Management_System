package com.sample.controller;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class OtpGenerator {

	private static final SecureRandom random = new SecureRandom();

    public static String generateOTP() {
    	
        // Generates a random integer between 0 and 999999
        int otp = random.nextInt(1000000);
        // Format the number as a 6-digit string with leading zeroes if necessary
        return String.format("%06d", otp);
    }
}
