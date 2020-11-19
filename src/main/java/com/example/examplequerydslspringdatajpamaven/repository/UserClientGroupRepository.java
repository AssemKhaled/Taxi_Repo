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
import com.example.examplequerydslspringdatajpamaven.entity.userClientGroup;

/**
 * Queries related to type 3 and 4 for tc_user_client_group
 * @author fuinco
 *
 */
@Component
public interface UserClientGroupRepository extends JpaRepository<userClientGroup, Long>, QueryDslPredicateExecutor<userClientGroup>{

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_group where tc_user_client_group.userid=:userId", nativeQuery = true)
	public void deleteGroupsByUserId(@Param("userId") Long userId);
	
	
	@Query(value = "select * from tc_user_client_group where tc_user_client_group.userid=:userId", nativeQuery = true)
	public List<userClientGroup> getGroupsOfUser(@Param("userId") Long userId);
	
	@Query(value = "select tc_groups.id as id , tc_groups.name as name from tc_groups "
			+ " INNER JOIN tc_user_client_group ON tc_user_client_group.groupid=tc_groups.id "
			+ "  where tc_user_client_group.userid=:userId ", nativeQuery = true)
	public List<DriverSelect> getGroupsOfUserList(@Param("userId") Long userId);
	
	@Query(value = "select tc_user_client_group.groupid from tc_user_client_group where tc_user_client_group.userid=:userId", nativeQuery = true)
	public List<Long> getGroupsIds(@Param("userId") Long userId);
	
	
	@Query(value = "select tc_user_client_group.groupid from tc_user_client_group "
			+ " where tc_user_client_group.userid=:userId and tc_user_client_group.groupid=:groupId  ", nativeQuery = true)
	public List<Long> getGroup(@Param("userId") Long userId,@Param("groupId") Long groupId);
	
	@Query(value = "select * from tc_user_client_group where tc_user_client_group.groupid IN (:groupIds) and tc_user_client_group.userid !=:userId", nativeQuery = true)
	public List<userClientGroup> getGroupByGroIds(@Param("groupIds") Long[] groupIds ,@Param("userId") Long userId);

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_group where tc_user_client_group.groupid=:groupId", nativeQuery = true)
	public void deleteGroupById(@Param("groupId") Long groupId);
	
	@Query(value = "select tc_user_client_group.groupid from tc_user_client_group where tc_user_client_group.groupid=:groupId", nativeQuery = true)
	public List<Long> getGroupsToDelete(@Param("groupId") Long groupId);
}
