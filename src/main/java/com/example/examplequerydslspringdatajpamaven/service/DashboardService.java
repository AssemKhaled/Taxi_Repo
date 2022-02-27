package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface DashboardService {
    ResponseEntity<?> getDashboardStatisticsDriverStatus(String TOKEN, Long userId);
    ResponseEntity<?> getDashboardStatisticsTrips(String TOKEN, Long userId);
    ResponseEntity<?> getDashboardDriversLiveDataTable(String TOKEN, Long userId, int offset , int limit, String search);
    ResponseEntity<?> getDashboardActivitiesList(String TOKEN, Long userId, int offset , int limit);
}
