package com.example.examplequerydslspringdatajpamaven.repository;

import com.example.examplequerydslspringdatajpamaven.entity.MongoTowCarLiveLocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoTowCarLiveLocationRepository extends MongoRepository<MongoTowCarLiveLocationEntity,String> {

}
