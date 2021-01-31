package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserSelect;

/**
 * Queries related to tc_users
 * @author fuinco
 *
 */
@Component
public interface UserRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {
	@Modifying
    @Transactional
	@Query(value = "UPDATE tc_users SET exp_date = DATE_ADD(create_date, INTERVAL 1 YEAR) where accountType not in(1,2,0) and create_date is not null", nativeQuery = true )
	public void updateUsersToSetExp();
	
	@Query(value = " select  * from tc_users u where u.email = :email and hashedpassword = :hashedPassword  and  delete_date is null", nativeQuery = true)
	public User getUserByEmailAndPassword(@Param("email")String email,@Param("hashedPassword")String hashedPassword );
	
	@Query(value = "select * from tc_users u where u.id =1",nativeQuery = true)
	public User getAll();
	
	@Query(value = "select * from tc_users u where u.id =:userId and u.delete_date Is null",nativeQuery = true)
	public User getUserData(@Param("userId") Long userId);
	
	@Query(value = "SELECT tc_users.* FROM tc_user_user inner join tc_users on tc_user_user.manageduserid=tc_users.id where tc_user_user.userid = :userId and tc_users.id != :loggedUserId and delete_date is null AND  "
			+ "(tc_users.name LIKE %:search% OR "
			+ "tc_users.email LIKE %:search% OR tc_users.commercial_num LIKE %:search% OR "
			+ "tc_users.identity_num LIKE %:search%) limit :offset,10", nativeQuery = true)
	public List<User> getUsersOfUser(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId,@Param("offset")int offset,@Param("search")String search); 
	
	@Query(value = "SELECT tc_users.* FROM tc_user_user inner join tc_users on tc_user_user.manageduserid=tc_users.id where tc_user_user.userid = :userId and tc_users.id != :loggedUserId and delete_date is null AND  "
			+ "(tc_users.name LIKE %:search% OR "
			+ "tc_users.email LIKE %:search% OR tc_users.commercial_num LIKE %:search% OR "
			+ "tc_users.identity_num LIKE %:search%) ", nativeQuery = true)
	public List<User> getUsersOfUserExport(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId,@Param("search")String search); 
	
	
	@Query(value = "SELECT tc_users.* FROM tc_user_user inner join tc_users on tc_user_user.manageduserid=tc_users.id where tc_user_user.userid = :userId and tc_users.id != :loggedUserId AND  "
			+ "(tc_users.name LIKE %:search% OR "
			+ "tc_users.email LIKE %:search% OR tc_users.commercial_num LIKE %:search% OR "
			+ "tc_users.identity_num LIKE %:search%) limit :offset,10", nativeQuery = true)
	public List<User> getAllUsersOfUser(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId,@Param("offset")int offset,@Param("search")String search); 

	@Query(value = "SELECT tc_users.* FROM tc_user_user inner join tc_users on tc_user_user.manageduserid=tc_users.id where tc_user_user.userid = :userId and tc_users.id != :loggedUserId AND  "
			+ "(tc_users.name LIKE %:search% OR "
			+ "tc_users.email LIKE %:search% OR tc_users.commercial_num LIKE %:search% OR "
			+ "tc_users.identity_num LIKE %:search%) ", nativeQuery = true)
	public List<User> getAllUsersOfUserExport(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId,@Param("search")String search); 
	
	
	@Query(value = "SELECT tc_users.* FROM tc_user_user inner join tc_users on tc_user_user.manageduserid=tc_users.id where tc_user_user.userid = :userId and tc_users.id != :loggedUserId and delete_date is not  null AND  "
			+ "(tc_users.name LIKE %:search% OR "
			+ "tc_users.email LIKE %:search% OR tc_users.commercial_num LIKE %:search% OR "
			+ "tc_users.identity_num LIKE %:search%) limit :offset,10", nativeQuery = true)
	public List<User> getInactiveUsersOfUser(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId,@Param("offset")int offset,@Param("search")String search); 
	
	@Query(value = "SELECT tc_users.* FROM tc_user_user inner join tc_users on tc_user_user.manageduserid=tc_users.id where tc_user_user.userid = :userId and tc_users.id != :loggedUserId and delete_date is not  null AND  "
			+ "(tc_users.name LIKE %:search% OR "
			+ "tc_users.email LIKE %:search% OR tc_users.commercial_num LIKE %:search% OR "
			+ "tc_users.identity_num LIKE %:search%) ", nativeQuery = true)
	public List<User> getInactiveUsersOfUserExport(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId,@Param("search")String search); 
	
	@Query(value = "SELECT count(*) FROM tc_user_user "
			+ " inner join tc_users on tc_user_user.manageduserid=tc_users.id "
			+ "where tc_user_user.userid = :userId and delete_date is not  null ", nativeQuery = true)
	public Integer getInactiveUsersOfUserSize(@Param("userId") Long userId); 
	
