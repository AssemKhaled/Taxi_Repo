package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import com.example.examplequerydslspringdatajpamaven.entity.Task;

public interface TaskRepository extends  JpaRepository<Task, Long>{

	@Query(value = "SELECT tc_task.* FROM tc_task LIMIT :offset,:limit ", nativeQuery = true)
	public List<Task> getList(@Param("offset") int offset,@Param("limit") int limit);
	
	@Query(value = "SELECT count(*) FROM tc_task", nativeQuery = true)
	public Integer getListSize();


}
