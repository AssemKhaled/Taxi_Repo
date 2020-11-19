package com.example.examplequerydslspringdatajpamaven.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.examplequerydslspringdatajpamaven.entity.MongoEvents;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
public interface MongoEventsRepository extends MongoRepository<MongoEvents, String>{


}
