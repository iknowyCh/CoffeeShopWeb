package com.example.Test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Test.model.po.User;

public interface UserRepository extends JpaRepository<User, Long> {

	  	User findByUsername(String username);
	    User findByUsernameAndPassword(String username, String password);
	    User findByEmail(String email);
	    User findByResetToken(String token);
	    User findByUsernameAndEmail(String username, String email);
}
