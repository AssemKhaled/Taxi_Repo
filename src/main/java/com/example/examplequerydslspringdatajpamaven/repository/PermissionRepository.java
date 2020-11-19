package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.Permission;

/**
 * Queries related to tc_permissions
 * @author fuinco
 *
 */
@Component
public interface PermissionRepository extends  JpaRepository<Permission, Long>, QueryDslPredicateExecutor<Permission> {

	@Query(value = "SELECT * FROM tc_permissions WHERE name LIKE LOWER(:name) AND delete_date IS NULL",nativeQuery = true)
	public  List<Permission> findByName(@Param("name")String name);
	
	@Query(value = "SELECT * FROM tc_permissions WHERE delete_date IS NULL",nativeQuery = true )
	public List<Permission> getAllPermissions();
} 
