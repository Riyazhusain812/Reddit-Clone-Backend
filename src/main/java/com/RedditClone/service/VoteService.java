package com.RedditClone.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.RedditClone.dto.VoteDto;
import com.RedditClone.entity.Post;
import com.RedditClone.entity.Vote;
import com.RedditClone.entity.VoteType;
import com.RedditClone.exceptions.PostNotFoundException;
import com.RedditClone.exceptions.SpringRedditException;
import com.RedditClone.repository.PostRepository;
import com.RedditClone.repository.VoteRepository;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor

public class VoteService {

	private VoteRepository voteRepository;
	private PostRepository postRepository;
	private AuthService authService;
	
	
	public void createVote(VoteDto voteDto) {
		Post post = postRepository.findById(voteDto.getPostId())
				.orElseThrow(() -> new PostNotFoundException(voteDto.getPostId().toString()));
		
		Optional<Vote> voteByPostAndUser  = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,authService.getCurrentUser());

		if( voteByPostAndUser.isPresent() &&  voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
			throw new SpringRedditException(" you have already " + voteDto.getVoteType() + "d for this post");
		}
		if(VoteType.UPVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + 1);
		}
		else {
			post.setVoteCount(post.getVoteCount() - 1);
		}
		postRepository.save(post);
		voteRepository.save(mapToVote(voteDto,post));
	}


	private Vote mapToVote(VoteDto voteDto, Post post) {
		
		return Vote.builder().
				voteType(voteDto.getVoteType()).
				post(post).
				user(authService.getCurrentUser()).build();
	}

}