	@Query(value = "SELECT tc_users.* FROM tc_user_user inner join tc_users on tc_user_user.manageduserid=tc_users.id where tc_user_user.userid = :userId and delete_date is null ", nativeQuery = true)
	public List<User> getChildrenOfUser(@Param("userId") Long userId);
	
	@Query(value = "SELECT tc_users.* FROM tc_user_user inner join tc_users on tc_user_user.manageduserid=tc_users.id where tc_user_user.userid = :userId ", nativeQuery = true)
	public List<User> getActiveAndInactiveChildrenOfUser(@Param("userId") Long userId);

	@Query(value = "SELECT count(*) FROM tc_user_user "
			+ " inner join tc_users on tc_user_user.manageduserid=tc_users.id "
			+ "where tc_user_user.userid = :userId and delete_date is null", nativeQuery = true)
	public Integer getUsersOfUserSize(@Param("userId") Long userId);
	
	@Query(value = "SELECT count(*) FROM tc_user_user "
			+ " inner join tc_users on tc_user_user.manageduserid=tc_users.id "
			+ "where tc_user_user.userid = :userId", nativeQuery = true)
	public Integer getAllUsersOfUserSize(@Param("userId") Long userId);
	
	

	
	@Query(value = "SELECT * from tc_users where delete_date is null and (email = :email or "
			+ "identity_num = :identityNum or commercial_num = :commercialNum or "
			+ "company_phone = :companyPhone or manager_phone = :managerPhone or "
			+ "manager_mobile = :managerMobile or (phone = :phone and (phone is not null and phone !='' )  ) ) ", nativeQuery = true)
	public List<User> checkUserDuplication(@Param("email") String email, @Param("identityNum")String identityNum,
			                               @Param("commercialNum")String commercialNum , @Param("companyPhone")String companyPhone,
			                               @Param("managerPhone")String managerPhone , @Param("managerMobile")String managerMobile,
			                               @Param("phone")String phone);
	
	@Query(value = "SELECT * from tc_users where delete_date is null and (email = :email or "
			+ "identity_num = :identityNum or "
			+ "company_phone = :companyPhone or  (phone = :phone and (phone is not null and phone !='' )  ) ) ", nativeQuery = true)
	public List<User> checkUserDuplicationIndvidual(@Param("email") String email, @Param("identityNum")String identityNum, 
			                                        @Param("companyPhone")String companyPhone,@Param("phone")String phone);
	
	
	@Modifying
    @Transactional
	@Query(value = "delete  from tc_user_user where manageduserid = :userId", nativeQuery = true )
	public void deleteUserOfUser(@Param("userId")Long deviceId);
	
	
	@Modifying
    @Transactional
	@Query(value = "update  tc_users set roleId = null where id = :userId", nativeQuery = true )
	public void removeRoleFromUser(@Param("userId")Long deviceId);
	
	@Query(value = "SELECT tc_users.id,tc_users.name FROM tc_user_user " + 
			" inner join tc_users on tc_user_user.manageduserid=tc_users.id" + 
			" where tc_user_user.userid = :userId and delete_date is null",nativeQuery = true)
	public List<UserSelect> getUserSelect(@Param("userId")Long userId);
	
	@Query(value = "SELECT tc_users.id,tc_users.name FROM tc_user_user " + 
			" inner join tc_users on tc_user_user.manageduserid=tc_users.id" + 
			" where tc_user_user.userid IN(:userIds) and delete_date is null",nativeQuery = true)
	public List<UserSelect> getUserSelectWithChild(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_users.id,tc_users.name FROM tc_user_user " + 
			" inner join tc_users on tc_user_user.manageduserid=tc_users.id" + 
			" where tc_user_user.userid = :userId and tc_users.accountType=2 and delete_date is null",nativeQuery = true)
	public List<UserSelect> getVendorSelect(@Param("userId")Long userId);
	
	@Query(value = "SELECT tc_users.id,tc_users.name FROM tc_user_user " + 
			" inner join tc_users on tc_user_user.manageduserid=tc_users.id" + 
			" where tc_user_user.userid = :vendorId and tc_users.accountType=3 and delete_date is null",nativeQuery = true)
	public List<UserSelect> getClientSelect(@Param("vendorId")Long vendorId);
	
	
	@Query(value = "SELECT * FROM tc_users where tc_users.id=:userId and tc_users.delete_date is not null", nativeQuery = true)
	public User getDeletedUser(@Param("userId")Long userId);
	
	@Query(value = "SELECT * FROM tc_users where roleId=:roleId and delete_date is null", nativeQuery = true)
	public List<User> getUsersAssignedByRoleId(@Param("roleId") Long roleId);
	
}
