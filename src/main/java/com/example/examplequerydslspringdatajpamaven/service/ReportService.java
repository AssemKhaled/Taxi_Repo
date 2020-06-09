package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface ReportService {
	
	public ResponseEntity<?> getEventsReport(String TOKEN,Long deviceId,Long groupId,int offset,String start,String end,String type,String search,Long userId);

	public ResponseEntity<?> getDeviceWorkingHours(String TOKEN,Long deviceId,Long groupId,int offset,String start,String end,String search,Long userId);
	
	public ResponseEntity<?> getDeviceWorkingHoursExport(String TOKEN,Long deviceId,Long groupId,String start,String end,Long userId);

    public ResponseEntity<?> getDriverWorkingHours(String TOKEN,Long driverId,Long groupId,int offset,String start,String end,String search,Long userId);
	
	public ResponseEntity<?> getDriverWorkingHoursExport(String TOKEN,Long driverId,Long groupId,String start,String end,Long userId);

	public ResponseEntity<?> getEventsReportToExcel(String TOKEN,Long deviceId,Long groupId,String start,String end,String type,Long userId);
	public ResponseEntity<?> getSensorsReport(String TOKEN,Long deviceId,Long groupId,int offset,String start,String end,String search,Long userId);
	public ResponseEntity<?> getSensorsReportExport(String TOKEN,Long deviceId,Long groupId,String start,String end,Long userId);

	
	public ResponseEntity<?> getStopsReport(String TOKEN,Long deviceId,Long groupId,String type,String from,String to,int page,int start,int limit,Long userId);

	public ResponseEntity<?> getTripsReport(String TOKEN,Long deviceId,Long groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	
	public ResponseEntity<?> getEventsReportByType(String TOKEN,Long deviceId,Long groupId,String type,String from,String to,int page,int start,int limit,Long userId);

	public ResponseEntity<?> getSummaryReport(String TOKEN,Long deviceId,Long groupId,String type,String from,String to,int page,int start,int limit,Long userId);

	public ResponseEntity<?> getNotifications(String TOKEN,Long userId,int offset,String search);

	public ResponseEntity<?> getNumTripsReport(String TOKEN,Long deviceId,Long driverId,Long groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> getNumStopsReport(String TOKEN,Long deviceId,Long driverId,Long groupId,String type,String from,String to,int page,int start,int limit,Long userId);

	public ResponseEntity<?> geTotalTripsReport(String TOKEN,Long deviceId,Long driverId,Long groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> getTotalStopsReport(String TOKEN,Long deviceId,Long driverId,Long groupId,String type,String from,String to,int page,int start,int limit,Long userId);
    
	public ResponseEntity<?> getNumberDriverWorkingHours(String TOKEN,Long driverId,Long groupId,int offset,String start,String end,String search,Long userId);

	
	public ResponseEntity<?> getviewTrip(String TOKEN,Long deviceId,Long startPositionId,Long endPositionId);

	
	public ResponseEntity<?> getDriveMoreThanReport(String TOKEN,Long deviceId,Long driverId,Long groupId,String type,String from,String to,int page,int start,int limit,Long userId);

	public ResponseEntity<?> getCustomReport(String TOKEN,Long deviceId,Long groupId,int offset,String start,String end,String search,Long userId,String custom,String  value);

}
