package com.example.examplequerydslspringdatajpamaven.repository;

import com.example.examplequerydslspringdatajpamaven.entity.TaxiProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.util.List;

@Repository
public interface TaxiProfileRepository extends JpaRepository<TaxiProfile, Long> {

//    List<TaxiProfile> findTaxiProfileByName(String name);
    List<TaxiProfile> findAllByName(String name, Pageable pageable);
    List<TaxiProfile> findAllByCompanyIdIn(List<Long> childrenId);
    List<TaxiProfile> findAllByCompanyIdInAndDeleteDate(List<Long> childrenId, Data data, Pageable pageable);
    long countAllByName(String name);
}
