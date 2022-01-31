package com.example.examplequerydslspringdatajpamaven.service;
import com.example.examplequerydslspringdatajpamaven.data.dtos.TaxiProfileDto;
import org.springframework.http.ResponseEntity;

public interface TaxiProfileService {
    ResponseEntity<?> getAllTaxiProfile(String TOKEN, Long loggerId, String name, int pageSize, int pageNumber);
    ResponseEntity<?> createTaxiProfile(String TOKEN, Long loggerId,TaxiProfileDto taxiProfileDto);
    ResponseEntity<?> editTaxiProfile(String TOKEN, Long loggerId, TaxiProfileDto taxiProfileDto);
    ResponseEntity<?> deleteTaxiProfile(String TOKEN, Long loggerId, Long userId);
    ResponseEntity<?> GetUserChildren(String TOKEN, Long loggerId, Long UserId, int pageSize, int pageNumber);
//    ResponseEntity<?> getTaxiProfileByName(String name);
}
