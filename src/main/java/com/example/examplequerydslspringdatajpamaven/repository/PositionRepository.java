package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;


import javax.jdo.annotations.Query;

import org.bson.types.ObjectId;
import org.hibernate.annotations.Parameter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceLiveData;
import com.example.examplequerydslspringdatajpamaven.entity.NewPosition;
import com.example.examplequerydslspringdatajpamaven.entity.NewcustomerDivice;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;
import com.example.examplequerydslspringdatajpamaven.entity.User;

public interface PositionRepository extends MongoRepository<MongoPositions, String>{
//    @org.springframework.data.mongodb.repository.Query( fields = "{ '_id': 0, 'protocol': 0}")
//    //List<NewPosition> f indAllByProt();
//     //findByProtocol(String protocol);
//      //List<Position> findByProtocol(String protocol);
//     public List<MongoPositions> findAllBydeviceidIn(List<Integer>deviceId);
//    @org.springframework.data.mongodb.repository.Query("{ 'devicetime': ?0, 'devicetime': ?1}")
//    MongoPositions findTopByOrderByOrderdevicetimeDesc(String start,String end);
//     public List<MongoPositions> findAllByidIn(List<String>positionId);

    public List<MongoPositions> findByProtocol(String Protocol);
       
}


