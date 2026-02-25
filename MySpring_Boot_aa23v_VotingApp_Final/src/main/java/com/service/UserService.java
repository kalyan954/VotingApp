package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.Candidate;
import com.model.User;
import com.repository.CandidateRepository;
import com.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private CandidateRepository canRepo;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public User addUser(User user)
    {
        user.setRole("ROLE_NORMAL");
        return this.userRepo.save(user);
    }
    
    public List<User> getAllUsers()
    {
        return this.userRepo.findAll();
    }
    
    public User getUserById(int id)
    {
        return this.userRepo.getReferenceById(id);
    }
    
    public void deleteUser(int id)
    {
        this.userRepo.deleteById(id);
    }
    
    public User getUserByEmail(String email)
    {
        return this.userRepo.getUserByEmail(email);
    }
    
   
    public User getUserByEmailUnsafe(String email) {
        String sql = "SELECT * FROM users WHERE email = '" + email + "'";
        Query query = entityManager.createNativeQuery(sql, User.class);
        return (User) query.getSingleResult();
    }
}