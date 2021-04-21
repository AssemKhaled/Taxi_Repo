package com.example.examplequerydslspringdatajpamaven.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.CustomPositions;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceTempHum;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.DriverWorkingHours;
import com.example.examplequerydslspringdatajpamaven.entity.EventReport;
import com.example.examplequerydslspringdatajpamaven.entity.Schedule;
import com.example.examplequerydslspringdatajpamaven.entity.StopReport;
import com.example.examplequerydslspringdatajpamaven.entity.SummaryReport;
import com.example.examplequerydslspringdatajpamaven.entity.TripReport;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.ScheduledRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.rest.ScheduledTasksRestController;

/**
 * services functionality related to schedules
 * @author fuinco
 *
 */
@Component
@Service
public class ScheduledServiceImpl extends RestServiceController implements ScheduledService{

	private static final Log logger = LogFactory.getLog(ScheduledServiceImpl.class);

	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private ScheduledRepository scheduledRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ScheduledTasksRestController scheduledTasksRestController;
	
	
	@Autowired
	private ReportServiceImpl reportServiceImpl;
	
	
    @Autowired
    private JavaMailSender emailSender;
	
	 @Value("${stopsUrl}")
	 private String stopsUrl;
	 
	 @Value("${tripsUrl}")
	 private String tripsUrl;
	 
	 @Value("${eventsUrl}")
	 private String eventsUrl;
	 
	 @Value("${summaryUrl}")
	 private String summaryUrl;
	
