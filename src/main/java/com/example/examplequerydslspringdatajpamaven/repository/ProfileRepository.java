package com.example.examplequerydslspringdatajpamaven.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.User;

@Service
public interface ProfileRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {

	
	
}
