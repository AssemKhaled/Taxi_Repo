package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.userClientComputed;

/**
 * Queries related to type 3 and 4 for tc_user_client_computed
 * @author fuinco
 *
 */
@Component
public interface UserClientComputedRepository extends JpaRepository<userClientComputed, Long>, QueryDslPredicateExecutor<userClientComputed>{

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_computed where tc_user_client_computed.userid=:userId", nativeQuery = true)
	public void deleteComputedsByUserId(@Param("userId") Long userId);
	
	
	@Query(value = "select * from tc_user_client_computed where tc_user_client_computed.userid=:userId", nativeQuery = true)
	public List<userClientComputed> getComputedsOfUser(@Param("userId") Long userId);
	
	@Query(value = "select tc_attributes.id,tc_attributes.description from tc_attributes "
			+ " INNER JOIN tc_user_client_computed ON tc_user_client_computed.computedid=tc_attributes.id "
			+ "  where tc_user_client_computed.userid=:userId ", nativeQuery = true)
	public List<DriverSelect> getComputedsOfUserList(@Param("userId") Long userId);
	
	@Query(value = "select tc_user_client_computed.computedid from tc_user_client_computed where tc_user_client_computed.userid=:userId", nativeQuery = true)
	public List<Long> getComputedsIds(@Param("userId") Long userId);
	
	@Query(value = "select tc_user_client_computed.computedid from tc_user_client_computed "
			+ " where tc_user_client_computed.userid=:userId and tc_user_client_computed.computedid=:computedId  ", nativeQuery = true)
	public List<Long> getComputed(@Param("userId") Long userId,@Param("computedId") Long computedId);
	
	@Query(value = "select * from tc_user_client_computed where tc_user_client_computed.computedid IN (:computedIds) and tc_user_client_computed.userid !=:userId", nativeQuery = true)
	public List<userClientComputed> getComputedsByCompIds(@Param("computedIds") Long[] computedIds,@Param("userId") Long userId);

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_computed where tc_user_client_computed.computedid=:attributeId", nativeQuery = true)
	public void deleteAttributeById(@Param("attributeId") Long attributeId);
	
	@Query(value = "select tc_user_client_computed.computedid from tc_user_client_computed where tc_user_client_computed.computedid=:attributeId", nativeQuery = true)
	public List<Long> getComputedsAttrbIds(@Param("attributeId") Long attributeId);
	
}
