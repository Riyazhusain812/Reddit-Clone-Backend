package com.RedditClone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RedditClone.entity.Post;
import com.RedditClone.entity.Subreddit;

public interface SubredditRepository extends JpaRepository<Subreddit, Long> {

	Optional<Subreddit> findByName(String subredditName);

}
