package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Interface functions of charts
 * @author fuinco
 *
 */
@Service
public interface ChartService {
	public ResponseEntity<?> getStatus(String TOKEN,Long userId);
	public ResponseEntity<?> getDistanceFuelEngine(String TOKEN,Long userId);
	public ResponseEntity<?> getNotificationsChart(String TOKEN,Long userId);
	public ResponseEntity<?> getMergeHoursIgnition(String TOKEN,Long userId);


}
