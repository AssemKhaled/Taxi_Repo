package com.example.examplequerydslspringdatajpamaven.repository;

import com.example.examplequerydslspringdatajpamaven.entity.TaxiProfile;
import com.example.examplequerydslspringdatajpamaven.responses.TaxiProfileListSelect;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT tc_profile_taxi.id,tc_profile_taxi.name FROM tc_profile_taxi"
            + " WHERE tc_profile_taxi.companyId IN (:userIds ) and tc_profile_taxi.delete_date is null",nativeQuery = true)
    List<TaxiProfileListSelect> getTaxiProfileByCompanyIds(@Param("userIds")List<Long> userIds);
}
