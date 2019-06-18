package com.example.examplequerydslspringdatajpamaven;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import com.example.service.DeviceServiceImpl;
import com.example.service.UserServiceImpl;

@SpringBootApplication
//@Configuration
public class ExampleQuerydslSpringDataJpaMavenApplication  {

	

	public static void main(String[] args) {
		SpringApplication.run(ExampleQuerydslSpringDataJpaMavenApplication.class, args);
	}

	@Bean
	public UserServiceImpl test() {
		return new UserServiceImpl();
	}
	
	@Bean
	public DeviceServiceImpl testDevice() {
		return new DeviceServiceImpl();
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
