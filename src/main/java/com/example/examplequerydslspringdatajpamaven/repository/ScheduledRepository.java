package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Schedule;

/**
 * Queries realated to tc_schedule
 * @author fuinco
 *
 */
@Service
public interface ScheduledRepository extends JpaRepository<Schedule, Long>, QueryDslPredicateExecutor<Schedule>{

	
	@Query(value = "SELECT tc_schedule.* FROM tc_schedule"
			+ " WHERE tc_schedule.userId IN(:userIds) and tc_schedule.delete_date is null"
			+ " and ( (tc_schedule.date_type Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Schedule> getAllScheduled(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_schedule.* FROM tc_schedule"
			+ " WHERE tc_schedule.userId IN(:userIds) and tc_schedule.delete_date is null"
			+ " and ( (tc_schedule.date_type Like %:search%) ) " , nativeQuery = true)
	public List<Schedule> getAllScheduledExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	

	@Query(value = "SELECT distinct expression FROM tc_schedule" , nativeQuery = true)
	public ArrayList<String> getDistinctExp();
	
	@Query(value = "SELECT count(*) FROM tc_schedule  " + 
			"  WHERE tc_schedule.userId IN(:userIds) and tc_schedule.delete_date is null", nativeQuery = true)
	public Integer getAllScheduledSize(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_schedule.* FROM tc_schedule"
			+ " WHERE tc_schedule.expression=:expression and tc_schedule.delete_date is null ", nativeQuery = true)
	public List<Schedule> getAllScheduledHaveExpression(@Param("expression") String expression);
	
	@Query(value = "SELECT tc_schedule.id,tc_schedule.email FROM tc_schedule " 
			+ " WHERE tc_schedule.userId IN(:userIds) and tc_schedule.delete_date is null",nativeQuery = true)
	public List<DriverSelect> getScheduledSelect(@Param("userIds") List<Long> userIds);
	
}
