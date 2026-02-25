package com.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.model.Candidate;
import com.model.User;
import com.service.CandidateService;
import com.service.UserService;

@Controller
public class CandidateController {
	
	@Autowired
	private CandidateService canServ;
	
	@Autowired
	private UserService userServ;
	
	@GetMapping("/candidates")
	@ResponseBody
	public ResponseEntity<Page<Candidate>> getCandidates(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size)
	{
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(canServ.getCandidates(pageable));
	}
	
	@PostMapping("/addcandidate") // vote
	public String addCandidate(@RequestParam("candidate") String candidate,
			Principal p, Model model, RedirectAttributes redirectAttributes)
	{
		String email = p.getName();
		User user = userServ.getUserByEmail(email);
	
		
		if(user.getStatus() == null)
		{
			try {
				// add a vote to the selectedCandidate
				Candidate selectedCan = canServ.getCandidateByCandidate(candidate);
				selectedCan.setVotes(selectedCan.getVotes() + 1);
				canServ.addCandidate(selectedCan); // update candidate
				
				user.setStatus("Voted");
				userServ.addUser(user); // update user
				
				redirectAttributes.addFlashAttribute("vmsg", "Successfully Voted...");
			}
			catch(Exception e)
			{
				redirectAttributes.addFlashAttribute("vmsg", "Something went wrong...");
				e.printStackTrace();
				return "redirect:/user";
			}
			
			
		}
		
		
		return "redirect:/user";
		
	}

}
