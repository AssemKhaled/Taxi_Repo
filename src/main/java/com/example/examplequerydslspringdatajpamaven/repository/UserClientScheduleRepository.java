package com.example.examplequerydslspringdatajpamaven.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.userClientSchedule;




@Component
public interface UserClientScheduleRepository extends JpaRepository<userClientSchedule, Long>, QueryDslPredicateExecutor<userClientSchedule>{

}
