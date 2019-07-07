package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.User;

@Service
public interface ProfileRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {

	@Query(value = "SELECT * from tc_users where delete_date is null and (email = :email or "
			+ "identity_num = :identityNum or commercial_num = :commercialNum or "
			+ "company_phone = :companyPhone or manager_phone = :managerPhone or "
			+ "manager_mobile = :managerMobile or phone = :phone) and tc_users.id !=:id", nativeQuery = true)
	public List<User> checkUserDuplication(@Param("id") Long id,@Param("email") String email, @Param("identityNum")String identityNum,
            @Param("commercialNum")String commercialNum , @Param("companyPhone")String companyPhone,
            @Param("managerPhone")String managerPhone , @Param("managerMobile")String managerMobile,
            @Param("phone")String phone);
	
}
