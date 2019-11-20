package com.example.examplequerydslspringdatajpamaven.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import com.example.examplequerydslspringdatajpamaven.entity.NewPosition;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;

import lombok.Data;



@ComponentScan
public class MongoDBConfig {
//	@Bean(name = "dataSource")
//	@ConfigurationProperties(prefix = "spring.secondDatasource")
//	public DataSource mongoDataSource() {
//		return DataSourceBuilder.create().build();
//	}


//	  //  @Primary
//	    @Bean(name = "mongoEntityManager")
//	    public LocalContainerEntityManagerFactoryBean mongoEntityManagerFactory(EntityManagerFactoryBuilder builder) {
//	    	return builder
//	                    .dataSource(mongoDataSource())
//	                    .packages(NewPosition.class)
//	                    .build();
//	    }
//	 
//	    @Primary
//	    @Bean(name = "mongoTransactionManager")
//	    public PlatformTransactionManager mongoTransactionManager(@Qualifier("mongoEntityManager") EntityManagerFactory entityManagerFactory) {
//	        return new JpaTransactionManager(entityManagerFactory);
//	    }

}
