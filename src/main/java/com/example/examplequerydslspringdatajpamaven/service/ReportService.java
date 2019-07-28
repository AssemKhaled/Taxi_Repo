package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;

public interface ReportService {
	
	public ResponseEntity<?> getEventsReport(String TOKEN,Long deviceId,int offset,String start,String end,String search);

	public ResponseEntity<?> getStopsReport(String TOKEN,Long deviceId,String type,String from,String to,int page,int start,int limit);

	public ResponseEntity<?> getTripsReport(String TOKEN,Long deviceId,String type,String from,String to,int page,int start,int limit);
	
	public ResponseEntity<?> getNotifications(String TOKEN,Long userId,int offset,String search);


}
