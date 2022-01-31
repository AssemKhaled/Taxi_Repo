package com.example.examplequerydslspringdatajpamaven.repository;

import com.example.examplequerydslspringdatajpamaven.entity.MongoTowCarLiveLocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoTowCarLiveLocationRepository extends MongoRepository<MongoTowCarLiveLocationEntity,String> {

    @Query(value="{ '_id' : { $in: ?0 } }", delete = true)
    void deleteByIdIn(List<String> positionIds);


}
