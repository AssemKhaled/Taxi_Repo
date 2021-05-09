package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;

/**
 * Queries related to tc_notifications
 * @author fuinco
 *
 */
public interface NotificationRepository extends  JpaRepository<Notification, Long>, QueryDslPredicateExecutor<Notification>{
	
	
	@Query(value = "SELECT tc_notifications.* FROM tc_notifications INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id"
			+ " WHERE tc_user_notification.userid IN(:userIds) and  tc_notifications.delete_date is null"
			+ " and ((tc_notifications.type Like %:search%) )" + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Notification> getAllNotifications(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_notifications.* FROM tc_notifications INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id"
			+ " WHERE tc_user_notification.userid IN(:userIds) and  tc_notifications.delete_date is null"
			+ " and ((tc_notifications.type Like %:search%) )" , nativeQuery = true)
	public List<Notification> getAllNotificationsExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_notifications INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id "
	+  " WHERE tc_user_notification.userid IN(:userIds) and  tc_notifications.delete_date is null", nativeQuery = true)
	public Integer getAllNotificationsSize(@Param("userIds")List<Long> userIds);
		

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_device_notification where tc_device_notification.notificationid=:notificationId", nativeQuery = true)
	public void deleteNotificationDeviceId(@Param("notificationId") Long notificationId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_group_notification where tc_group_notification.notificationid=:notificationId", nativeQuery = true)
	public void deleteNotificationGroupId(@Param("notificationId") Long notificationId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_notification where tc_user_notification.notificationid=:notificationId", nativeQuery = true)
	public void deleteNotificationUserId(@Param("notificationId") Long notificationId);
	
	@Query(value = "SELECT tc_notifications.id,tc_notifications.type FROM tc_notifications"
			+ " INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id"
			+ " WHERE tc_user_notification.userid IN(:userIds) and tc_notifications.delete_date is null",nativeQuery = true)
	public List<DriverSelect> getNotificationSelect(@Param("userIds") List<Long> userIds);
	
	@Query(value = "SELECT tc_notifications.id FROM tc_notifications"
			+ " INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id"
			+ " WHERE tc_user_notification.userid IN(:userId) and tc_notifications.delete_date is null",nativeQuery = true)
	public List<Long> getNotificationIds(@Param("userId") Long userId);
	
}
