package com.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

	private static final DateTimeFormatter TIMESTAMP_FMT =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username:noreply@votingapp.com}")
	private String fromAddress;

	/**
	 * Send a vote confirmation email asynchronously so the HTTP response
	 * is not blocked by SMTP latency.
	 */
	@Async
	public void sendVoteConfirmation(String toEmail, String candidateName) {
		LocalDateTime timestamp = LocalDateTime.now();
		sendVoteConfirmation(toEmail, candidateName, timestamp);
	}

	/**
	 * Overload that accepts an explicit timestamp (useful for testing).
	 */
	public void sendVoteConfirmation(String toEmail, String candidateName, LocalDateTime timestamp) {
		SimpleMailMessage message = buildVoteConfirmationMessage(toEmail, candidateName, timestamp);

		try {
			mailSender.send(message);
			log.info("Vote confirmation email sent to {}", toEmail);
		} catch (MailException e) {
			// Email failure should not break the voting flow
			log.error("Failed to send vote confirmation email to {}: {}", toEmail, e.getMessage());
		}
	}

	SimpleMailMessage buildVoteConfirmationMessage(String toEmail, String candidateName,
			LocalDateTime timestamp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromAddress);
		message.setTo(toEmail);
		message.setSubject("Vote Confirmation");
		message.setText(String.format(
				"Dear Voter,%n%n"
				+ "Your vote has been successfully recorded.%n%n"
				+ "Details:%n"
				+ "  Candidate: %s%n"
				+ "  Time: %s%n"
				+ "  Email: %s%n%n"
				+ "Thank you for participating!%n"
				+ "VotingApp",
				candidateName,
				timestamp.format(TIMESTAMP_FMT),
				toEmail));
		return message;
	}
}
