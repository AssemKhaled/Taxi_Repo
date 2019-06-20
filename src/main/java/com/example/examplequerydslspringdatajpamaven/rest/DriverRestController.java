package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.photo.DecodePhoto;
import com.example.service.DriverServiceImpl;


@RestController
@RequestMapping(path = "/drivers")
public class DriverRestController {
	
	@Autowired
	DriverServiceImpl driverServiceImpl;

	@RequestMapping(value = "/")
	public ResponseEntity<?> noService1() {
		return ResponseEntity.ok("no service available");
		
	}
	@RequestMapping(value = "")
	public ResponseEntity<?> noService2() {
		return ResponseEntity.ok("no service available");
		
	}
	@RequestMapping(value = "/get_all_drivers/{userId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getDrivers(@PathVariable (value = "userId") Long id) {
		
		if(id != 0) {
			
			return ResponseEntity.ok(driverServiceImpl.getAllDrivers(id));

		}
		else {
			
			return ResponseEntity.ok("no user selected to get his own drivers");			
		
		}
		
		
	}
	
	@RequestMapping(value = "/get_driver_by_id/{driverId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getDriverById(@PathVariable (value = "driverId") Long driverId) {
		
		if(driverId != 0) {
			
			Driver driver=driverServiceImpl.getDriverById(driverId);
			if(driver != null) {
				if(driver.getDelete_date() == null) {
					return ResponseEntity.ok(driver);
				}
				else {
					return ResponseEntity.ok("no data for this driver may be deleted");

				}
			}
			else {
				return ResponseEntity.ok("no data for this driver");
			}
			
						
		}
		else {
			
			return ResponseEntity.ok("no driver selected");

		}
		
	}
	
	
	@RequestMapping(value = "/delete_driver/{driverId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> deleteDriver(@PathVariable (value = "driverId") Long driverId) {
		
		if(driverId != 0) {
			Driver res= driverServiceImpl.getDriverById(driverId);
			if(res != null) {
				
				driverServiceImpl.deleteDriver(driverId);
				return ResponseEntity.ok("Deleted successfully.");
				
			}
			else {

				return ResponseEntity.ok("not allow to delete this driver.");

			}
						
		}
		else {
			
			return ResponseEntity.ok("no driver selected");

		}
		
	}
	
