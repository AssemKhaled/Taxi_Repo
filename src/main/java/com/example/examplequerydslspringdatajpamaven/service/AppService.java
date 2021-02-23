package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;

/**
 * Interface of function of app
 * @author fuinco
 *
 */
@Service
public interface AppService {
	public Boolean logoutTraccarApp(String email , String password);
	public Boolean loginTraccarApp(String authorization);
	public ResponseEntity<?> loginApp(String authorization);
	public ResponseEntity<?> logoutApp(String TOKEN);

	public ResponseEntity<?> getAllDeviceLiveDataMapApp(String TOKEN,Long userId);
	public ResponseEntity<?> vehicleInfoApp(String TOKEN,Long deviceId,Long userId);

	public ResponseEntity<?>  getDevicesListApp(String TOKEN,Long userId , int offset, String search);
	public ResponseEntity<?> createDeviceApp(String TOKEN,Device device,Long userId);
	public ResponseEntity<?> editDeviceApp(String TOKEN,Device device,Long userId);
	public ResponseEntity<?> deleteDeviceApp(String TOKEN,Long userId, Long deviceId);
	public ResponseEntity<?>  findDeviceByIdApp(String TOKEN,Long deviceId,Long userId);
	public ResponseEntity<?> assignDeviceToDriverApp(String TOKEN,Long deviceId , Long driverId , Long userId);
	public ResponseEntity<?> assignGeofencesToDeviceApp(String TOKEN,Long deviceId,Long [] geoIds,Long userId );
	public ResponseEntity<?> getDeviceDriverApp(String TOKEN,Long deviceId);	
	public ResponseEntity<?> getDeviceGeofencesApp(String TOKEN,Long deviceId);
	public ResponseEntity<?> getDeviceSelectApp(String TOKEN,Long userId);

	public ResponseEntity<?> getAllDriversApp(String TOKEN,Long id,int offset,String search);
	public ResponseEntity<?> getDriverByIdApp(String TOKEN,Long driverId,Long userId);
	public ResponseEntity<?> deleteDriverApp(String TOKEN,Long driverId, Long userId);
	public ResponseEntity<?> addDriverApp(String TOKEN,Driver driver,Long id);
	public ResponseEntity<?> editDriverApp(String TOKEN,Driver driver,Long id);
	public ResponseEntity<?> getUnassignedDriversApp(String TOKEN,Long userId);
	public ResponseEntity<?> getDriverSelectApp(String TOKEN,Long userId);

	
	public ResponseEntity<?> getGeoListApp(String TOKEN,Long id,int offset,String search);
	public ResponseEntity<?> getGeofenceByIdApp(String TOKEN,Long geofenceId,Long userId);
	public ResponseEntity<?> deleteGeofenceApp(String TOKEN,Long geofenceId,Long userId);
	public ResponseEntity<?> addGeofenceApp(String TOKEN,Geofence geofence,Long id);
	public ResponseEntity<?> editGeofenceApp(String TOKEN,Geofence geofence,Long id);
	public ResponseEntity<?> getGeofenceSelectApp(String TOKEN,Long userId);

	public ResponseEntity<?> getStopsReportApp(String TOKEN,Long [] deviceId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> getTripsReportApp(String TOKEN, Long [] deviceId, Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> getSummaryReportApp(String TOKEN, Long [] deviceId, Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> viewTripApp(String TOKEN,Long deviceId,String startTime,String endTime);
	public ResponseEntity<?> returnFromTraccarApp(String url,String report,List<Long> allDevices,String from,String to,String type,int page,int start,int limit);
	public ResponseEntity<?> getEventsReportApp(String TOKEN,Long [] deviceId,Long [] groupId,int offset,String start,String end,String type,String search,Long userId);
	public ResponseEntity<?> getDeviceWorkingHoursApp(String TOKEN,Long [] deviceId,Long [] groupId,int offset,String start,String end,String search,Long userId);
	public ResponseEntity<?> getCustomReportApp(String TOKEN,Long [] deviceId,Long [] groupId,int offset,String start,String end,String search,Long userId,String custom,String  value);
    public ResponseEntity<?> getDriverWorkingHoursApp(String TOKEN,Long [] driverId,Long [] groupId,int offset,String start,String end,String search,Long userId);
	public ResponseEntity<?> getDriveMoreThanReportApp(String TOKEN,Long [] deviceId,Long [] driverId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> getEventsReportByTypeApp(String TOKEN,Long [] deviceId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> getSensorsReportApp(String TOKEN,Long [] deviceId,Long [] groupId,int offset,String start,String end,String search,Long userId);
	public ResponseEntity<?> getNumTripsReportApp(String TOKEN,Long [] deviceId,Long [] driverId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> getNumStopsReportApp(String TOKEN,Long [] deviceId,Long [] driverId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> geTotalTripsReportApp(String TOKEN,Long [] deviceId,Long [] driverId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> getTotalStopsReportApp(String TOKEN,Long [] deviceId,Long [] driverId,Long [] groupId,String type,String from,String to,int page,int start,int limit,Long userId);
	public ResponseEntity<?> getNumberDriverWorkingHoursApp(String TOKEN,Long [] driverId,Long [] groupId,int offset,String start,String end,String search,Long userId);

	public ResponseEntity<?> updateProfilePhotoApp(String TOKEN,Map<String, String> data,Long userId);
	public ResponseEntity<?> getUserInfoApp(String TOKEN,Long userId);
	public ResponseEntity<?> updateProfilePasswordApp(String TOKEN,Map<String, String> data,String check,Long userId);

	public ResponseEntity<?> getStatusApp(String TOKEN,Long userId);
	public ResponseEntity<?> getMergeHoursIgnitionApp(String TOKEN,Long userId);
	public ResponseEntity<?> getDistanceFuelEngineApp(String TOKEN,Long userId);
	public ResponseEntity<?> getNotificationsChartApp(String TOKEN,Long userId);
	
	public ResponseEntity<?> registerToken(String TOKEN,Map<Object, Object> data);
	public ResponseEntity<?> logoutTokenApp(String TOKEN,Map<Object, Object> data);

}	

