package com.example.examplequerydslspringdatajpamaven.repository;

import com.example.examplequerydslspringdatajpamaven.entity.MongoActivities;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface MongoActivitiesRepository extends MongoRepository<MongoActivities, String> {

    List<MongoActivities> findAllByDriverIdInAndActivityTimeBetweenOrderByActivityTimeDesc(List<Integer> driverIds, Date startOfDay, Date endOfDay, Pageable pageable);
}
