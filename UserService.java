package com.sample.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.management.relation.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import com.sample.dto.UserRegisterDto;
import com.sample.entity.Author;
import com.sample.entity.User;
import com.sample.repository.BookRepository;

import com.sample.repository.UserRepo;

@Service
public class UserService {
	
	@Autowired
	UserRepo userRepo;
	
	private static final String STRONG_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(STRONG_PASSWORD_REGEX);
	
	public User findUser(String uname,String email,String password) {
		User user=userRepo.findUser(uname, email, password);
		return user;
	}
	
	public User changeTransactionCounts(User user) {
		
		int count=user.getTransactionCounts() + 1;
		user.setTransactionCounts(count);
		
		return userRepo.save(user);

	}
	
	public User findById(Long userId) {
		return userRepo.findById(userId).get();
	}
	
	public User decreaseTransactionCounts(User user)
	{
		user.setTransactionCounts(user.getTransactionCounts()-1);
		return userRepo.save(user);
	}
	
	public User createUser(UserRegisterDto userRDto,String img) throws IOException {

		User user= new User();
		user.setName(userRDto.getName());
		user.setEmail(userRDto.getEmail());
		user.setPassword(userRDto.getPassword());
		user.setPhoneNo(userRDto.getPhoneNo());
		user.setGender(userRDto.getGender());
		user.setTransactionCounts(0);
		user.setIsAdmin(false);
		user.setDelFlag(false);
		user.setUserImg(img);
		
		
		return userRepo.save(user);		
	}
	
	public Long countUsers() {
		
		return userRepo.countByDelFlagFalse();
	}
	
	public Page<User> getAllUsersByPagination(Pageable page){
    	return userRepo.findAllByDelFlagAndIsAdmin(false,false,page);
    }

	public void deleteUser(User user) {
		
		user.setDelFlag(true);
		userRepo.save(user);
	}
	
	public boolean checkPassword(String password,String confirmPassword, ModelMap modelmap) {
		
		boolean errFlg=false;
		if(password.equals(confirmPassword))
		{
			if(!PASSWORD_PATTERN.matcher(password).matches())
			{
				errFlg=true;
				modelmap.addAttribute("passwordErr", "Password must be at least 8 characters long, include an uppercase, lowercase letter, number, and special character.");
				
			}
		}
		else {
			errFlg=true;
			modelmap.addAttribute("passwordConfirmErr", "Password and confirm password must be same.");
		}
		
		return errFlg;
	}
	
	public boolean checkErr(String phone,String password,String confirmPassword, ModelMap modelmap)
	{
		boolean errFlg=false;
		if(phone.length()<11 && !phone.isEmpty())
		{
			errFlg=true;
			modelmap.addAttribute("phoneErr", "Phone number must have at least 11 numbers.");
			
		}

		if(!PASSWORD_PATTERN.matcher(password).matches())
		{
			errFlg=true;
			modelmap.addAttribute("passwordErr", "Password must be at least 8 characters long, include an uppercase, lowercase letter, number, and special character.");
			
		}
		
		if(!confirmPassword.equals(password))
		{
			errFlg=true;
			modelmap.addAttribute("passwordConfirmErr", "Password and confirm password must be same.");
		}
		if(errFlg)
		{
			return true;
		}
		return false;
	}
	
	public boolean findByUserEmail(String email)
	{
		return userRepo.existsByEmailAndDelFlag(email,false);
	}
	
	public User updateUserPassword(String email,String password)
	{
		User u=userRepo.findByEmailAndDelFlag(email,false);
		u.setPassword(password);
		return userRepo.save(u);
	}
}
