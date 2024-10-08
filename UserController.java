package com.sample.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sample.dto.UserLoginDto;
import com.sample.dto.UserRegisterDto;
import com.sample.entity.Gender;
import com.sample.entity.Transaction;
import com.sample.entity.User;
import com.sample.repository.UserRepo;
import com.sample.service.AuthorService;
import com.sample.service.BookService;
import com.sample.service.CategoryService;
import com.sample.service.TransactionService;
import com.sample.service.UserService;
import com.sample.util.ConfirmEmail;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
@Controller
public class UserController {

	@Autowired
	UserService userservice;
	
	@Autowired
	BookService bookService;
	
	@Autowired
	AuthorService authorService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	TransactionService tService;
	
	@Autowired
	ConfirmEmail confirmMail;
	
	@Autowired
	OtpGenerator otpGenerator;
	
//	for strong password check
	private static final String STRONG_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(STRONG_PASSWORD_REGEX);
	
	@RequestMapping("/user/login")
	public String login(Model model) {
		model.addAttribute("userDto", new UserLoginDto());
		model.addAttribute("page", "signin");
		return "loginModal";
	}
	
	@RequestMapping("/user/postLogin")
	public String postLogin(@Valid @ModelAttribute("userDto") UserLoginDto user, BindingResult result, RedirectAttributes reAttributes, HttpSession session, ModelMap model) {
		if(result.hasErrors()) {
			return "loginModal";
		}
		User user1=userservice.findUser(user.getName(), user.getEmail(), user.getPassword());
		
		if(user1==null) {
			reAttributes.addFlashAttribute("alertType", "alert-danger");
			reAttributes.addFlashAttribute("alertMessage", "Your credentials are wrong");
			return "redirect:/user/login";
		}
		if (user1.getDelFlag()) {
	        reAttributes.addFlashAttribute("alertType", "alert-danger");
	        reAttributes.addFlashAttribute("alertMessage", "Your account has been deactivated.");
	        return "redirect:/user/login";
	    }
	    if(user1.getIsAdmin()) {
			session.setAttribute("user", user1);
			reAttributes.addFlashAttribute("alertType", "alert-success");
		    reAttributes.addFlashAttribute("alertMessage", "Welcome "+ user1.getName());
			return "redirect:/admin/";
		}
		session.setAttribute("user", user1);
		reAttributes.addFlashAttribute("alertType", "alert-success");
	    reAttributes.addFlashAttribute("alertMessage", "Welcome "+ user1.getName());
		return "redirect:/";
	}
	
	@RequestMapping("/user/register")
	public String register(Model model) {
		model.addAttribute("userRegister", new UserRegisterDto());
		
		Gender[] genders = Gender.values();
		model.addAttribute("genders", genders);
		model.addAttribute("page", "register");
		return "registerForm";
	}
	
	@RequestMapping("/user/postRegister")
	public String postRegister(@Valid @ModelAttribute("userRegister") UserRegisterDto userReDto,
			BindingResult result,
			@RequestParam("img") MultipartFile img,
			RedirectAttributes reAttributes, 
			HttpSession session,
			Model model,
			ModelMap modelmap
			) throws IOException, MessagingException 
	{
		Gender[] genders = Gender.values();
		model.addAttribute("genders", genders);

		if(result.hasErrors() || userservice.checkErr(userReDto.getPhoneNo(),userReDto.getPassword(),userReDto.getConfirmPassword(), modelmap)) {
			return "registerForm";
		}
		if(userservice.findByUserEmail(userReDto.getEmail()))
		{
			model.addAttribute("emailErr", "The email you entered is already exists");
			return "registerForm";
		}

		String otp = otpGenerator.generateOTP();
		LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
		
		if(img != null && !img.isEmpty()) {

			String uploadDir = "C:/Users/GIC-T-06-User/Documents/Library-Management-System-Zip-(10.7.24)/Library-Management-System-Zip/Library-Management-System/profiles/";
			
	        File directory = new File(uploadDir);
	        if (!directory.exists()) {
	            directory.mkdirs();
	        }
	        
	        String imgName = img.getOriginalFilename();
	        File upl = new File(directory, imgName);
	        upl.createNewFile();
	        
	        try (FileOutputStream fout = new FileOutputStream(upl);
					BufferedOutputStream stream = new BufferedOutputStream(fout)) {
				stream.write(img.getBytes());
			}
		}
		
		session.setAttribute("img", img.getOriginalFilename());
		session.setAttribute("userDTO", userReDto);
        session.setAttribute("otp", otp);
        session.setAttribute("otpExpiration", expirationTime);
        
		confirmMail.sendOtpEmail(userReDto.getEmail(), otp);
        
		return "enterOTP";
	}
	
