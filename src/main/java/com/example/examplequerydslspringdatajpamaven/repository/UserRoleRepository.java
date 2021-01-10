package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.UserRole;

/**
 * Queries related to tc_user_roles
 * @author fuinco
 *
 */
@Component
public interface UserRoleRepository extends  JpaRepository<UserRole, Long>, QueryDslPredicateExecutor<UserRole>{

	@Query(value = "SELECT * FROM tc_user_roles WHERE name LIKE LOWER(:name) AND delete_date IS NULL and tc_user_roles.userId =:userId",nativeQuery = true)
	public  List<UserRole> checkDublicateAdd(@Param("userId")Long userId,@Param("name")String name);
	
	@Query(value = "SELECT * FROM tc_user_roles INNER JOIN tc_users ON tc_users.roleId = tc_user_roles.roleId "
			+ "WHERE tc_users.id = :userId AND tc_users.delete_date IS NULL AND tc_user_roles.delete_date IS NULL" 
			,nativeQuery = true)
	public List<UserRole>getUserRole(@Param("userId")Long userId);
	
	@Query(value = "SELECT * from tc_user_roles where tc_user_roles.userId IN(:userIds) AND delete_date IS NULL",nativeQuery = true)
	public List<UserRole>getAllRolesCreatedByUser(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT * from tc_user_roles where tc_user_roles.userId IN(:userIds) AND delete_date IS NULL"
			+ " and tc_user_roles.name LIKE %:search% limit :offset,10 ",nativeQuery = true)
	public List<UserRole>getAllRolesCreatedByUserOffset(@Param("userIds")List<Long> userIds,@Param("offset")int offset,@Param("search")String search);

	@Query(value = "SELECT * from tc_user_roles where tc_user_roles.userId IN(:userIds) AND delete_date IS NULL"
			+ " and tc_user_roles.name LIKE %:search% ",nativeQuery = true)
	public List<UserRole>getAllRolesCreatedByUserOffsetExport(@Param("userIds")List<Long> userIds,@Param("search")String search);
	
	
	@Query(value = "SELECT count(*) from tc_user_roles where tc_user_roles.userId IN(:userIds) AND delete_date IS NULL", nativeQuery = true)
	public Integer getRolesSize(@Param("userIds")List<Long> userIds);
	
	
	@Query(value = "SELECT * from tc_user_roles where tc_user_roles.userId=:userId AND delete_date IS NULL",nativeQuery = true)
	public List<UserRole>getAllRolesCreatedByUserToCompare(@Param("userId")Long userId);
	
}
