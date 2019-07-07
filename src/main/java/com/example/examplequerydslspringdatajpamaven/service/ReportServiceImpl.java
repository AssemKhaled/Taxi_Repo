package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Event;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.repository.EventRepository;
@Component
public class ReportServiceImpl implements ReportService {
	
	@Autowired
	EventRepository eventRepository;

	@Override
	public List<EventReport> getEventsReport(Long deviceId,int offset,String start,String end) {
		

		return eventRepository.getEvents(deviceId,offset,start,end);
	}

}
