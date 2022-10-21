package com.RedditClone.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RedditClone.entity.Post;
import com.RedditClone.entity.User;
import com.RedditClone.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote,Long>{

	

	Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);

}
