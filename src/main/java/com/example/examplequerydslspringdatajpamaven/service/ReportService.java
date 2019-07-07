package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import com.example.examplequerydslspringdatajpamaven.entity.Event;

public interface ReportService {
	
	public List<Event> getEventsReport(Long deviceId,int offset,String start,String end);


}
