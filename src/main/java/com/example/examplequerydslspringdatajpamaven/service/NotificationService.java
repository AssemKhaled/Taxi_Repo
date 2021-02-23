package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;

@Service
public interface NotificationService {
	public ResponseEntity<?> createNotification(String TOKEN,String authorization,Notification notification,Long userId);
	public ResponseEntity<?> getAllNotifications(String TOKEN,Long id,int offset,String search,String exportData);
	public ResponseEntity<?> getNotificationById(String TOKEN,Long notificationId,Long userId);
	public ResponseEntity<?> editNotification(String TOKEN,String authorization,Notification notification,Long id);
	public ResponseEntity<?> deleteNotification(String TOKEN,Long notificationId,Long userId);
	public ResponseEntity<?> assignNotificationToGroup(String TOKEN,Long groupId , Map<String, List> data, Long userId);
	public ResponseEntity<?> assignNotificationToDevice(String TOKEN,Long deviceId , Map<String, List> data, Long userId);

	public ResponseEntity<?> getNotificationSelect(String TOKEN,Long userId,Long deviceId,Long groupId);

}
