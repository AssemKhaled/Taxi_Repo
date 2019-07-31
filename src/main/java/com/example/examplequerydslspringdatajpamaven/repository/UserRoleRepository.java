package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Permission;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
@Component
public interface UserRoleRepository extends  JpaRepository<UserRole, Long>, QueryDslPredicateExecutor<UserRole>{

	@Query(value = "SELECT * FROM tc_user_roles WHERE name LIKE LOWER(:name) AND delete_date IS NULL",nativeQuery = true)
	public  List<UserRole> findByName(@Param("name")String name);
	
	@Query(value = "SELECT * FROM tc_user_roles INNER JOIN tc_users ON tc_users.roleId = tc_user_roles.roleId "
			+ "WHERE tc_users.id = :userId AND tc_users.delete_date IS NULL AND tc_user_roles.delete_date IS NULL" 
			,nativeQuery = true)
	public List<UserRole>getUserRole(@Param("userId")Long userId);
	
	@Query(value = "SELECT * from tc_user_roles where userId = :userId  AND delete_date IS NULL",nativeQuery = true)
	public List<UserRole>getAllRolesCreatedByUser(@Param("userId")Long userId);
}