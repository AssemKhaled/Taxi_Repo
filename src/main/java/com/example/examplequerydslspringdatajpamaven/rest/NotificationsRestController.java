package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;
import com.example.examplequerydslspringdatajpamaven.service.NotificationServiceImpl;;

@CrossOrigin
@Component
@RequestMapping(path = "/notification")
public class NotificationsRestController {

	

	@Autowired
	NotificationServiceImpl notificationServiceImpl;
	
	@PostMapping(path ="/createNotification")
	public ResponseEntity<?> createNotification(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			@RequestParam (value = "userId",defaultValue = "0") Long userId,
            @RequestBody(required = false) Notification notification) {
			 return notificationServiceImpl.createNotification(TOKEN, notification,userId);				
	}
	@RequestMapping(value = "/getAllNotifications", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getAllNotifications(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
    	return  notificationServiceImpl.getAllNotifications(TOKEN,id);

	}
	
	@RequestMapping(value = "/getNotificationById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotificationById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "notificationId", defaultValue = "0") Long notificationId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  notificationServiceImpl.getNotificationById(TOKEN,notificationId,userId);

	}
	
	@RequestMapping(value = "/editNotification", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editNotification(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestBody(required = false) Notification notification,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return notificationServiceImpl.editNotification(TOKEN,notification,id);

	}
	
	@RequestMapping(value = "/deleteNotification", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteNotification(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "notificationId", defaultValue = "0") Long notificationId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  notificationServiceImpl.deleteNotification(TOKEN,notificationId,userId);


	}
	
	@RequestMapping(value = "/assignNotificationToGroup", method = RequestMethod.POST)
	public ResponseEntity<?> assignNotificationToGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestBody Map<String, List> data,
													@RequestParam(value = "groupId" ,defaultValue = "0") Long groupId,
													@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return notificationServiceImpl.assignNotificationToGroup(TOKEN,groupId,data,userId);	
		
	}
	
	@RequestMapping(value = "/assignNotificationToDevice", method = RequestMethod.POST)
	public ResponseEntity<?> assignNotificationToDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestBody Map<String, List> data,
													@RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
													@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return notificationServiceImpl.assignNotificationToDevice(TOKEN,deviceId,data,userId);	
		
	}
}
