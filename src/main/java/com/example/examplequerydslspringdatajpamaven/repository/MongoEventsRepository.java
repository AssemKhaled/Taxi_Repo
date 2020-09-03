package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLogs;
import com.example.examplequerydslspringdatajpamaven.entity.MongoEvents;

public interface MongoEventsRepository extends MongoRepository<MongoEvents, String>{


}
