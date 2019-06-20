package com.example.examplequerydslspringdatajpamaven.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.User;

@Service
public interface UserRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {

	@Query(value = " select  * from tc_users u where u.id=1", nativeQuery = true)
	public User getName();
	
	@Query(value = "select * from tc_users u where u.id =1",nativeQuery = true)
	public User getAll();
	
	@Query(value = "select * from tc_users u where u.id =:userId and u.delete_date Is null",nativeQuery = true)
	public User getUserData(@Param("userId") int userId);
	

}
