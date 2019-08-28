package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;

public interface ReportService {
	
	public ResponseEntity<?> getEventsReport(String TOKEN,Long deviceId,int offset,String start,String end,String search,Long userId);

	public ResponseEntity<?> getDeviceWorkingHours(String TOKEN,Long deviceId,int offset,String start,String end,String search,Long userId);
	
	public ResponseEntity<?> getDeviceWorkingHoursExport(String TOKEN,Long deviceId,String start,String end,Long userId);

    public ResponseEntity<?> getDriverWorkingHours(String TOKEN,Long driverId,int offset,String start,String end,String search,Long userId);
	
	public ResponseEntity<?> getDriverWorkingHoursExport(String TOKEN,Long driverId,String start,String end,Long userId);

	public ResponseEntity<?> getEventsReportToExcel(String TOKEN,Long deviceId,String start,String end,Long userId);

	
	public ResponseEntity<?> getStopsReport(String TOKEN,Long deviceId,String type,String from,String to,int page,int start,int limit,Long userId);

	public ResponseEntity<?> getTripsReport(String TOKEN,Long deviceId,String type,String from,String to,int page,int start,int limit,Long userId);
	
	public ResponseEntity<?> getNotifications(String TOKEN,Long userId,int offset,String search);


}
