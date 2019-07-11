package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.User;

@Service
public interface ProfileRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {

	
	
}
