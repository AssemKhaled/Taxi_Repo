package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.examplequerydslspringdatajpamaven.entity.Attribute;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;

/**
 * Queries on table tc_attributes
 * @author fuinco
 *
 */
@Component
public interface ComputedRepository  extends  JpaRepository<Attribute, Long>, QueryDslPredicateExecutor<Attribute>{

	@Query(value = "SELECT tc_attributes.* FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userIds) and  tc_attributes.delete_date is null"
			+ " and ( (tc_attributes.type Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Attribute> getAllComputed(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(value = "SELECT tc_attributes.* FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userIds) and  tc_attributes.delete_date is null"
			+ " and ( (tc_attributes.type Like %:search%) ) ", nativeQuery = true)
	public List<Attribute> getAllComputedExport(@Param("userIds")List<Long> userIds,@Param("search") String search);

	@Query(value = "SELECT tc_attributes.* FROM tc_attributes "
			+ " WHERE tc_attributes.id IN(:computedIds) and  tc_attributes.delete_date is null "
			+ " and ( (tc_attributes.type Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Attribute> getAllComputedByIds(@Param("computedIds")List<Long> computedIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_attributes.* FROM tc_attributes "
			+ " WHERE tc_attributes.id IN(:computedIds) and  tc_attributes.delete_date is null "
			+ " and ( (tc_attributes.type Like %:search%) ) ", nativeQuery = true)
	public List<Attribute> getAllComputedByIdsExport(@Param("computedIds")List<Long> computedIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id " + 
			"  WHERE tc_user_attribute.userid IN(:userIds) and  tc_attributes.delete_date is null", nativeQuery = true)
	public Integer getAllComputedSize(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT count(*) FROM tc_attributes " + 
			"  WHERE tc_attributes.id IN(:computedIds) and tc_attributes.delete_date is null", nativeQuery = true)
	public Integer getAllComputedSizeByIds(@Param("computedIds")List<Long> computedIds);
	
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_device_attribute where tc_device_attribute.attributeid=:attributeId", nativeQuery = true)
	public void deleteAttributeDeviceId(@Param("attributeId") Long attributeId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_group_attribute where tc_group_attribute.attributeid=:attributeId", nativeQuery = true)
	public void deleteAttributeGroupId(@Param("attributeId") Long attributeId);
	
	@Query(value = "SELECT tc_attributes.id,tc_attributes.description FROM tc_attributes"
			+ " INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userIds) and tc_attributes.delete_date is null",nativeQuery = true)
	public List<DriverSelect> getComputedSelect(@Param("userIds") List<Long> userIds);
	
	@Query(value = "SELECT tc_attributes.id,tc_attributes.description FROM tc_attributes"
			+ " INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:loggedUserId) and tc_attributes.delete_date is null"
			+ " and tc_attributes.id Not IN(Select tc_user_client_computed.computedid from tc_user_client_computed where tc_user_client_computed.userid !=:userId ) ",nativeQuery = true)
	public List<DriverSelect> getComputedUnSelectOfClient(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId);
	
	@Query(value = "SELECT tc_attributes.id,tc_attributes.description FROM tc_attributes"
			+ " WHERE tc_attributes.id IN(:computedIds) and tc_attributes.delete_date is null",nativeQuery = true)
	public List<DriverSelect> getComputedSelectByIds(@Param("computedIds") List<Long> computedIds);
	
	
	@Query(value = "SELECT tc_attributes.id FROM tc_attributes"
			+ " INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userId) and tc_attributes.delete_date is null"
			+ " and tc_attributes.description=:description and tc_attributes.type=:type order by tc_attributes.id DESC limit 0,1",nativeQuery = true)
	public Long getComputedIdByName(@Param("userId") Long userId,@Param("description") String description,@Param("type") String type);
	
	@Query(value = "SELECT tc_attributes.* FROM tc_attributes "
			+ " INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userId) and  tc_attributes.delete_date is null"
			+ " and tc_attributes.description =:description", nativeQuery = true)
	public List<Attribute> checkDuplicationAdd(@Param("userId")Long userId,@Param("description") String description);
	
	@Query(value = "SELECT tc_attributes.* FROM tc_attributes "
			+ " INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userId) and  tc_attributes.delete_date is null"
			+ " and tc_attributes.description =:description and tc_attributes.id !=:id", nativeQuery = true)
	public List<Attribute> checkDuplicationEdit(@Param("userId")Long userId,@Param("id")Long id,@Param("description") String description);

	
}
