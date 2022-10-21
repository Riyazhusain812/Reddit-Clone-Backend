package com.RedditClone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RedditClone.entity.Comment;
import com.RedditClone.entity.Post;
import com.RedditClone.entity.User;

public interface CommentRepository extends JpaRepository<Comment, Long>{
	
	List<Comment> findAllByPost(Post post);

	List<Comment> findAllByUser(User user);

}
