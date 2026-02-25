package com.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private NotificationService notificationService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(notificationService, "fromAddress", "noreply@votingapp.com");
	}

	@Test
	void sendVoteConfirmation_sendsEmailWithCorrectFields() {
		String email = "voter@example.com";
		String candidate = "candidate1";
		LocalDateTime timestamp = LocalDateTime.of(2025, 6, 15, 10, 30, 0);

		notificationService.sendVoteConfirmation(email, candidate, timestamp);

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender, times(1)).send(captor.capture());

		SimpleMailMessage sent = captor.getValue();
		assertEquals("noreply@votingapp.com", sent.getFrom());
		assertEquals(email, sent.getTo()[0]);
		assertEquals("Vote Confirmation", sent.getSubject());
		assertTrue(sent.getText().contains("candidate1"));
		assertTrue(sent.getText().contains("2025-06-15 10:30:00"));
		assertTrue(sent.getText().contains("voter@example.com"));
	}

	@Test
	void sendVoteConfirmation_doesNotThrowOnMailFailure() {
		doThrow(new MailSendException("SMTP down"))
				.when(mailSender).send(any(SimpleMailMessage.class));

		LocalDateTime timestamp = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

		assertDoesNotThrow(() ->
				notificationService.sendVoteConfirmation("user@test.com", "candidate2", timestamp));
	}

	@Test
	void buildVoteConfirmationMessage_containsAllRequiredDetails() {
		String email = "test@example.com";
		String candidate = "candidate3";
		LocalDateTime timestamp = LocalDateTime.of(2025, 12, 25, 18, 0, 0);

		SimpleMailMessage msg = notificationService.buildVoteConfirmationMessage(email, candidate, timestamp);

		assertEquals("Vote Confirmation", msg.getSubject());
		assertEquals("noreply@votingapp.com", msg.getFrom());
		assertEquals(1, msg.getTo().length);
		assertEquals(email, msg.getTo()[0]);

		String body = msg.getText();
		assertTrue(body.contains("candidate3"), "Body should contain candidate name");
		assertTrue(body.contains("2025-12-25 18:00:00"), "Body should contain formatted timestamp");
		assertTrue(body.contains("test@example.com"), "Body should contain user email");
	}
}
