package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.model.Candidate;
import com.model.User;
import com.repository.CandidateRepository;
import com.repository.UserRepository;

@SpringBootApplication
public class MySpringBootAa23vVotingAppFinalApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(MySpringBootAa23vVotingAppFinalApplication.class, args);
	}
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CandidateRepository canRepo;

	@Override
	public void run(String... args) throws Exception {
	
		// Seed admin if not already present
		if (userRepo.getUserByEmail("admin") == null) {
			User admin = new User();
			admin.setEmail("admin");
			admin.setName("admin");
			admin.setPassword("admin");
			admin.setPhone("1234");
			admin.setRole("ROLE_ADMIN");
			admin.setStatus("admin");
			userRepo.save(admin);
		}
		
		// Seed candidates if not already present
		if (canRepo.getCandidateByCandidate("candidate1") == null) {
			Candidate candidate1 = new Candidate();
			candidate1.setCandidate("candidate1");
			canRepo.save(candidate1);
		}
		
		if (canRepo.getCandidateByCandidate("candidate2") == null) {
			Candidate candidate2 = new Candidate();
			candidate2.setCandidate("candidate2");
			canRepo.save(candidate2);
		}
		
		if (canRepo.getCandidateByCandidate("candidate3") == null) {
			Candidate candidate3 = new Candidate();
			candidate3.setCandidate("candidate3");
			canRepo.save(candidate3);
		}
		
		if (canRepo.getCandidateByCandidate("candidate4") == null) {
			Candidate candidate4 = new Candidate();
			candidate4.setCandidate("candidate4");
			canRepo.save(candidate4);
		}
		
	}
	

}
