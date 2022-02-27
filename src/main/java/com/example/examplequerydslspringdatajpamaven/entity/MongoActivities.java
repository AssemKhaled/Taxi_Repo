package com.example.examplequerydslspringdatajpamaven.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.util.Date;

@Document(collection = "tc_activities")
public class MongoActivities {

    @Id
    @JsonIgnore
    private ObjectId _id;

    @Field("driver_id")
    private Integer driverId;

    @Field("company_id")
    private Long companyId;

    @Field("activity_type")
    private String activityType;

    @Field("attributes")
    private Object attributes;

    @Field("activity_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date activityTime;

    public MongoActivities(ObjectId _id, Integer driverId, Long companyId, String activityType, Object attributes, Date activityTime) {
        this._id = _id;
        this.driverId = driverId;
        this.companyId = companyId;
        this.activityType = activityType;
        this.attributes = attributes;
        this.activityTime = activityTime;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Object getAttributes() {
        return attributes;
    }

    public void setAttributes(Object attributes) {
        this.attributes = attributes;
    }

    public Date getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Date activityTime) {
        this.activityTime = activityTime;
    }
}
