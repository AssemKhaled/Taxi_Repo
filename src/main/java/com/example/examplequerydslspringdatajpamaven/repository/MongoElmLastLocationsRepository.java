package com.example.examplequerydslspringdatajpamaven.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLastLocations;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
public interface MongoElmLastLocationsRepository extends MongoRepository<MongoElmLastLocations,String>{

}
