package com.sample.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sample.entity.User;
import java.util.List;


public interface UserRepo extends JpaRepository<User, Long>{
	
	@Query("from User where name=:uname and email=:email and password=:password")
	User findUser(@Param("uname") String uname,@Param("email") String email,@Param("password") String password);

	User  findByName(String name);
	
	Page<User> findAllByDelFlagAndIsAdmin(Boolean delFlag,Boolean isAdmin, Pageable page);
	
	@Query("SELECT COUNT(u) FROM User u WHERE u.isAdmin = false AND u.delFlag = false")
	Long countByDelFlagFalse();
	
	boolean existsByEmailAndDelFlag(String email, Boolean delFlag);
	
	User findByEmailAndDelFlag(String email, Boolean delFlag);
}