	@RequestMapping(value = "/add_driver/{userId}", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> addDriver(@RequestBody Map<String, Object> driver,@PathVariable (value = "userId") Long id) {
		if(id != 0) {
			Driver queryData=new Driver();
			if(driver.get("name")!=null) {
				queryData.setName(driver.get("name").toString());
				
			}
			else {
				queryData.setName("");
			}
			
			if(driver.get("uniqueId")!=null) {
				queryData.setUniqueid(driver.get("uniqueId").toString());
				
			}
			else {
				queryData.setUniqueid("");
			}
			
			if(driver.get("mobileNum")!=null) {
				queryData.setMobile_num(driver.get("mobileNum").toString());
				
			}
			else {
				queryData.setMobile_num("");
			}
			
			if(driver.get("attributes")!=null) {
				queryData.setAttributes(driver.get("attributes").toString());
				
			}
			else {
				queryData.setAttributes("");
			}

			if(driver.get("dateType")!=null) {
				queryData.setDate_type(Integer.parseInt(driver.get("dateType").toString()));
				
			}
			else {
				queryData.setDate_type(null);
			}
			
			if(driver.get("day")!=null && driver.get("month")!=null && driver.get("year")!=null) {
				String date=driver.get("year")+"-"+driver.get("month")+"-"+driver.get("day");
				queryData.setBirth_date(date);
			}
			else {
				queryData.setBirth_date("");
			}
			if(driver.get("photo")!=null) {
				
				//base64_Image
				DecodePhoto decodePhoto=new DecodePhoto();
				queryData.setPhoto(decodePhoto.Base64_Image(driver.get("photo").toString()));				
				
			}
			else {
				queryData.setPhoto("Not-available.png");
			}	
			List<Driver> res=driverServiceImpl.checkDublicateDriverInAdd(id,queryData.getName(),queryData.getUniqueid(),queryData.getMobile_num());
			if(!res.isEmpty()) {
				String message="";
				for(int i=0;i<res.size();i++) {
					if(res.get(i).getName().equalsIgnoreCase(queryData.getName())) {
						message="name was add before";
													
					}
					if(res.get(i).getUniqueid().equalsIgnoreCase(queryData.getUniqueid())) {
						message= "uniqueId was add before";
												
					}
					else if(res.get(i).getMobile_num().equalsIgnoreCase(queryData.getMobile_num())) {
						message="mobile num was add before";
						
					}
					
				}
				return ResponseEntity.ok(message);

			}
			else {
				String resut=driverServiceImpl.addDriver(queryData,id);
				return ResponseEntity.ok(resut);

			
			}

		}
		else {
			
			return ResponseEntity.ok("no user selected to add his own driver");

			
		}
		
	}	
	@RequestMapping(value = "/edit_driver/{userId}", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> editDriver(@RequestBody Map<String, Object> driver,@PathVariable (value = "userId") Long id) {
		
		if(id != 0) {
			Driver queryData=new Driver();
			if(driver.get("name")!=null) {
				queryData.setName(driver.get("name").toString());
				
			}
			else {
				queryData.setName("");
			}
			
			if(driver.get("uniqueId")!=null) {
				queryData.setUniqueid(driver.get("uniqueId").toString());
				
			}
			else {
				queryData.setUniqueid("");
			}
			
			if(driver.get("mobileNum")!=null) {
				queryData.setMobile_num(driver.get("mobileNum").toString());
				
			}
			else {
				queryData.setMobile_num("");
			}
			
			if(driver.get("attributes")!=null) {
				queryData.setAttributes(driver.get("attributes").toString());
				
			}
			else {
				queryData.setAttributes("");
			}

			if(driver.get("dateType")!=null) {
				queryData.setDate_type(Integer.parseInt(driver.get("dateType").toString()));
				
			}
			else {
				queryData.setDate_type(null);
			}
			
			if(driver.get("day")!=null && driver.get("month")!=null && driver.get("year")!=null) {
				String date=driver.get("year")+"-"+driver.get("month")+"-"+driver.get("day");
				queryData.setBirth_date(date);
			}
			else {
				queryData.setBirth_date("");
			}
			if(driver.get("photo")!=null) {
				
				//base64_Image
				DecodePhoto decodePhoto=new DecodePhoto();
				queryData.setPhoto(decodePhoto.Base64_Image(driver.get("photo").toString()));				
				
			}
			else {
				queryData.setPhoto("Not-available.png");
			}	
			if(driver.get("driverId") != null) {
				Driver driverData=driverServiceImpl.getDriverById(Long.parseLong(driver.get("driverId").toString()));
				if(driverData == null) {
					
					return ResponseEntity.ok("no data for this driverId");

				}
				else {
					List<Driver> checkDublicateInEdit=driverServiceImpl.checkDublicateDriverInEdit(Long.parseLong(driver.get("driverId").toString()),id,queryData.getName(),queryData.getUniqueid(),queryData.getMobile_num());
					if(!checkDublicateInEdit.isEmpty()) {
						String message="";
						for(int i=0;i<checkDublicateInEdit.size();i++) {
							if(checkDublicateInEdit.get(i).getName().equalsIgnoreCase(queryData.getName())) {
								message="name was add before";
															
							}
							if(checkDublicateInEdit.get(i).getUniqueid().equalsIgnoreCase(queryData.getUniqueid())) {
								message= "uniqueId was add before";
														
							}
							else if(checkDublicateInEdit.get(i).getMobile_num().equalsIgnoreCase(queryData.getMobile_num())) {
								message="mobile num was add before";
								
							}
							
						}
						return ResponseEntity.ok(message);
						
					}
					else {
						//edit
						Long driverID=Long.parseLong(driver.get("driverId").toString());
						queryData.setId(driverID);
						driverServiceImpl.editDriver(queryData);
						return ResponseEntity.ok("Updated successfully.");

					}
					
				}
				
			}
			else {
				return ResponseEntity.ok("no driver to edit his data");				
			}
			
			
		}
		else {
			
			return ResponseEntity.ok("no user selected to edit his own driver");

			
		}
	}	
	

	
	

}