	@RequestMapping("/validate")
	public String validateOTP(@RequestParam("otp") String enteredOtp, HttpSession session, 
	                          RedirectAttributes reAttributes, ModelMap model) throws IOException {

	    // Retrieve session attributes
	    String storedOtp = (String) session.getAttribute("otp");
	    LocalDateTime expirationTime = (LocalDateTime) session.getAttribute("otpExpiration");
	    UserRegisterDto userDTO = (UserRegisterDto) session.getAttribute("userDTO");
	    String img = (String) session.getAttribute("img");

	    // Check if OTP is entered
	    if (!enteredOtp.trim().isEmpty()) {
	        // Check if stored OTP is not null and matches entered OTP
	        if (storedOtp != null && storedOtp.equals(enteredOtp)) {
	            // Check if expiration time is not null and OTP is still valid
	            if (expirationTime != null && expirationTime.isAfter(LocalDateTime.now())) {
	            	
	                // Create user and store in session
	                User user = userservice.createUser(userDTO, img);

	                session.setAttribute("user", user);  // Set user in session
	                reAttributes.addFlashAttribute("alertType", "alert-success");
	                reAttributes.addFlashAttribute("alertMessage", "Welcome " + user.getName());

	                // Remove sensitive session data after successful validation
	                session.removeAttribute("otp");
	                session.removeAttribute("otpExpiration");
	                session.removeAttribute("userDTO");

	                return "redirect:/";  // Redirect to home after successful registration

	            } else {
	            	
	                    // OTP expired, generate a new one
	                    String newOtp = otpGenerator.generateOTP();
	                    LocalDateTime newExpirationTime = LocalDateTime.now().plusMinutes(1);

	                    // Update session with new OTP and expiration time
	                    session.setAttribute("otp", newOtp);
	                    session.setAttribute("otpExpiration", newExpirationTime);

	                    // Resend the new OTP email
	                    confirmMail.sendOtpEmail(userDTO.getEmail(), newOtp);

	                    model.addAttribute("otpErr", "Your OTP has expired. A new OTP has been sent to your email.");
	                    return "enterOTP";  // Return to OTP input page
	                
	            }
	        } else {
	        	
	        	model.addAttribute("otpErr", "Invalid OTP. Please try again.");
	        }
	    } else {
	        // Handle empty OTP input
	        model.addAttribute("otpErr", "Please enter the OTP.");
	    }

	    return "enterOTP";  // Return to OTP input page on failure
	}

	@RequestMapping("/user/logout")
	public String logout(HttpSession session, RedirectAttributes reAttributes) {
		
		session.invalidate();
		reAttributes.addFlashAttribute("alertType", "alert-success");
	    reAttributes.addFlashAttribute("alertMessage", "Bye Bye ");
		return "redirect:/";
	}
	
	@RequestMapping("/user/userProfile")
	public String userProfile(HttpSession session, Model model,@RequestParam(defaultValue = "0",value="page") int page) {
		
		User user = (User) session.getAttribute("user");
		 Pageable pageable = PageRequest.of(page,5);
	     Page<Transaction> transactions = tService.getTransactionsByUser(user, pageable);

	     model.addAttribute("transactionsPage", transactions);
	     model.addAttribute("user", user);
	     return "userProfile";
	}
	
	
	@RequestMapping("/admin/")
	
	public String admin(Model model, HttpSession session, HttpServletRequest request) {
		
		User user=(User) session.getAttribute("user");
		if(user == null || !user.getIsAdmin())
		{
			return "accessDenied";
		}
		Long books=bookService.countBooks();
		Long borrowedBooks=bookService.countBorrowedBooks();
		Long remainingBooks=bookService.countRemainingBooks();
		
		Long authors=authorService.countAuthors();
		Long categories=categoryService.countCategories();
		Long users=userservice.countUsers();
		
		Long transactions=tService.countTransactions();
		Long c_transactions=tService.countCompleteTransactions();
		Long p_transactions=tService.countPendingTransacions();
		
		Long b_requests=tService.countBorrowRequests();
		Long r_requests=tService.countReturnRequests();
		
		model.addAttribute("no_of_books", books);
		model.addAttribute("no_of_borrowedBooks", borrowedBooks);
		model.addAttribute("no_of_remainingBooks", remainingBooks);
		model.addAttribute("no_of_authors", authors);
		model.addAttribute("no_of_categories", categories);
		model.addAttribute("no_of_users", users);
		model.addAttribute("no_of_transactions", transactions);
		model.addAttribute("no_of_ctransactions", c_transactions);
		model.addAttribute("no_of_ptransactions", p_transactions);
		model.addAttribute("no_of_borrowRequests",b_requests);
		model.addAttribute("no_of_returnRequests", r_requests);
		return "adminHomeTest";
	}
	
	@RequestMapping("/user/forgotPassword")
	public String requestEmail()
	{
		return "requestEmail";
	}
	
	@RequestMapping("/user/forgotPasswordAction")
	public String forgotPasswordAction(@RequestParam(value="email") String email, HttpSession session, ModelMap model)
	{
		if(!userservice.findByUserEmail(email))
		{
			model.addAttribute("emailErr", "Invalid email.");
			return "requestEmail";
		}
		String otp = otpGenerator.generateOTP();
		LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
		
		confirmMail.sendOtpEmail(email, otp);
		
		session.setAttribute("otp", otp);
        session.setAttribute("otpExpiration", expirationTime);
        
        model.addAttribute("mode", "passwordReset");
        model.addAttribute("email",email);
        return "enterOTP";
		
	}
	
	@RequestMapping("/user/resetPassword")
	public String resetPassword(@RequestParam("email") String email, ModelMap model)
	{
		model.addAttribute("email", email);
		return "resetPassword";
	}
	
	@RequestMapping("/user/resetPasswordAction")
	public String resetPasswordAction(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword, ModelMap model, HttpSession session, RedirectAttributes reAttributes)
	
	{
		System.out.println(userservice.checkPassword(password,confirmPassword,model));
		System.out.println(password);
		System.out.println(confirmPassword);
		
		if(!userservice.checkPassword(password,confirmPassword,model))
		{
			System.out.println(email);
			User u=userservice.updateUserPassword(email, confirmPassword);
			session.setAttribute("user", u);
			
			reAttributes.addFlashAttribute("alertType", "alert-success");
		    reAttributes.addFlashAttribute("alertMessage", "Welcome "+u.getName()+" Your password is updated.");
			return "redirect:/";
		}
		model.addAttribute("email", email);
		return "resetPassword";
	}
	
	
}
