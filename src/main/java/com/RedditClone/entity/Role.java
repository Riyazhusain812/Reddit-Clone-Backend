package com.RedditClone.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long roleId;
	@NotBlank(message = "Username is required")
	private String roleName;
	
	private String roleDescription;

}
