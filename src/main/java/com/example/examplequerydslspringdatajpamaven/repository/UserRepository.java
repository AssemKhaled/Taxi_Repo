package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.User;

@Component
public interface UserRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {

	@Query(value = " select  * from tc_users u where u.email = :email and hashedpassword = :hashedPassword  and  delete_date is null", nativeQuery = true)
	public User getUserByEmailAndPassword(@Param("email")String email,@Param("hashedPassword")String hashedPassword );
	
	@Query(value = "select * from tc_users u where u.id =1",nativeQuery = true)
	public User getAll();
	
	@Query(value = "select * from tc_users u where u.id =:userId and u.delete_date Is null",nativeQuery = true)
	public User getUserData(@Param("userId") Long userId);
	
	@Query(value = "SELECT tc_users.* FROM tc_user_user inner join tc_users on tc_user_user.manageduserid=tc_users.id where tc_user_user.userid = :userId and delete_date is null limit 0,10", nativeQuery = true)
	public Set<User> getUsersOfUser(@Param("userId") Long userId); 
	
	@Query(value = "SELECT * from tc_users where delete_date is null and (email = :email or "
			+ "identity_num = :identityNum or commercial_num = :commercialNum or "
			+ "company_phone = :companyPhone or manager_phone = :managerPhone or "
			+ "manager_mobile = :managerMobile or phone = :phone) ", nativeQuery = true)
	public List<User> checkUserDuplication(@Param("email") String email, @Param("identityNum")String identityNum,
			                               @Param("commercialNum")String commercialNum , @Param("companyPhone")String companyPhone,
			                               @Param("managerPhone")String managerPhone , @Param("managerMobile")String managerMobile,
			                               @Param("phone")String phone);
	@Modifying
    @Transactional
	@Query(value = "delete  from tc_user_user where manageduserid = :userId", nativeQuery = true )
	public void deleteUserOfUser(@Param("userId")Long deviceId);
	
}
