package com.RedditClone.service;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RedditClone.dto.PostRequest;
import com.RedditClone.dto.PostResponse;
import com.RedditClone.dto.SubredditDto;
import com.RedditClone.entity.Post;
import com.RedditClone.entity.Subreddit;
import com.RedditClone.entity.User;
import com.RedditClone.exceptions.PostNotFoundException;
import com.RedditClone.exceptions.SubredditNotFoundException;
import com.RedditClone.repository.CommentRepository;
import com.RedditClone.repository.PostRepository;
import com.RedditClone.repository.SubredditRepository;
import com.RedditClone.repository.UserRepository;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentRepository commentRepository;
    //private final PostMapper postMapper;

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));
        return mapToDto(post);
    }

    
	@Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(toList());
    }

    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));
        postRepository.save(mapToPost(postRequest, subreddit, authService.getCurrentUser()));
    }

   

	@Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(this::mapToDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(toList());
    }
    
    private PostResponse mapToDto(Post post) {
   	 return PostResponse.builder().postName(post.getPostName())
                .id(post.getPostId())
                .description(post.getDescription())
                .url(post.getUrl())
                .subredditName(post.getSubreddit().getName())
                .userName(post.getUser().getUsername())
                .voteCount(post.getVoteCount())
                .commentCount(commentRepository.findAllByPost(post).size())
                .duration(post.getCreatedDate().toString() )
                .build();
   	 //TimeAgo.using(post.getCreatedDate().toEpochMilli())
	}

    private Post mapToPost(PostRequest postRequest, Subreddit subreddit, User currentUser) {
    	 return Post.builder().postName(postRequest.getPostName())
                 .description(postRequest.getDescription())
                 .url(postRequest.getUrl())
                 .subreddit(subreddit)
                 .user(currentUser)
                 .voteCount(0)
                 .createdDate(Instant.now())
                 .build();
	}
}
