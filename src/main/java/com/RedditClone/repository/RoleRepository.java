package com.RedditClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RedditClone.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{

}
