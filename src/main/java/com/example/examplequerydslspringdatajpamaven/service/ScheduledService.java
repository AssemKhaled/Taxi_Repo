package com.example.examplequerydslspringdatajpamaven.service;

import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.Schedule;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;;

public interface ScheduledService {
	
	public ResponseEntity<?> createScheduled(String TOKEN,Schedule schedule,Long userId);
	public ResponseEntity<?> getScheduledList(String TOKEN,Long id,int offset,String search);
	public ResponseEntity<?> getScheduledById(String TOKEN,Long scheduledId,Long userId);
	public ResponseEntity<?> deleteScheduled(String TOKEN,Long scheduledId,Long userId);
	public ResponseEntity<?> editScheduled(String TOKEN,Schedule schedule,Long userId);
	public void accessExpression(String expression);
	public void doReports(String Expression);
	public boolean sendMail(String excelName,String email);
	public Boolean createExcel(String reportType,List<?> entity,String excelName,String[] columns);
}
