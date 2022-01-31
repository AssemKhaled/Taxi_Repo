package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.examplequerydslspringdatajpamaven.entity.MongoEvents;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
public interface MongoEventsRepository extends MongoRepository<MongoEvents, String>{

	@Query(value="{ '_id' : { $in: ?0 } }", delete = true)
	public List<MongoEvents> deleteByIdIn(List<String> positionIds);

}
