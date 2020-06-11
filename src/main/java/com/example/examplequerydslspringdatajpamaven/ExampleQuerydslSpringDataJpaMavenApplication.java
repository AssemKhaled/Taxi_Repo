package com.example.examplequerydslspringdatajpamaven;

import java.io.File;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;


import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;
import com.example.examplequerydslspringdatajpamaven.config.MongoDBConfig;
import com.example.examplequerydslspringdatajpamaven.entity.NewPosition;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.PositionRepository;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.DriverServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.GeofenceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.ProfileServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.ReportServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.mongodb.MongoClient;

@EnableMongoRepositories(basePackageClasses = PositionRepository.class)
@EnableJpaRepositories(basePackageClasses = DeviceRepository.class)
@SpringBootApplication 
@Configuration
@ComponentScan(basePackages = { "com.example.examplequerydslspringdatajpamaven.*"})
public class ExampleQuerydslSpringDataJpaMavenApplication  {

	@Bean
	@Primary
	@ConfigurationProperties(prefix="spring.datasource")
	public DataSource primaryDataSource() {
	    return  DataSourceBuilder.create().build();
	}
	@Bean
	@ConfigurationProperties(prefix="spring.secondDatasource")
	public DataSource secondaryDataSource() {
	    return DataSourceBuilder.create().build();
	}

	  @Bean(name = "mongoTemplate")
	    public MongoTemplate mongoTemplate()
	    {
	        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
	        return mongoTemplate;
	    }
	  
	   @Bean
	   public MongoDbFactory mongoDbFactory() 
	   {
	        MongoClient mongoClient = new MongoClient("localhost", 27017);
	        return new SimpleMongoDbFactory(mongoClient, "sareb");
	    }
	   
	  
//	@Bean
//	  public MongoClient mongoClient() {
//	    MongoClient uri = new MongoClient("jdbc:mongo://localhost:27017/sareb");
//	    return uri;
//	  }
	public static void main(String[] args) {
		
		String directoryPath = "/var/www/html/sareb_photo";
		String user = "/var/www/html/sareb_photo/user_photos";
		String driver = "/var/www/html/sareb_photo/driver_photos";
		String vehicle = "/var/www/html/sareb_photo/vehicle_photos";


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
