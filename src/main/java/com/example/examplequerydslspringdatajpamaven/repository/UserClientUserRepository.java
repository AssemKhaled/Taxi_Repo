package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.UserSelect;
import com.example.examplequerydslspringdatajpamaven.entity.userClientUser;

@Component
public interface UserClientUserRepository extends JpaRepository<userClientUser, Long>, QueryDslPredicateExecutor<userClientUser>{


	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_user where tc_user_client_user.userid=:userId", nativeQuery = true)
	public void deleteUsersByUserId(@Param("userId") Long userId);
	
	
	@Query(value = "select * from tc_user_client_user where tc_user_client_user.userid=:userId", nativeQuery = true)
	public List<userClientUser> getUsersOfUser(@Param("userId") Long userId);
	
	@Query(value = "select tc_users.id as id , tc_users.name as name from tc_users "
			+ " INNER JOIN tc_user_client_user ON tc_user_client_user.manageduserid=tc_users.id "
			+ "  where tc_user_client_user.userid=:userId ", nativeQuery = true)
	public List<UserSelect> getUsersOfUserList(@Param("userId") Long userId);
	
}
