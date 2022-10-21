package com.RedditClone.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentsDto {

	private Long id;
	private Long postId;
	private Instant createdDate;
	private String text;
	private String userName;
}
