package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ReportService {
	
	
	public ResponseEntity<?> getEventsReport(String TOKEN,Long [] deviceId,Long [] groupId,int offset,String start,String end,String type,String search,Long userId,String exportData);

	public ResponseEntity<?> getDeviceWorkingHours(String TOKEN,Long [] deviceId,Long [] groupId,int offset,String start,String end,String search,Long userId,String exportData);
	
	public ResponseEntity<?> getCustomReport(String TOKEN,Long [] deviceId,Long [] groupId,int offset,String start,String end,String search,Long userId,String custom,String  value,String exportData);

    public ResponseEntity<?> getDriverWorkingHours(String TOKEN,Long [] driverId,Long [] groupId,int offset,String start,String end,String search,Long userId,String exportData);

	public ResponseEntity<?> getStopsReport(String TOKEN,Long [] deviceId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);

	public ResponseEntity<?> returnFromTraccar(String url,String report,List<Long> allDevices,String from,String to,String type,int page,int start,int limit);
	
	public ResponseEntity<?> getTripsReport(String TOKEN,Long [] deviceId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	
	public ResponseEntity<?> getDriveMoreThanReport(String TOKEN,Long [] deviceId,Long [] driverId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	
	public ResponseEntity<?> getEventsReportByType(String TOKEN,Long [] deviceId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	
	public ResponseEntity<?> getSummaryReport(String TOKEN,Long [] deviceId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);

	public ResponseEntity<?> getSensorsReport(String TOKEN,Long [] deviceId,Long [] groupId,int offset,String start,String end,String search,Long userId,String exportData);
	
	public ResponseEntity<?> getNotifications(String TOKEN,Long userId,int offset,String search);

	public ResponseEntity<?> getNumTripsReport(String TOKEN,Long [] deviceId,Long [] driverId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	
	public ResponseEntity<?> getNumStopsReport(String TOKEN,Long [] deviceId,Long [] driverId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);

	public ResponseEntity<?> geTotalTripsReport(String TOKEN,Long [] deviceId,Long [] driverId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);

	public ResponseEntity<?> getTotalStopsReport(String TOKEN,Long [] deviceId,Long [] driverId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
    
	public ResponseEntity<?> getNumberDriverWorkingHours(String TOKEN,Long [] driverId,Long [] groupId,int offset,String start,String end,String search,Long userId,String exportData);

	public ResponseEntity<?> getviewTrip(String TOKEN,Long deviceId,String startTime,String endTime);

	public ResponseEntity<?> getVehicleTempHum(String TOKEN,Long [] deviceId,Long [] groupId,int offset,String start,String end,String search,Long userId,String exportData);	


}
