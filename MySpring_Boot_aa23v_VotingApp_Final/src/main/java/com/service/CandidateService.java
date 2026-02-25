package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.Candidate;
import com.repository.CandidateRepository;

@Service
public class CandidateService {

	@Autowired
	private CandidateRepository canRepo;

	public void addCandidate(Candidate candidate)
	{
		this.canRepo.save(candidate);
	}

	public List<Candidate> getAllCandidates()
	{
		return this.canRepo.findAll();
	}

	public Page<Candidate> getCandidates(Pageable pageable)
	{
		return this.canRepo.findAll(pageable);
	}

	public Candidate getCandidateById(int id)
	{
		return this.canRepo.getReferenceById(id);
	}

	public int getNumOfVotes(String candidate)
	{
		return this.canRepo.getNumOfVotes(candidate);
	}

	public Candidate getCandidateByCandidate(String candidate)
	{
		if (candidate == null || candidate.isBlank()) {
			return null;
		}
		return this.canRepo.getCandidateByCandidate(candidate);
	}
}
