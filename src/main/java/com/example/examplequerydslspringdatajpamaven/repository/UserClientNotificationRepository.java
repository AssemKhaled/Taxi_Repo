package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.userClientNotification;

@Component
public interface UserClientNotificationRepository extends JpaRepository<userClientNotification, Long>, QueryDslPredicateExecutor<userClientNotification>{

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_notification where tc_user_client_notification.userid=:userId", nativeQuery = true)
	public void deleteNotificationsByUserId(@Param("userId") Long userId);
	
	
	@Query(value = "select * from tc_user_client_notification where tc_user_client_notification.userid=:userId", nativeQuery = true)
	public List<userClientNotification> getNotificationsOfUser(@Param("userId") Long userId);
	
	@Query(value = "select tc_notifications.id,tc_notifications.type from tc_notifications "
			+ " INNER JOIN tc_user_client_notification ON tc_user_client_notification.notificationid=tc_notifications.id "
			+ "  where tc_user_client_notification.userid=:userId ", nativeQuery = true)
	public List<DriverSelect> getNotificationsOfUserList(@Param("userId") Long userId);
}