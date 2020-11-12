package com.example.examplequerydslspringdatajpamaven;

import java.io.File;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.examplequerydslspringdatajpamaven.rest.ScheduledTasksRestController;
import com.example.examplequerydslspringdatajpamaven.service.ProfileServiceImpl;



@SpringBootApplication 
@Configuration
@ComponentScan(basePackages = { "com.example.examplequerydslspringdatajpamaven.*"})
public class ExampleQuerydslSpringDataJpaMavenApplication  {

	
//    @PostConstruct
//    public void init(){
//      // Setting Spring Boot SetTimeZone
//      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Riyadh"));
//    }
	 
	public static void main(String[] args) {

		
		String directoryPath = "/var/www/html/sareb_photo";
		String user = "/var/www/html/sareb_photo/user_photos";
		String driver = "/var/www/html/sareb_photo/driver_photos";
		String vehicle = "/var/www/html/sareb_photo/vehicle_photos";
		String icon = "/var/www/html/sareb_photo/icons";
		String points = "/var/www/html/sareb_photo/points";
		String defaultIcon = "/var/www/html/sareb_photo/icons/default";
		String excelSheets = "/var/www/html/sareb_sheets";

		File sheets = new File(excelSheets);
		if (sheets.isDirectory()) {
			System.out.println("File sheets is a Directory");
		} else {
			sheets.mkdirs();
			System.out.println("Directory Photos doesn't exist!!");
		}
		
		
		File photo = new File(directoryPath);
		if (photo.isDirectory()) {
			System.out.println("File Photos is a Directory");
		} else {
			photo.mkdirs();
			System.out.println("Directory Photos doesn't exist!!");
		}
		
		File userPhoto = new File(user);
		if (userPhoto.isDirectory()) {
			System.out.println("File User Photos is a Directory");
		} else {
			userPhoto.mkdirs();
			System.out.println("Directory User Photos doesn't exist!!");
		}
		
		File driverPhoto = new File(driver);
		if (driverPhoto.isDirectory()) {
			System.out.println("File Driver Photos is a Directory");
		} else {
			driverPhoto.mkdirs();
			System.out.println("Directory Driver Photos doesn't exist!!");
		}
		
		File vehiclePhoto = new File(vehicle);
		if (vehiclePhoto.isDirectory()) {
			System.out.println("File Vehicle Photos is a Directory");
		} else {
			vehiclePhoto.mkdirs();
			System.out.println("Directory Vehicle Photos doesn't exist!!");
		}
		
		File iconPhoto = new File(icon);
		if (iconPhoto.isDirectory()) {
			System.out.println("File Icon Photos is a Directory");
		} else {
			iconPhoto.mkdirs();
			System.out.println("Directory Icon Photos doesn't exist!!");
		}
		
		File defaultIconPhoto = new File(defaultIcon);
		if (defaultIconPhoto.isDirectory()) {
			System.out.println("File Default Icon Photos is a Directory");
		} else {
			defaultIconPhoto.mkdirs();
			System.out.println("Directory Default Icon Photos doesn't exist!!");
		}
		
		
		File pointsFile = new File(points);
		if (pointsFile.isDirectory()) {
			System.out.println("File Points Photos is a Directory");
		} else {
			pointsFile.mkdirs();
			System.out.println("Directory Points Photos doesn't exist!!");
		}
		
		SpringApplication.run(ExampleQuerydslSpringDataJpaMavenApplication.class, args);
	}
	
	@Bean
	public ProfileServiceImpl testProfile() {
		return new ProfileServiceImpl();
	}


}
