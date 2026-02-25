package com.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.model.User;
import com.service.CandidateService;
import com.service.UserService;

@Controller
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userServ;

	@Autowired
	private CandidateService canServ;

	@PostMapping("/createuser")
	public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes)
	{
		// Validate required fields
		if (user.getEmail() == null || user.getEmail().isBlank()) {
			redirectAttributes.addFlashAttribute("fail", "Email is required.");
			return "redirect:/register";
		}
		if (user.getName() == null || user.getName().isBlank()) {
			redirectAttributes.addFlashAttribute("fail", "Name is required.");
			return "redirect:/register";
		}
		if (user.getPassword() == null || user.getPassword().isBlank()) {
			redirectAttributes.addFlashAttribute("fail", "Password is required.");
			return "redirect:/register";
		}

		String email = user.getEmail().trim();
		user.setEmail(email);

		if (userServ.getUserByEmail(email) != null)
		{
			redirectAttributes.addFlashAttribute("fail", "Registration Failed, Please try different Email Id");
			return "redirect:/register";
		}

		try {
			userServ.addUser(user);
			redirectAttributes.addFlashAttribute("msg", "Registration successful");
		} catch (Exception e) {
			log.error("Failed to register user with email={}", email, e);
			redirectAttributes.addFlashAttribute("fail", "Registration failed due to a server error.");
		}
		return "redirect:/register";
	}

	@RequestMapping("/user")
	public String dashboard(Model model, Principal p)
	{
		String email = p.getName();
		User user = userServ.getUserByEmail(email);
		model.addAttribute("user", user);
		model.addAttribute("candidates", canServ.getAllCandidates());
		return "user/dashboard";
	}

}
