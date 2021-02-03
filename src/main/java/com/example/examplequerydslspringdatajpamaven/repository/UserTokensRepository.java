package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.example.examplequerydslspringdatajpamaven.entity.UserTokens;

@Component
public interface UserTokensRepository extends JpaRepository<UserTokens, Long>, QueryDslPredicateExecutor<UserTokens> {

	@Modifying
    @Transactional
	@Query(value = "delete from tc_users_tokens where tc_users_tokens.tokenid =:token", nativeQuery = true )
	public void deleteTokenOfUser(@Param("token")String token);
	
	@Query(value = "SELECT * from tc_users_tokens where tc_users_tokens.tokenid =:token",nativeQuery = true)
	public List<UserTokens> getUserToken(@Param("token")String token);
	
	@Query(value = "SELECT * from tc_users_tokens where tc_users_tokens.userid =:userId",nativeQuery = true)
	public List<UserTokens> getUserTokenById(@Param("userId")Long userId);
	
	@Modifying
    @Transactional
	@Query(value = "delete from tc_users_tokens where tc_users_tokens.userid =:userId", nativeQuery = true )
	public void deleteTokenOfUserById(@Param("userId")Long userId);
}