	 @Value("${spring.mail.username}")
	 private String emaiFrom;
	 
	 
	 /**
	  * create schedule by data in body 
	  */
	@Override
	public ResponseEntity<?> createScheduled(String TOKEN, Schedule schedule, Long userId) {
		logger.info("************************ createScheduled STARTED ***************************");

		List<Schedule> schedules= new ArrayList<Schedule>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",schedules);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User user = userServiceImpl.findById(userId);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",schedules);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(userId, "SCHEDULED", "create")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create",null);
						 logger.info("************************ createScheduled ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date()==null) {
					if(schedule.getDate()== null || schedule.getDate_type()== null
							   || schedule.getTask() == null || schedule.getDate()== "" || schedule.getDate_type()== ""
							   || schedule.getTask() == "") {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Schedule task , date and date_type is Required",null);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					if(schedule.getEmail()==null || schedule.getEmail()=="") {

						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Email is required.",null);
						 return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					if(schedule.getId()==null || schedule.getId()==0) {
						if(user.getAccountType().equals(4)) {
							 schedule.setUserId(userId);

						}
						else {
							 schedule.setUserId(userId);

						}
						
						JSONObject obj = new JSONObject(schedule.getDate().toString());
						if(schedule.getDate_type().equals("everyDay")) {
							String exp = "0 0 "+ obj.get("hour") +" ? * *";
							schedule.setExpression(exp);
						}
						
						else if(schedule.getDate_type().equals("everyWeek")) {
							String exp = "0 0 "+ obj.get("hour") +" ? * "+obj.get("dayName");
							schedule.setExpression(exp);
						}
						else if(schedule.getDate_type().equals("everyMonth")) {
							String exp = "0 0 "+ obj.get("hour")+" "+obj.get("dayNumber")+" * ?";
							schedule.setExpression(exp);
						}
						

						else {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date Type should be everyDay ,everyWeek or everyMonth",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						scheduledRepository.save(schedule);
						
						
						if(!scheduledTasksRestController.cronsExpressions.contains(schedule.getExpression())) {
							scheduledTasksRestController.cronsExpressions.add(schedule.getExpression());

							try {
								scheduledTasksRestController.destroy();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						
						schedules.add(schedule);

						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"sucsess",schedules);
						logger.info("************************ createScheduled ENDED ***************************");

						return ResponseEntity.ok().body(getObjectResponse);

					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update Schedule Id",schedules);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",schedules);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}
           			
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",schedules);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}	
	}

	/**
	 * get list of schedule with limit 10
	 */
	@Override
	public ResponseEntity<?> getScheduledList(String TOKEN, Long id, int offset, String search,String exportData) {
        logger.info("************************ getScheduledList STARTED ***************************");
		
		List<Schedule> schedules = new ArrayList<Schedule>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",schedules);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",schedules);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "SCHEDULED", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get SCHEDULED list",null);
						 logger.info("************************ getScheduledList ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDelete_date() == null) {
					
					userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
						
							 List<Long>usersIds= new ArrayList<>();
							 usersIds.add(user.getId());
							 Integer size = 0;
							 List<Map> data = new ArrayList<>();

							 if(exportData.equals("exportData")) {
								 schedules = scheduledRepository.getAllScheduledExport(usersIds,search);

							 }
							 else {
								 schedules = scheduledRepository.getAllScheduled(usersIds,offset,search);
								 if(schedules.size() >0) {
									 size = scheduledRepository.getAllScheduledSize(usersIds);
									 
								 }
							 }

							 
							 for(Schedule schedule:schedules) {
							     Map scheduleList= new HashMap();

								 scheduleList.put("date", schedule.getDate());
								 scheduleList.put("date_type", schedule.getDate_type());
								 scheduleList.put("delete_date", schedule.getDelete_date());
								 scheduleList.put("expression", schedule.getExpression());
								 scheduleList.put("id", schedule.getId());
								 scheduleList.put("task", schedule.getTask());
								 scheduleList.put("userId", schedule.getUserId());
								 scheduleList.put("userName", null);

								 User us = userRepository.findOne(schedule.getUserId());
								 if(us != null) {
									 scheduleList.put("userName", us.getName());

								 }
								 data.add(scheduleList);

							 }
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
							logger.info("************************ getScheduledList ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
						 
					}
				    List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
					 List<Long>usersIds= new ArrayList<>();
					 if(childernUsers.isEmpty()) {
						 usersIds.add(id);
					 }
					 else {
						 usersIds.add(id);
						 for(User object : childernUsers) {
							 usersIds.add(object.getId());
						 }
					 }

					
					 Integer size = 0;
					 List<Map> data = new ArrayList<>();
					 
					 if(exportData.equals("exportData")) {
						 schedules = scheduledRepository.getAllScheduledExport(usersIds,search);

					 }
					 else {
						 schedules = scheduledRepository.getAllScheduled(usersIds,offset,search);
						 if(schedules.size() >0) {
							 size = scheduledRepository.getAllScheduledSize(usersIds);

						 }
					 }

					 
					 for(Schedule schedule:schedules) {
					     Map scheduleList= new HashMap();

						 scheduleList.put("date", schedule.getDate());
						 scheduleList.put("date_type", schedule.getDate_type());
						 scheduleList.put("delete_date", schedule.getDelete_date());
						 scheduleList.put("expression", schedule.getExpression());
						 scheduleList.put("id", schedule.getId());
						 scheduleList.put("task", schedule.getTask());
						 scheduleList.put("userId", schedule.getUserId());
						 scheduleList.put("userName", null);

						 User us = userRepository.findOne(schedule.getUserId());
						 if(us != null) {
							 scheduleList.put("userName", us.getName());

						 }
						 data.add(scheduleList);

					 }
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
					logger.info("************************ getScheduledList ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",schedules);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",schedules);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	/**
	 * get schedule by id
	 */
	@Override
	public ResponseEntity<?> getScheduledById(String TOKEN, Long scheduledId, Long userId) {
		
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<Schedule> schedules = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",schedules);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(scheduledId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No scheduledId  to return ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Schedule schedule= scheduledRepository.findOne(scheduledId);
		if(schedule == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this schedule not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(schedule.getDelete_date() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this schedule not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
		Long createdBy=schedule.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			
			if(isParent) {
				List<Schedule> schedules = new ArrayList<>();
				schedules.add(schedule);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",schedules);
				return  ResponseEntity.ok().body(getObjectResponse);
			}
			
		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}
		
		
 		
		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get schedule",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		
		List<Schedule> schedules = new ArrayList<>();
		schedules.add(schedule);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",schedules);
		return  ResponseEntity.ok().body(getObjectResponse);

	}

	
	/**
	 * delete schedule by id
	 */
	@Override
	public ResponseEntity<?> deleteScheduled(String TOKEN, Long scheduledId, Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<Schedule> schedules= null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",schedules);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "SCHEDULED", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete schedule",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(scheduledId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No scheduledId  to delete ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Schedule schedule= scheduledRepository.findOne(scheduledId);
		if(schedule == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this scheduledId not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(schedule.getDelete_date() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this schedule not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
				


		Long createdBy=schedule.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			if(isParent) {
				Calendar cal = Calendar.getInstance();
				int day = cal.get(Calendar.DATE);
			    int month = cal.get(Calendar.MONTH) + 1;
			    int year = cal.get(Calendar.YEAR);
			    String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
			    schedule.setDelete_date(date);
			     
			    scheduledRepository.save(schedule);
			    
			    
			    List<Schedule> scheduleCheckBeforeRemove = scheduledRepository.getAllScheduledHaveExpression(schedule.getExpression());
			    
			    if(scheduleCheckBeforeRemove.isEmpty()) {
			    	if(scheduledTasksRestController.cronsExpressions.contains(schedule.getExpression())) {
						scheduledTasksRestController.cronsExpressions.remove(schedule.getExpression());

						try {
							scheduledTasksRestController.destroy();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			    }

				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
				return  ResponseEntity.ok().body(getObjectResponse);
				
				
			}
			 
		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}
		
		
 		
		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get schedule",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
	    int month = cal.get(Calendar.MONTH) + 1;
	    int year = cal.get(Calendar.YEAR);
	    String date =  Integer.toString(year)+"-"+ Integer.toString(month)+"-"+ Integer.toString(day);
	    schedule.setDelete_date(date);
	     
	    scheduledRepository.save(schedule);
	    
	    
	    List<Schedule> scheduleCheckBeforeRemove = scheduledRepository.getAllScheduledHaveExpression(schedule.getExpression());
	    
	    if(scheduleCheckBeforeRemove.isEmpty()) {
	    	if(scheduledTasksRestController.cronsExpressions.contains(schedule.getExpression())) {
				scheduledTasksRestController.cronsExpressions.remove(schedule.getExpression());

				try {
					scheduledTasksRestController.destroy();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    }

	    
	    
	    
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	
	/**
	 * edit schedule by data in body and id is mandatory
	 */
	@Override
	public ResponseEntity<?> editScheduled(String TOKEN, Schedule schedule, Long userId) {
		// TODO Auto-generated method stub
		List<Schedule> schedules = new ArrayList<Schedule>();

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",schedules);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "SCHEDULED", "edit")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit schedule",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 
		 if(schedule.getId() == null) {
			 getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "schedule ID is Required",schedules);
			return ResponseEntity.status(404).body(getObjectResponse);
		 }
		 Schedule scheduleCheck = scheduledRepository.findOne(schedule.getId());
			if(scheduleCheck == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "schedule is not found",schedules);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			if(scheduleCheck.getDelete_date() != null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "schedule is not found or deleted",schedules);
				return ResponseEntity.status(404).body(getObjectResponse);
			}

		schedule.setUserId(scheduleCheck.getUserId());
		Long createdBy=schedule.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 
			if(isParent) {
				if(schedule.getEmail()==null || schedule.getEmail()=="") {

					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Email is required.",null);
					 return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				if(schedule.getDate()== null || schedule.getDate_type()== null
						   || schedule.getTask() == null || schedule.getDate()== "" || schedule.getDate_type()== ""
						   || schedule.getTask() == "") {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Schedule task , date and date_type is Required",null);
					return ResponseEntity.badRequest().body(getObjectResponse);

				}
				else {
					JSONObject obj = new JSONObject(schedule.getDate().toString());
					if(schedule.getDate_type().equals("everyDay")) {
						String exp = "0 0 "+ obj.get("hour") +" ? * *";
						schedule.setExpression(exp);
					}
					
					else if(schedule.getDate_type().equals("everyWeek")) {
						String exp = "0 0 "+ obj.get("hour") +" ? * "+obj.get("dayName");
						schedule.setExpression(exp);
					}
					else if(schedule.getDate_type().equals("everyMonth")) {
						String exp = "0 0 "+ obj.get("hour")+" "+obj.get("dayNumber")+" * ?";
						schedule.setExpression(exp);
					}
					
					

					
					
					else {
						getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date Type should be everyDay ,everyWeek or everyMonth",null);
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					
					scheduledRepository.save(schedule);
					
					
					if(!scheduledTasksRestController.cronsExpressions.contains(schedule.getExpression())) {
						scheduledTasksRestController.cronsExpressions.add(schedule.getExpression());

						try {
							scheduledTasksRestController.destroy();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					
					schedules.add(schedule);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",schedules);
					logger.info("************************ editScheduled ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				}
				
			}

			 
		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}
		
		
 		
		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get schedule",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(schedule.getEmail()==null || schedule.getEmail()=="") {

			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Email is required.",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(schedule.getDate()== null || schedule.getDate_type()== null
				   || schedule.getTask() == null || schedule.getDate()== "" || schedule.getDate_type()== ""
				   || schedule.getTask() == "") {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Schedule task , date and date_type is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			JSONObject obj = new JSONObject(schedule.getDate().toString());
			if(schedule.getDate_type().equals("everyDay")) {
				String exp = "0 0 "+ obj.get("hour") +" ? * *";
				schedule.setExpression(exp);
			}
			
			else if(schedule.getDate_type().equals("everyWeek")) {
				String exp = "0 0 "+ obj.get("hour") +" ? * "+obj.get("dayName");
				schedule.setExpression(exp);
			}
			else if(schedule.getDate_type().equals("everyMonth")) {
				String exp = "0 0 "+ obj.get("hour")+" "+obj.get("dayNumber")+" * ?";
				schedule.setExpression(exp);
			}
			
			

			
			
			else {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date Type should be everyDay ,everyWeek or everyMonth",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			scheduledRepository.save(schedule);
			
			
			if(!scheduledTasksRestController.cronsExpressions.contains(schedule.getExpression())) {
				scheduledTasksRestController.cronsExpressions.add(schedule.getExpression());

				try {
					scheduledTasksRestController.destroy();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
			schedules.add(schedule);
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",schedules);
			logger.info("************************ editScheduled ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);

				
				
			
		
		}
		
		
	    

	}

	@Override
	public void doReports(String Expression) {
		
		
	    List<Schedule> scheduleCheckBeforeRemove = scheduledRepository.getAllScheduledHaveExpression(Expression);

	    List<Map> data = new ArrayList<>();
		Map resp = new HashMap();
		JSONArray reports = new JSONArray();
	    JSONArray deviceIds = new JSONArray();
	    JSONArray groupIds = new JSONArray();
	    JSONArray driverIds = new JSONArray();
	    String custom ="";
	    String value ="";

	    for(Schedule object : scheduleCheckBeforeRemove) {
	    	
	    	String email="";
	    	Long userId = object.getUserId();
	    	email = object.getEmail();

	    	String dateType = object.getDate_type();
	    	String from = "";
	    	String to = "";
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
			String currentDate=formatter.format(date);
			
	    	if(dateType.equals("everyMonth")) {
	    		
	    		Date referenceDate = new Date();
	    		Calendar c = Calendar.getInstance(); 
	    		c.setTime(referenceDate); 
	    		c.add(Calendar.MONTH, -1);	    		
	    		String fromDate=formatter.format(c.getTime());

	    		from = fromDate;
	    		to = currentDate;
	    		
	    	}
            if(dateType.equals("everyWeek")) {
	    		
	    		Date referenceDate = new Date();
	    		Calendar c = Calendar.getInstance(); 
	    		c.setTime(referenceDate); 
	    		c.add(Calendar.DATE, -7);	    		
	    		String fromDate=formatter.format(c.getTime());

	    		from = fromDate;
	    		to = currentDate;
	    		
	    	}
            if(dateType.equals("everyDay")) {
	    		
	    		Date referenceDate = new Date();
	    		Calendar c = Calendar.getInstance(); 
	    		c.setTime(referenceDate); 
	    		c.add(Calendar.DATE, -1);	    		
	    		String fromDate=formatter.format(c.getTime());

	    		from = fromDate;
	    		to = currentDate;
	    		
	    	}
	    	
	    	
		    if(object.getTask().toString().startsWith("{")) {

			   JSONObject obj = new JSONObject(object.getTask().toString());

			   
			   if(obj.has("reports")) {
				   reports = obj.getJSONArray("reports");
				   
			   }
			   if(obj.has("custom")) {
				   custom = (String) obj.get("custom");
				   
			   }
			   if(obj.has("value")) {
				   value = (String) obj.get("value");
				   
			   }
			   if(obj.has("groupIds")) {
				   groupIds = obj.getJSONArray("groupIds");
				   
			   }
			   if(obj.has("deviceIds")) {
				   deviceIds = obj.getJSONArray("deviceIds");
				   
			   }
			   if(obj.has("driverIds")) {
				   driverIds = obj.getJSONArray("driverIds");
				   
			   }

		    }
		    
		
			Long [] deviIds= new Long[deviceIds.length()];
			Long [] grouIds=new Long[groupIds.length()];
			Long [] drivIds=new Long[driverIds.length()];



		    for (int i = 0 ; i < deviceIds.length(); i++) {
		    	deviIds[i]=deviceIds.getLong(i);		    
		    }
		    
		    for (int i = 0 ; i < groupIds.length(); i++) {
		    	grouIds[i]=groupIds.getLong(i);		    
		    }
		    
		    for (int i = 0 ; i < driverIds.length(); i++) {
		    	drivIds[i]=driverIds.getLong(i);		    
		    }
		    
		    for (int i = 0 ; i < reports.length(); i++) {
		    	
		    	
                if(reports.get(i).equals("vehicleTempHum")) {
		    		
		    		ResponseEntity<?> response = reportServiceImpl.getVehicleTempHum("Schedule", deviIds, grouIds, 0, from, to, "", userId,"");
		    		
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<DeviceTempHum> tempHumReports = (List<DeviceTempHum>) getObjectResponse.getEntity();

					Random rand = new Random();
					int n = rand.nextInt(999999);

		    		String[] columns = {"Vehicle Name", "Time", "Temperature", "Humidity","Speed (Km/h)"};
				    createExcel("vehicleTempHum",tempHumReports, "vehicleTempHum"+n+".xlsx",columns);
				    sendMail("vehicleTempHum"+n+".xlsx",email);
		    		
		    	}
                
                if(reports.get(i).equals("sensorWeight")) {
		    		
		    		ResponseEntity<?> response = reportServiceImpl.getSensorsReport("Schedule", deviIds, grouIds, 0, from, to, "", userId,"");
		    		
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<CustomPositions> sensorReports = (List<CustomPositions>) getObjectResponse.getEntity();

					Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name", "Time", "Sensor 1 (mv)", "Sensor 2 (mv)",
		    				"Weight (kg)","Last Speed (Km/h)"};
				    createExcel("sensorWeight",sensorReports, "sensorWeight"+n+".xlsx",columns);
				    sendMail("sensorWeight"+n+".xlsx",email);
		    		
		    	}
		    	
		    	if(reports.get(i).equals("events")) {
		    		
		    		ResponseEntity<?> response = reportServiceImpl.getEventsReport("Schedule", deviIds, grouIds, 0, from, to, "", "", userId,"");
		    		
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<EventReport> eventReports = (List<EventReport>) getObjectResponse.getEntity();

					Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Event Type", "Vehicle Name", "Driver Name", "Time", "Latitude", "Longitude"};
				    createExcel("events",eventReports, "events"+n+".xlsx",columns);
				    sendMail("events"+n+".xlsx",email);
		    		
		    	}
                if(reports.get(i).equals("geofenceEnter")) {
		    		
		    		ResponseEntity<?> response = reportServiceImpl.getEventsReport("Schedule", deviIds, grouIds, 0, from, to, "geofenceEnter", "", userId,"");
		    		
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<EventReport> geofenceEnterReports = (List<EventReport>) getObjectResponse.getEntity();

					Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Event Type", "Vehicle Name", "Driver Name", "Time", "Latitude", "Longitude"};
				    createExcel("geofenceEnter",geofenceEnterReports, "geofenceEnter"+n+".xlsx",columns);
				    sendMail("geofenceEnter"+n+".xlsx",email);
		    		
		    	}
                if(reports.get(i).equals("geofenceExit")) {
		    		
		    		ResponseEntity<?> response = reportServiceImpl.getEventsReport("Schedule", deviIds, grouIds, 0, from, to, "geofenceExit", "", userId,"");
		    		
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<EventReport> geofenceExitReports = (List<EventReport>) getObjectResponse.getEntity();

					Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Event Type", "Vehicle Name", "Driver Name", "Time", "Latitude", "Longitude"};
				    createExcel("geofenceExit",geofenceExitReports, "geofenceExit"+n+".xlsx",columns);
				    sendMail("geofenceExit"+n+".xlsx",email);
		    		
		    	}
		    	if(reports.get(i).equals("devicesHours")) {
		    		ResponseEntity<?> response = reportServiceImpl.getDeviceWorkingHours("Schedule", deviIds, grouIds, 0,  from, to, "", userId,"");
		    		
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<DeviceWorkingHours> devicesHours = (List<DeviceWorkingHours>) getObjectResponse.getEntity();
		    		
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name", "Date", "Total Hours Per Day"};
				    createExcel("devicesHours",devicesHours, "devicesHours"+n+".xlsx",columns);
				    sendMail("devicesHours"+n+".xlsx",email);
		    	}
		    	
		    	if(reports.get(i).equals("customs")) {
		    		ResponseEntity<?> response = reportServiceImpl.getCustomReport("Schedule", deviIds, grouIds, 0, from , to, "", userId,custom,value,"");
		    		
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<DeviceWorkingHours> customs = (List<DeviceWorkingHours>) getObjectResponse.getEntity();
		    		
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name", "Date", "Attribute"};
				    createExcel("customs",customs, "customs"+n+".xlsx",columns);
				    sendMail("customs"+n+".xlsx",email);
		    	}
		    	if(reports.get(i).equals("driversHours")) {
		    		ResponseEntity<?> response = reportServiceImpl.getDeviceWorkingHours("Schedule", drivIds, grouIds, 0, from, to, "", userId,"");
		    		
		    		getObjectResponse = (GetObjectResponse) response.getBody();
					List<DriverWorkingHours> driversHours = (List<DriverWorkingHours>) getObjectResponse.getEntity();

		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Driver Name", "Date", "Total Hours Per Day"};
				    createExcel("driverHours",driversHours, "driverHours"+n+".xlsx",columns);
				    sendMail("driverHours"+n+".xlsx",email);
		    	}

		    	
		    	if(reports.get(i).equals("driveMoreThan")) {
		    		ResponseEntity<?> response = reportServiceImpl.getDriveMoreThanReport("Schedule", deviIds,drivIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		
		    		List<TripReport> driveMoreThanReports = (List<TripReport>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Start Time",
		    				"Start Address","Start Latitude","Start Longitude","End Time","End Address",
		    				"End Latitude","End Longitude","Distance (Km)","Average Speed (Km/h)","Maximum Speed (Km/h)","Duration"};
				    createExcel("driveMoreThan",driveMoreThanReports, "driveMoreThan"+n+".xlsx",columns);
				    sendMail("driveMoreThan"+n+".xlsx",email);

		    	}
		    	if(reports.get(i).equals("trips")) {
		    		ResponseEntity<?> response = reportServiceImpl.getTripsReport("Schedule", deviIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		
		    		List<TripReport> tripReports = (List<TripReport>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Start Time",
		    				"Start Address","Start Latitude","Start Longitude","End Time","End Address",
		    				"End Latitude","End Longitude","Distance (Km)","Average Speed (Km/h)","Maximum Speed (Km/h)","Duration"};
				    createExcel("trips",tripReports, "trips"+n+".xlsx",columns);
				    sendMail("trips"+n+".xlsx",email);

		    	}
		    	if(reports.get(i).equals("totalDistance")) {
		    		ResponseEntity<?> response = reportServiceImpl.geTotalTripsReport("Schedule", deviIds,drivIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();

		   		    List<Map> totalDistance = (List<Map>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Total Distance"};
				    createExcel("totalDistance",totalDistance, "totalDistance"+n+".xlsx",columns);
				    sendMail("totalDistance"+n+".xlsx",email);

		    	}
		    	if(reports.get(i).equals("numTrips")) {
		    		ResponseEntity<?> response = reportServiceImpl.getNumTripsReport("Schedule", deviIds,drivIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();

		   		    List<Map> tripsNum = (List<Map>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Number Of Trips"};
				    createExcel("numTrips",tripsNum, "numTrips"+n+".xlsx",columns);
				    sendMail("numTrips"+n+".xlsx",email);

		    	}
		    	if(reports.get(i).equals("numStops")) {
		    		ResponseEntity<?> response = reportServiceImpl.getNumStopsReport("Schedule", deviIds,drivIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();

		   		    List<Map> stopsNum = (List<Map>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Number Of Stops"};
				    createExcel("numStops",stopsNum, "numStops"+n+".xlsx",columns);
				    sendMail("numStops"+n+".xlsx",email);

		    	}
		    	if(reports.get(i).equals("numVisited")) {
		    		ResponseEntity<?> response = reportServiceImpl.getTotalStopsReport("Schedule", deviIds,drivIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();

		   		    List<Map> numVisited = (List<Map>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Total Visited Places"};
				    createExcel("numVisited",numVisited, "numVisited"+n+".xlsx",columns);
				    sendMail("numVisited"+n+".xlsx",email);

		    	}
		    	if(reports.get(i).equals("numDriveHours")) {

		    		ResponseEntity<?> response = reportServiceImpl.getNumberDriverWorkingHours("Schedule",drivIds, grouIds, 0, from,to, "", userId,"");
		    		getObjectResponse = (GetObjectResponse) response.getBody();

		   		    List<Map> numDriveHours = (List<Map>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Total Hours"};
				    createExcel("numDriveHours",numDriveHours, "numDriveHours"+n+".xlsx",columns);
				    sendMail("numDriveHours"+n+".xlsx",email);

		    	}
		    	if(reports.get(i).equals("engineNotMoving")) {
		    		ResponseEntity<?> response = reportServiceImpl.getTotalStopsReport("Schedule", deviIds,drivIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();

		   		    List<Map> engineNotMoving = (List<Map>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Total Engine Hours"};
				    createExcel("engineNotMoving",engineNotMoving, "engineNotMoving"+n+".xlsx",columns);
				    sendMail("engineNotMoving"+n+".xlsx",email);

		    	}
		    	
		    	if(reports.get(i).equals("tripSpentFuel")) {
		    		ResponseEntity<?> response = reportServiceImpl.getTripsReport("Schedule", deviIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		
		    		List<TripReport> spentFuelTripReports = (List<TripReport>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Start Time",
		    				"End Time","Distance (Km)","Spent Fuel (liters)"};
				    createExcel("tripSpentFuel",spentFuelTripReports, "tripSpentFuel"+n+".xlsx",columns);
				    sendMail("tripSpentFuel"+n+".xlsx",email);

		    	}
		    	
		    	if(reports.get(i).equals("tripDistanceSpeed")) {
		    		ResponseEntity<?> response = reportServiceImpl.getTripsReport("Schedule", deviIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		
		    		List<TripReport> distanceSpeedtripReports = (List<TripReport>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Start Time",
		    				"End Time","Distance (Km)","Average Speed (Km/h)","Maximum Speed (Km/h)"};
				    createExcel("tripDistanceSpeed",distanceSpeedtripReports, "tripDistanceSpeed"+n+".xlsx",columns);
				    sendMail("tripDistanceSpeed"+n+".xlsx",email);

		    	}
                if(reports.get(i).equals("stops")) {
                	ResponseEntity<?> response = reportServiceImpl.getStopsReport("Schedule", deviIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		
		    		List<StopReport> stopReports = (List<StopReport>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Start Time",
		    				"Address","End Time","Latitude","Longitude","Engine Hours","Duration"};
				    createExcel("stops",stopReports, "stops"+n+".xlsx",columns);
				    sendMail("stops"+n+".xlsx",email);
                
                }
                if(reports.get(i).equals("durationInStops")) {
                	ResponseEntity<?> response = reportServiceImpl.getStopsReport("Schedule", deviIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		
		    		List<StopReport> durationStopReports = (List<StopReport>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Start Time",
		    				"End Time","Duration"};
				    createExcel("durationInStops",durationStopReports, "durationInStops"+n+".xlsx",columns);
				    sendMail("durationInStops"+n+".xlsx",email);
                
                }
                if(reports.get(i).equals("engineInStops")) {
                	ResponseEntity<?> response = reportServiceImpl.getStopsReport("Schedule", deviIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		
		    		List<StopReport> engineStopReports = (List<StopReport>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Identity Number","Start Time",
		    				"End Time","Engine Hours"};
				    createExcel("engineInStops",engineStopReports, "engineInStops"+n+".xlsx",columns);
				    sendMail("engineInStops"+n+".xlsx",email);
                
                }
                if(reports.get(i).equals("summary")) {
                	ResponseEntity<?> response = reportServiceImpl.getSummaryReport("Schedule", deviIds, grouIds, "allEvents", from, to, 1, 0, 25, userId);
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		
		    		List<StopReport> summaryReports = (List<StopReport>) getObjectResponse.getEntity();
		    		Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name","Driver Name", "Distance","Average Speed",
		    				"Max Speed","Spent Fuel","Start Odometer","End Odometer","Engine Hours"};
				    createExcel("summary",summaryReports, "summary"+n+".xlsx",columns);
				    sendMail("summary"+n+".xlsx",email);
                
                }
		    	
		    }
		    

		    
		   
	    }
	    
		
		
	}

	@Override
	public void accessExpression(String expression) {
		logger.info("************************ Start Access Expression STARTED ***************************");

		doReports(expression);
	    
		logger.info("************************ Start Access Expression ENDED ***************************");


	}

	@Override
	public boolean sendMail(String excelName,String email) {
		logger.info("************************ sendMail STARTED ***************************");

        MimeMessage message = emailSender.createMimeMessage();
        
        MimeMessageHelper helper = null;
		

        try {
			helper = new MimeMessageHelper(message, true);
			helper.setFrom(emaiFrom);
			helper.setTo(email);
	        helper.setSubject("Report Schedule");
	        helper.setText("Check Your Report");
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        String path ="/var/www/html/sareb_sheets/";
        
        FileSystemResource file 
          = new FileSystemResource(new File(path+excelName));
        try {
			helper.addAttachment(file.getFilename(), file);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
        emailSender.send(message);
        File filee = new File(path+excelName);
		if (filee.isFile()) {
			filee.delete();
		}
		logger.info("************************ sendMail ENDED ***************************");

		return true;
	}

	@Override
	public Boolean createExcel(String reportType,List<?> entity, String excelName,String[] columns) {
		List<EventReport> eventsReport = new ArrayList<EventReport>();
		List<TripReport> tripsReport = new ArrayList<TripReport>();
		List<DeviceWorkingHours> devicesHoursReport = new ArrayList<DeviceWorkingHours>();
		List<DeviceWorkingHours> customReport = new ArrayList<DeviceWorkingHours>();
		List<DriverWorkingHours> driversHoursReport = new ArrayList<DriverWorkingHours>();
		List<StopReport> stopsReport = new ArrayList<StopReport>();
		List<SummaryReport> summaryReport = new ArrayList<SummaryReport>();
		List<CustomPositions> sensorWeightReport = new ArrayList<CustomPositions>();
		List<Map> numTripsReport = new ArrayList<Map>();
		List<Map> numStopsReport = new ArrayList<Map>();
		List<Map> totalDistanceReport = new ArrayList<Map>();
		List<Map> numVisitedReport = new ArrayList<Map>();
		List<Map> engineNotMovingReport = new ArrayList<Map>();
		List<Map> numDriveHoursReport = new ArrayList<Map>();
		List<DeviceTempHum> tempHumReport = new ArrayList<DeviceTempHum>();

		
		
		if(reportType.equals("numDriveHours")) {
			numDriveHoursReport = (List<Map>) entity;

		}
    	if(reportType.equals("numVisited")) {
    		numVisitedReport = (List<Map>) entity;

		}
    	if(reportType.equals("engineNotMoving")) {
    		engineNotMovingReport = (List<Map>) entity;

		}
		if(reportType.equals("vehicleTempHum")) {
			tempHumReport = (List<DeviceTempHum>) entity;

		}
		if(reportType.equals("sensorWeight")) {
			sensorWeightReport = (List<CustomPositions>) entity;

		}
		if(reportType.equals("summary")) {
			summaryReport = (List<SummaryReport>) entity;

		}
		if(reportType.equals("events")) {
			eventsReport = (List<EventReport>) entity;

		}
		if(reportType.equals("geofenceEnter")) {
			eventsReport = (List<EventReport>) entity;

		}
		if(reportType.equals("geofenceExit")) {
			tripsReport = (List<TripReport>) entity;

		}
		if(reportType.equals("trips")) {
			tripsReport = (List<TripReport>) entity;

		}
		if(reportType.equals("numTrips")) {
			numTripsReport = (List<Map>) entity;

		}
		if(reportType.equals("numStops")) {
			numStopsReport = (List<Map>) entity;

		}
		if(reportType.equals("totalDistance")) {
			totalDistanceReport = (List<Map>) entity;

		}
		if(reportType.equals("tripSpentFuel")) {
			tripsReport = (List<TripReport>) entity;

		}
		if(reportType.equals("tripDistanceSpeed")) {
			tripsReport = (List<TripReport>) entity;

		}
		if(reportType.equals("stops")) {
			stopsReport = (List<StopReport>) entity;

		}
		if(reportType.equals("durationInStops")) {
			stopsReport = (List<StopReport>) entity;

		}
		if(reportType.equals("engineInStops")) {
			stopsReport = (List<StopReport>) entity;

		}
		if(reportType.equals("devicesHours")) {
			devicesHoursReport = (List<DeviceWorkingHours>) entity;

		}
		if(reportType.equals("driversHours")) {
			driversHoursReport = (List<DriverWorkingHours>) entity;

		}
		if(reportType.equals("customs")) {
			customReport = (List<DeviceWorkingHours>) entity;

		}

		
		Workbook workbook = new XSSFWorkbook();
		
		
	    Sheet sheet = workbook.createSheet("sheet#1");

		
		
        

        CreationHelper createHelper = workbook.getCreationHelper();

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLUE_GREY.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
        


        int rowNum = 1;
        if(reportType.equals("sensorWeight")) {
        	for(CustomPositions sensorWeight: sensorWeightReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(sensorWeight.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(sensorWeight.getDeviceName());
                }
                
                if(sensorWeight.getServertime() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(sensorWeight.getServertime());
                }
                
                if(sensorWeight.getSensor1() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(sensorWeight.getSensor1());
                }
                
                if(sensorWeight.getSensor2() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(sensorWeight.getSensor2());
                }
                
                if(sensorWeight.getWeight() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(sensorWeight.getWeight());
                }
                
                
                if(sensorWeight.getSpeed() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(sensorWeight.getSpeed());
                }
                
              

            }
        }
        if(reportType.equals("vehicleTempHum")) {
        	for(DeviceTempHum sensorWeight: tempHumReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(sensorWeight.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(sensorWeight.getDeviceName());
                }
                
                if(sensorWeight.getServertime() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(sensorWeight.getServertime());
                }
                
                if(sensorWeight.getTemperature() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(sensorWeight.getTemperature());
                }
                
                if(sensorWeight.getHumidity() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(sensorWeight.getHumidity());
                }
                if(sensorWeight.getSpeed() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(sensorWeight.getSpeed());
                }
                
              

            }
        }
        if(reportType.equals("summary")) {
        	for(SummaryReport summary: summaryReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(summary.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(summary.getDeviceName());
                }
                
                if(summary.getDriverName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(summary.getDriverName());
                }
                
                if(summary.getDistance() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(summary.getDistance());
                }
                
                if(summary.getAverageSpeed() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(summary.getAverageSpeed());
                }
                
                if(summary.getMaxSpeed() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(summary.getMaxSpeed());
                }
                
                
                if(summary.getSpentFuel() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(summary.getSpentFuel());
                }
                
                if(summary.getStartOdometer() == null){
                	row.createCell(6)
                    .setCellValue("");
                }
                else {
                	row.createCell(6)
                    .setCellValue(summary.getStartOdometer());
                }
                
                if(summary.getEndOdometer() == null){
                	row.createCell(7)
                    .setCellValue("");
                }
                else {
                	row.createCell(7)
                    .setCellValue(summary.getEndOdometer());
                }
                
                if(summary.getEngineHours() == null){
                	row.createCell(8)
                    .setCellValue("");
                }
                else {
                	row.createCell(8)
                    .setCellValue(summary.getEngineHours());
                }

            }
        }
        if(reportType.equals("durationInStops")) {
        	for(StopReport stopReport: stopsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(stopReport.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(stopReport.getDeviceName());
                }
                
                if(stopReport.getDriverName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(stopReport.getDriverName());
                }
                
                if(stopReport.getDriverUniqueId() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(stopReport.getDriverUniqueId());
                }
                
                if(stopReport.getStartTime() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(stopReport.getStartTime());
                }
                
                if(stopReport.getEndTime() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(stopReport.getEndTime());
                }
                
                
                if(stopReport.getDuration() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(stopReport.getDuration());
                }

            }
        }
        if(reportType.equals("engineInStops")) {
        	for(StopReport stopReport: stopsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(stopReport.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(stopReport.getDeviceName());
                }
                
                if(stopReport.getDriverName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(stopReport.getDriverName());
                }
                
                if(stopReport.getDriverUniqueId() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(stopReport.getDriverUniqueId());
                }
                
                if(stopReport.getStartTime() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(stopReport.getStartTime());
                }
                
                if(stopReport.getEndTime() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(stopReport.getEndTime());
                }
                
               
                if(stopReport.getEngineHours() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(stopReport.getEngineHours());
                }
                
                

            }
        }
        if(reportType.equals("stops")) {
        	for(StopReport stopReport: stopsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(stopReport.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(stopReport.getDeviceName());
                }
                
                if(stopReport.getDriverName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(stopReport.getDriverName());
                }
                
                if(stopReport.getDriverUniqueId() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(stopReport.getDriverUniqueId());
                }
                
                if(stopReport.getStartTime() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(stopReport.getStartTime());
                }
                
                if(stopReport.getAddress() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(stopReport.getAddress());
                }
                
                if(stopReport.getEndTime() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(stopReport.getEndTime());
                }
                
                if(stopReport.getLatitude() == null){
                	row.createCell(6)
                    .setCellValue("");
                }
                else {
                	row.createCell(6)
                    .setCellValue(stopReport.getLatitude());
                }
                
                if(stopReport.getLongitude() == null){
                	row.createCell(7)
                    .setCellValue("");
                }
                else {
                	row.createCell(7)
                    .setCellValue(stopReport.getLongitude());
                }
                
                if(stopReport.getEngineHours() == null){
                	row.createCell(8)
                    .setCellValue("");
                }
                else {
                	row.createCell(8)
                    .setCellValue(stopReport.getEngineHours());
                }
                
                if(stopReport.getDuration() == null){
                	row.createCell(9)
                    .setCellValue("");
                }
                else {
                	row.createCell(9)
                    .setCellValue(stopReport.getDuration());
                }

            }
        }
        if(reportType.equals("events")) {
        	for(EventReport events: eventsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(events.getEventType() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(events.getEventType());
                }
                
                if(events.getDeviceName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(events.getDeviceName());
                }

                if(events.getDriverName() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(events.getDriverName());
                }

                if(events.getServerTime() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(events.getServerTime());
                }
                
                if(events.getLatitude() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(events.getLatitude());
                }
                
                if(events.getLongitude() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(events.getLongitude());
                }
                


            }
        }
        if(reportType.equals("geofenceEnter")) {
        	for(EventReport events: eventsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(events.getEventType() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(events.getEventType());
                }
                
                if(events.getDeviceName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(events.getDeviceName());
                }

                if(events.getDriverName() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(events.getDriverName());
                }

                if(events.getServerTime() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(events.getServerTime());
                }
                
                if(events.getLatitude() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(events.getLatitude());
                }
                
                if(events.getLongitude() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(events.getLongitude());
                }
                


            }
        }
        if(reportType.equals("geofenceExit")) {
        	for(EventReport events: eventsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(events.getEventType() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(events.getEventType());
                }
                
                if(events.getDeviceName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(events.getDeviceName());
                }

                if(events.getDriverName() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(events.getDriverName());
                }

                if(events.getServerTime() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(events.getServerTime());
                }
                
                if(events.getLatitude() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(events.getLatitude());
                }
                
                if(events.getLongitude() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(events.getLongitude());
                }
                


            }
        }
        
        if(reportType.equals("driversHours")) {
        	for(DriverWorkingHours driverHour: driversHoursReport) {
        		Row row = sheet.createRow(rowNum++);
                
                
                if(driverHour.getDriverName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(driverHour.getDriverName());
                }
                
                if(driverHour.getDeviceTime() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(driverHour.getDeviceTime());
                }

                if(driverHour.getHours() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(driverHour.getHours());
                }

        	}
        	
        }
        if(reportType.equals("custom")) {
        	for(DeviceWorkingHours devicehour: devicesHoursReport) {
        		Row row = sheet.createRow(rowNum++);
                
                
                if(devicehour.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(devicehour.getDeviceName());
                }
                
                if(devicehour.getDeviceTime() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(devicehour.getDeviceTime());
                }

                if(devicehour.getAttributes() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(devicehour.getAttributes().toString());
                }

        	}
        	
        }
        if(reportType.equals("devicesHours")) {
        	for(DeviceWorkingHours devicehour: devicesHoursReport) {
        		Row row = sheet.createRow(rowNum++);
                
                
                if(devicehour.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(devicehour.getDeviceName());
                }
                
                if(devicehour.getDeviceTime() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(devicehour.getDeviceTime());
                }

                if(devicehour.getHours() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(devicehour.getHours());
                }

        	}
        	
        }
        if(reportType.equals("driveMoreThan")) {
        	for(TripReport trips: tripsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(trips.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(trips.getDeviceName());
                }
                
                if(trips.getDriverName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(trips.getDriverName());
                }
                
                if(trips.getDriverUniqueId() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(trips.getDriverUniqueId());
                }
                
                if(trips.getStartTime() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(trips.getStartTime());
                }
                
                if(trips.getStartAddress() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(trips.getStartAddress());
                }
               
                if(trips.getStartLat() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(trips.getStartLat());
                }
                
                if(trips.getStartLon() == null){
                	row.createCell(6)
                    .setCellValue("");
                }
                else {
                	row.createCell(6)
                    .setCellValue(trips.getStartLon());
                }
                
                if(trips.getEndTime() == null){
                	row.createCell(7)
                    .setCellValue("");
                }
                else {
                	row.createCell(7)
                    .setCellValue(trips.getEndTime());
                }
                
                if(trips.getEndAddress() == null){
                	row.createCell(8)
                    .setCellValue("");
                }
                else {
                	row.createCell(8)
                    .setCellValue(trips.getEndAddress());
                }
                
                if(trips.getEndLat() == null){
                	row.createCell(9)
                    .setCellValue("");
                }
                else {
                	row.createCell(9)
                    .setCellValue(trips.getEndLat());
                }
                
                if(trips.getEndLon() == null){
                	row.createCell(10)
                    .setCellValue("");
                }
                else {
                	row.createCell(10)
                    .setCellValue(trips.getEndLon());
                }

                if(trips.getDistance() == null){
                	row.createCell(11)
                    .setCellValue("");
                }
                else {
                	row.createCell(11)
                    .setCellValue(trips.getDistance());
                }
                
                if(trips.getAverageSpeed() == null){
                	row.createCell(12)
                    .setCellValue("");
                }
                else {
                	row.createCell(12)
                    .setCellValue(trips.getAverageSpeed());
                }
                
                
                if(trips.getMaxSpeed() == null){
                	row.createCell(13)
                    .setCellValue("");
                }
                else {
                	row.createCell(13)
                    .setCellValue(trips.getMaxSpeed());
                }
                
                
                if(trips.getDuration() == null){
                	row.createCell(14)
                    .setCellValue("");
                }
                else {
                	row.createCell(14)
                    .setCellValue(trips.getDuration());
                }


                
            }
        }
        if(reportType.equals("trips")) {
        	for(TripReport trips: tripsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(trips.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(trips.getDeviceName());
                }
                
                if(trips.getDriverName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(trips.getDriverName());
                }
                
                if(trips.getDriverUniqueId() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(trips.getDriverUniqueId());
                }
                
                if(trips.getStartTime() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(trips.getStartTime());
                }
                
                if(trips.getStartAddress() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(trips.getStartAddress());
                }
               
                if(trips.getStartLat() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(trips.getStartLat());
                }
                
                if(trips.getStartLon() == null){
                	row.createCell(6)
                    .setCellValue("");
                }
                else {
                	row.createCell(6)
                    .setCellValue(trips.getStartLon());
                }
                
                if(trips.getEndTime() == null){
                	row.createCell(7)
                    .setCellValue("");
                }
                else {
                	row.createCell(7)
                    .setCellValue(trips.getEndTime());
                }
                
                if(trips.getEndAddress() == null){
                	row.createCell(8)
                    .setCellValue("");
                }
                else {
                	row.createCell(8)
                    .setCellValue(trips.getEndAddress());
                }
                
                if(trips.getEndLat() == null){
                	row.createCell(9)
                    .setCellValue("");
                }
                else {
                	row.createCell(9)
                    .setCellValue(trips.getEndLat());
                }
                
                if(trips.getEndLon() == null){
                	row.createCell(10)
                    .setCellValue("");
                }
                else {
                	row.createCell(10)
                    .setCellValue(trips.getEndLon());
                }

                if(trips.getDistance() == null){
                	row.createCell(11)
                    .setCellValue("");
                }
                else {
                	row.createCell(11)
                    .setCellValue(trips.getDistance());
                }
                
                if(trips.getAverageSpeed() == null){
                	row.createCell(12)
                    .setCellValue("");
                }
                else {
                	row.createCell(12)
                    .setCellValue(trips.getAverageSpeed());
                }
                
                
                if(trips.getMaxSpeed() == null){
                	row.createCell(13)
                    .setCellValue("");
                }
                else {
                	row.createCell(13)
                    .setCellValue(trips.getMaxSpeed());
                }
                
                
                if(trips.getDuration() == null){
                	row.createCell(14)
                    .setCellValue("");
                }
                else {
                	row.createCell(14)
                    .setCellValue(trips.getDuration());
                }


                
            }
        }
        
        if(reportType.equals("numDriveHours")) {
        	for(Map trips: numDriveHoursReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(trips.get("deviceName") == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue((String) trips.get("deviceName"));
                }
                
                if(trips.get("driverName") == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue((String) trips.get("driverName"));
                }
                
                if(trips.get("driverUniqueId") == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue((String) trips.get("driverUniqueId"));
                }
                
                if(trips.get("totalHours") == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue((String) trips.get("totalHours"));
                }
                
            }
        }
        if(reportType.equals("numTrips")) {
        	for(Map trips: numTripsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(trips.get("deviceName") == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue((String) trips.get("deviceName"));
                }
                
                if(trips.get("driverName") == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue((String) trips.get("driverName"));
                }
                
                if(trips.get("driverUniqueId") == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue((String) trips.get("driverUniqueId"));
                }
                
                if(trips.get("trips") == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue((int) trips.get("trips"));
                }
                
            }
        }
        if(reportType.equals("totalDistance")) {
        	for(Map trips: totalDistanceReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(trips.get("deviceName") == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue((String) trips.get("deviceName"));
                }
                
                if(trips.get("driverName") == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue((String) trips.get("driverName"));
                }
                
                if(trips.get("driverUniqueId") == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue((String) trips.get("driverUniqueId"));
                }
                
                if(trips.get("totalDistance") == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue((double) trips.get("totalDistance"));
                }
                
            }
        }
        if(reportType.equals("numStops")) {
        	for(Map stops: numStopsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(stops.get("deviceName") == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue((String) stops.get("deviceName"));
                }
                
                if(stops.get("driverName") == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue((String) stops.get("driverName"));
                }
                
                if(stops.get("driverUniqueId") == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue((String) stops.get("driverUniqueId"));
                }
                
                if(stops.get("stops") == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue((int) stops.get("stops"));
                }
                
            }
        }
        if(reportType.equals("engineNotMoving")) {
        	for(Map stops: engineNotMovingReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(stops.get("deviceName") == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue((String) stops.get("deviceName"));
                }
                
                if(stops.get("driverName") == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue((String) stops.get("driverName"));
                }
                
                if(stops.get("driverUniqueId") == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue((String) stops.get("driverUniqueId"));
                }
                
                if(stops.get("totalEngineHours") == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue((String) stops.get("totalEngineHours"));
                }
                
            }
        }
        if(reportType.equals("numVisited")) {
        	for(Map stops: numVisitedReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(stops.get("deviceName") == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue((String) stops.get("deviceName"));
                }
                
                if(stops.get("driverName") == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue((String) stops.get("driverName"));
                }
                
                if(stops.get("driverUniqueId") == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue((String) stops.get("driverUniqueId"));
                }
                
                if(stops.get("totalVisitedPlace") == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue((int) stops.get("totalVisitedPlace"));
                }
                
            }
        }
        if(reportType.equals("tripSpentFuel")) {
        	for(TripReport trips: tripsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(trips.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(trips.getDeviceName());
                }
                
                if(trips.getDriverName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(trips.getDriverName());
                }
                
                if(trips.getDriverUniqueId() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(trips.getDriverUniqueId());
                }
                
                if(trips.getStartTime() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(trips.getStartTime());
                }
                
                if(trips.getEndTime() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(trips.getEndTime());
                }
                
                if(trips.getDistance() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(trips.getDistance());
                }
                
                if(trips.getSpentFuel() == null){
                	row.createCell(6)
                    .setCellValue("");
                }
                else {
                	row.createCell(6)
                    .setCellValue(trips.getSpentFuel());
                }
                
               
            }
        }
        if(reportType.equals("tripDistanceSpeed")) {
        	for(TripReport trips: tripsReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(trips.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(trips.getDeviceName());
                }
                
                if(trips.getDriverName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(trips.getDriverName());
                }
                
                if(trips.getDriverUniqueId() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(trips.getDriverUniqueId());
                }
                
                if(trips.getStartTime() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(trips.getStartTime());
                }
                
                
                if(trips.getEndTime() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(trips.getEndTime());
                }
                
                
                if(trips.getDistance() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(trips.getDistance());
                }
                
                if(trips.getAverageSpeed() == null){
                	row.createCell(6)
                    .setCellValue("");
                }
                else {
                	row.createCell(6)
                    .setCellValue(trips.getAverageSpeed());
                }
                
                
                if(trips.getMaxSpeed() == null){
                	row.createCell(7)
                    .setCellValue("");
                }
                else {
                	row.createCell(7)
                    .setCellValue(trips.getMaxSpeed());
                }
                
               
                
            }
        }


        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }


        FileOutputStream fileOut = null;
        String path ="/var/www/html/sareb_sheets/";
        
		try {
			fileOut = new FileOutputStream(path+excelName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			workbook.write(fileOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


        try {
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
		
		return true;
	}

	/**
	 * get select list of schedule
	 */
	@Override
	public ResponseEntity<?> getScheduledSelect(String TOKEN, Long userId) {
		// TODO Auto-generated method stub
		
		logger.info("************************ getNotificationSelect STARTED ***************************");
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
	    if(userId != 0) {
	    	User user = userServiceImpl.findById(userId);
	    	userServiceImpl.resetChildernArray();

	    	if(user != null) {
	    		if(user.getDelete_date() == null) {
	    			
	    			if(user.getAccountType().equals(4)) {
	   				 

			   			List<Long>usersIds= new ArrayList<>();
	   					usersIds.add(user.getId());
						drivers = scheduledRepository.getScheduledSelect(usersIds);
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
						logger.info("************************ getNotificationSelect ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
	   						
	   				}
	    		
	    			 List<User>childernUsers = userServiceImpl.getAllChildernOfUser(userId);
		   			 List<Long>usersIds= new ArrayList<>();
		   			 if(childernUsers.isEmpty()) {
		   				 usersIds.add(userId);
		   			 }
		   			 else {
		   				 usersIds.add(userId);
		   				 for(User object : childernUsers) {
		   					 usersIds.add(object.getId());
		   				 }
		   			 }
	    			
	    			drivers = scheduledRepository.getScheduledSelect(usersIds);
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
					logger.info("************************ getNotificationSelect ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

	    		}
	    		else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",drivers);
					return ResponseEntity.status(404).body(getObjectResponse);

	    		}
	    	
	    	
	    	}
	    	else {
	    		getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

	    	}
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	
	}
	
	

}
