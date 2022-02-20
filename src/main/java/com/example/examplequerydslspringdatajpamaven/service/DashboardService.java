package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface DashboardService {
    ResponseEntity<?> getDashboardStatisticsDriverStatus(String TOKEN, Long userId);
}
