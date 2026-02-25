package com.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.model.Candidate;
import com.model.User;
import com.service.CandidateService;
import com.service.UserService;

@Controller
public class CandidateController {

	private static final Logger log = LoggerFactory.getLogger(CandidateController.class);

	private static final int MAX_PAGE_SIZE = 100;

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
		if (page < 0) page = 0;
		if (size < 1 || size > MAX_PAGE_SIZE) size = 5;
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(canServ.getCandidates(pageable));
	}

	@PostMapping("/addcandidate")
	public String addCandidate(@RequestParam("candidate") String candidate,
			Principal p, Model model, RedirectAttributes redirectAttributes)
	{
		if (candidate == null || candidate.isBlank()) {
			redirectAttributes.addFlashAttribute("vmsg", "Invalid candidate selection.");
			return "redirect:/user";
		}

		String email = p.getName();
		User user = userServ.getUserByEmail(email);

		if (user == null) {
			log.error("Authenticated user not found in database: {}", email);
			redirectAttributes.addFlashAttribute("vmsg", "Something went wrong...");
			return "redirect:/user";
		}

		if (user.getStatus() != null) {
			// Already voted — redirect without error
			return "redirect:/user";
		}

		try {
			Candidate selectedCan = canServ.getCandidateByCandidate(candidate);
			if (selectedCan == null) {
				redirectAttributes.addFlashAttribute("vmsg", "Invalid candidate.");
				return "redirect:/user";
			}

			selectedCan.setVotes(selectedCan.getVotes() + 1);
			canServ.addCandidate(selectedCan);

			user.setStatus("Voted");
			userServ.saveUser(user);

			redirectAttributes.addFlashAttribute("vmsg", "Successfully Voted...");
		}
		catch (Exception e)
		{
			log.error("Failed to cast vote for user={}, candidate={}", email, candidate, e);
			redirectAttributes.addFlashAttribute("vmsg", "Something went wrong...");
			return "redirect:/user";
		}

		return "redirect:/user";
	}

}
