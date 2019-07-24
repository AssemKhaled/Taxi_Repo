package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;

public interface ReportService {
	
	public ResponseEntity<?> getEventsReport(Long deviceId,int offset,String start,String end,String search);

	public ResponseEntity<?> getStopsReport(Long deviceId,String type,String from,String to,int page,int start,int limit);

	public ResponseEntity<?> getTripsReport(Long deviceId,String type,String from,String to,int page,int start,int limit);
	
	public ResponseEntity<?> getNotifications(Long userId,int offset,String search);


}
