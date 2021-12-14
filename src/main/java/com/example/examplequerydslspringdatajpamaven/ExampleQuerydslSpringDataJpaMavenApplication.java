package com.example.examplequerydslspringdatajpamaven;

import java.io.File;

import com.example.examplequerydslspringdatajpamaven.rest.ElmConnectionsRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.example.examplequerydslspringdatajpamaven.service.ProfileServiceImpl;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * Main of start Project 
 * @author fuinco
 *
 */
@SpringBootApplication 
@Configuration
@ComponentScan(basePackages = { "com.example.examplequerydslspringdatajpamaven.*"})
public class ExampleQuerydslSpringDataJpaMavenApplication  {




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

		} else {
			sheets.mkdirs();

		}
		
		
		File photo = new File(directoryPath);
		if (photo.isDirectory()) {

		} else {
			photo.mkdirs();

		}
		
		File userPhoto = new File(user);
		if (userPhoto.isDirectory()) {

		} else {
			userPhoto.mkdirs();

		}
		
		File driverPhoto = new File(driver);
		if (driverPhoto.isDirectory()) {

		} else {
			driverPhoto.mkdirs();

		}
		
		File vehiclePhoto = new File(vehicle);
		if (vehiclePhoto.isDirectory()) {

		} else {
			vehiclePhoto.mkdirs();

		}
		
		File iconPhoto = new File(icon);
		if (iconPhoto.isDirectory()) {

		} else {
			iconPhoto.mkdirs();

		}
		
		File defaultIconPhoto = new File(defaultIcon);
		if (defaultIconPhoto.isDirectory()) {

		} else {
			defaultIconPhoto.mkdirs();

		}
		
		
		File pointsFile = new File(points);
		if (pointsFile.isDirectory()) {

		} else {
			pointsFile.mkdirs();

		}

		SpringApplication.run(ExampleQuerydslSpringDataJpaMavenApplication.class, args);

	}
	
	@Bean
	public ProfileServiceImpl testProfile() {
		return new ProfileServiceImpl();
	}


}
