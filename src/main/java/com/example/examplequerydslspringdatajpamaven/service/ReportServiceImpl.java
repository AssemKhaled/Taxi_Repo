package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Event;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.repository.EventRepository;
@Component
public class ReportServiceImpl implements ReportService {
	
	@Autowired
	EventRepository eventRepository;
	private static final Log logger = LogFactory.getLog(ReportServiceImpl.class);

	@Override
	public List<EventReport> getEventsReport(Long deviceId,int offset,String start,String end) {
		logger.info("************************ getEventsReport STARTED ***************************");
		List<EventReport> events = eventRepository.getEvents(deviceId, offset, start, end);
		logger.info("************************ getEventsReport ENDED ***************************");

		return events;
	}

	

}
