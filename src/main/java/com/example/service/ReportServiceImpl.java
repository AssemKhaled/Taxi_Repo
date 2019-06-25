package com.example.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.examplequerydslspringdatajpamaven.entity.Event;
import com.example.examplequerydslspringdatajpamaven.repository.EventRepository;

public class ReportServiceImpl implements ReportService {
	
	@Autowired
	EventRepository eventRepository;

	@Override
	public List<Event> getEventsReport(Long deviceId,int offset,String start,String end) {
		

		return eventRepository.getEvents(deviceId,offset,start,end);
	}

}
