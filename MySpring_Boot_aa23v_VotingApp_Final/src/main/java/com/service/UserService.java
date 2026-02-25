package com.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.User;
import com.repository.UserRepository;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;

	public void addUser(User user)
	{
		user.setRole("ROLE_NORMAL");
		this.userRepo.save(user);
	}

	public void saveUser(User user)
	{
		this.userRepo.save(user);
	}

	public List<User> getAllUsers()
	{
		return this.userRepo.findAll();
	}

	public User getUserById(int id)
	{
		return this.userRepo.getReferenceById(id);
	}

	public User getUserByEmail(String email)
	{
		if (email == null || email.isBlank()) {
			log.warn("getUserByEmail called with null or blank email");
			return null;
		}
		return this.userRepo.getUserByEmail(email);
	}
}
