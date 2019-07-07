package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import com.example.examplequerydslspringdatajpamaven.entity.Event;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;

public interface ReportService {
	
	public List<EventReport> getEventsReport(Long deviceId,int offset,String start,String end);


}
