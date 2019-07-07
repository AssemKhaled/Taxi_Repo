package com.example.examplequerydslspringdatajpamaven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;


import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;




import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.DriverServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.GeofenceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.ProfileServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.ReportServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;


@SpringBootApplication
//@Configuration
@ComponentScan(basePackages = { "com.example.examplequerydslspringdatajpamaven.*"})
public class ExampleQuerydslSpringDataJpaMavenApplication  {

	

	public static void main(String[] args) {
		SpringApplication.run(ExampleQuerydslSpringDataJpaMavenApplication.class, args);
	}

//	@Bean
//	public UserServiceImpl test() {
//		return new UserServiceImpl();
//	}
	
//	@Bean
//	public DeviceServiceImpl testDevice() {
//		return new DeviceServiceImpl();
//	}
	
//	@Bean
//	public DriverServiceImpl testDriver() {
//		return new DriverServiceImpl();
//	}
	
//	@Bean
//	public GeofenceServiceImpl testGeofence() {
//		return new GeofenceServiceImpl();
//	}
	
//	@Bean
//	public ReportServiceImpl testReport() {
//		return new ReportServiceImpl();
//	}
//	@Bean
//	public LoginServiceImpl testLogin() {
//		return new LoginServiceImpl();
//	}
	/*@Bean
	public JWKValidator  testJWKValidator() {
		return new JWKValidator();
	}*/
	
	@Bean
	public ProfileServiceImpl testProfile() {
		return new ProfileServiceImpl();
	}
	
//	@Bean(name = "mainDataSource")
//	@Primary
//	public DataSource mainDataSource() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//
//		dataSource.setUrl(config.getProperty("datasource.url"));
//		dataSource.setUsername(config.getProperty("datasource.user"));
//		dataSource.setPassword(config.getProperty("datasource.pwd"));
//		return dataSource;
//	}
}
