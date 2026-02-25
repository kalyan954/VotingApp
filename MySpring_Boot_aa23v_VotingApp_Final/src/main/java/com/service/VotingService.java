package com.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.model.User;
import com.repository.CandidateRepository;
import com.repository.UserRepository;

@Service
public class VotingService {

	private static final Logger log = LoggerFactory.getLogger(VotingService.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CandidateRepository canRepo;

	/**
	 * Cast a vote atomically. Acquires a pessimistic lock on the user row to
	 * prevent double-voting, then increments the candidate vote count with a
	 * single UPDATE statement to avoid lost-update race conditions.
	 *
	 * @return result message for the UI
	 */
	@Transactional
	public VoteResult castVote(String email, String candidateName)
	{
		// Lock the user row — blocks concurrent vote attempts by the same user
		User user = userRepo.getUserByEmailForUpdate(email);

		if (user == null) {
			log.error("Authenticated user not found in database: {}", email);
			return VoteResult.error("Something went wrong...");
		}

		if (user.getStatus() != null) {
			return VoteResult.alreadyVoted();
		}

		// Atomic increment — no read-modify-write in Java
		int updated = canRepo.incrementVotes(candidateName);
		if (updated == 0) {
			return VoteResult.error("Invalid candidate.");
		}

		user.setStatus("Voted");
		userRepo.save(user);

		log.info("Vote recorded: user={}, candidate={}", email, candidateName);
		return VoteResult.success("Successfully Voted...");
	}

	public static class VoteResult {
		private final Status status;
		private final String message;

		public enum Status { SUCCESS, ALREADY_VOTED, ERROR }

		private VoteResult(Status status, String message) {
			this.status = status;
			this.message = message;
		}

		public static VoteResult success(String msg) { return new VoteResult(Status.SUCCESS, msg); }
		public static VoteResult alreadyVoted()       { return new VoteResult(Status.ALREADY_VOTED, null); }
		public static VoteResult error(String msg)    { return new VoteResult(Status.ERROR, msg); }

		public Status getStatus()  { return status; }
		public String getMessage() { return message; }
	}
}
