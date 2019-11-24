package com.example.examplequerydslspringdatajpamaven.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import com.example.examplequerydslspringdatajpamaven.entity.Device;

//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        entityManagerFactoryRef = "mysqlEntityManager",
//        transactionManagerRef = "mysqlTransactionManager",
//        basePackages = "com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository"
//)
@ComponentScan
public class SqlDBConfig {
//	@Primary
//	@Bean(name = "dataSource")
//	@ConfigurationProperties(prefix = "spring.datasource")
//	public DataSource mysqlDataSource() {
//		return DataSourceBuilder.create().build();
//	}

//	    @Primary
//	    @Bean(name = "mysqlEntityManager")
//	    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
//	    	return builder
//	                    .dataSource(mysqlDataSource())
//	                    .packages(Device.class)
//	                    .build();
//	    }
//	 
//	    @Primary
//	    @Bean(name = "mysqlTransactionManager")
//	    public PlatformTransactionManager mysqlTransactionManager(@Qualifier("mysqlEntityManager") EntityManagerFactory entityManagerFactory) {
//	        return new JpaTransactionManager(entityManagerFactory);
//	    }

}
