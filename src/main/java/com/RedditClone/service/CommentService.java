package com.RedditClone.service;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.RedditClone.dto.CommentsDto;
import com.RedditClone.entity.Comment;
import com.RedditClone.entity.Post;
import com.RedditClone.entity.User;
import com.RedditClone.exceptions.PostNotFoundException;
import com.RedditClone.exceptions.SubredditNotFoundException;
import com.RedditClone.repository.CommentRepository;
import com.RedditClone.repository.PostRepository;
import com.RedditClone.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {

	private AuthService authService;
	private PostRepository postRepository;
	private CommentRepository commentRepository;
	private UserRepository userRepository;

	public void save(CommentsDto commentDto) {
		Post post = postRepository.findById(commentDto.getPostId())
				.orElseThrow(() -> new PostNotFoundException(commentDto.getPostId().toString()));

		commentRepository.save(mapToComment(commentDto, post, authService.getCurrentUser()));

	}

	Comment mapToComment(CommentsDto commentsDto, Post post, User currentUser) {
		return Comment.builder().text(commentsDto.getText()).post(post).user(currentUser).createdDate(Instant.now())
				.build();

	}

	CommentsDto mapToCommentsDto(Comment comment) {
		return CommentsDto.builder().id(comment.getId()).text(comment.getText()).postId(comment.getPost().getPostId())
				.userName(comment.getUser().getUsername()).createdDate(comment.getCreatedDate()).build();
	}

	public List<CommentsDto> getAllCommentsForPost(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException(postId.toString()));
		return commentRepository.findAllByPost(post).stream().map(this::mapToCommentsDto).toList();
		
	}
	
	public List<CommentsDto> getAllCommentsForUser(String userName) {
		User user = userRepository.findByUsername(userName)
				.orElseThrow(() -> new UsernameNotFoundException(userName));
		return commentRepository.findAllByUser(user).stream().map(this::mapToCommentsDto).toList();
		
	}
}
