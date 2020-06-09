package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;

public interface ChartService {
	public ResponseEntity<?> getStatus(String TOKEN,Long userId);
	public ResponseEntity<?> getIgnitionMotion(String TOKEN,Long userId);
	public ResponseEntity<?> getDriverHours(String TOKEN,Long userId);
	public ResponseEntity<?> getDistanceFuelEngine(String TOKEN,Long userId);
	public ResponseEntity<?> getNotificationsChart(String TOKEN,Long userId);


}
