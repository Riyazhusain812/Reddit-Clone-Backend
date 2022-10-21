package com.RedditClone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RedditClone.entity.Post;
import com.RedditClone.entity.Subreddit;
import com.RedditClone.entity.User;

public interface PostRepository extends JpaRepository<Post, Long>{

	List<Post> findAllBySubreddit(Subreddit subreddit);

	Optional<Post> findByUser(User user);

}
