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
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.service.GeofenceServiceImpl;

@RestController
@RequestMapping(path = "/geofences")
public class GeofenceRestController {
	
	@Autowired
	GeofenceServiceImpl geofenceServiceImpl;

	@RequestMapping(value = "/")
	public ResponseEntity<?> noService1() {
		return ResponseEntity.ok("no service available");
		
	}
	@RequestMapping(value = "")
	public ResponseEntity<?> noService2() {
		return ResponseEntity.ok("no service available");
		
	}
	
	@RequestMapping(value = "/get_all_geofences/{userId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getGeofences(@PathVariable (value = "userId") Long id) {
		
		if(id != 0) {
			
			return ResponseEntity.ok(geofenceServiceImpl.getAllGeofences(id));

		}
		else{
			
			return ResponseEntity.ok("no user selected to get his own geofences");			
		
		}
		
		
	}
	
	@RequestMapping(value = "/get_geofence_by_id/{geofenceId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getGeofenceById(@PathVariable (value = "geofenceId") Long geofenceId) {
		
		if(geofenceId != 0) {
			
			

			Geofence geofence=geofenceServiceImpl.getGeofenceById(geofenceId);
			if(geofence != null) {
				if(geofence.getDelete_date() == null) {
					return ResponseEntity.ok(geofence);
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
			
			return ResponseEntity.ok("no geofence selected");

		}
		
	}
	
	@RequestMapping(value = "/delete_geofence/{geofenceId}", method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> deleteDriver(@PathVariable (value = "geofenceId") Long geofenceId) {
		
		if(geofenceId != 0) {
			Geofence res= geofenceServiceImpl.getGeofenceById(geofenceId);
			if(res != null) {
				
				geofenceServiceImpl.deleteGeofence(geofenceId);
				return ResponseEntity.ok("Deleted successfully.");
				
			}
			else {

				return ResponseEntity.ok("not allow to delete this geofence.");

			}
						
		}
		else {
			
			return ResponseEntity.ok("no geofence selected");

		}
		
	}
	
	@RequestMapping(value = "/add_geofence/{userId}", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> addDriver(@RequestBody Map<String, Object> geofence,@PathVariable (value = "userId") Long id) {
		if(id != 0) {
			
            Geofence queryData=new Geofence();
            
			if(geofence.get("name")!=null) {
				queryData.setName(geofence.get("name").toString());
				
			}
			else {
				queryData.setName("");
			}
			
			if(geofence.get("area")!=null) {
				queryData.setArea(geofence.get("area").toString());
				
			}
			else {
				queryData.setArea("");
			}
			
			if(geofence.get("type")!=null) {
				queryData.setType(geofence.get("type").toString());
				
			}
			else {
				queryData.setType("");
			}
			
			List<Geofence> res=geofenceServiceImpl.checkDublicateGeofenceInAdd(id,queryData.getName());
			if(!res.isEmpty()) {
				String message="";
				for(int i=0;i<res.size();i++) {
					if(res.get(i).getName().equalsIgnoreCase(queryData.getName())) {
						message="name was add before";
													
					}
				}
				return ResponseEntity.ok(message);

			}
			else {
				String resut=geofenceServiceImpl.addGeofence(queryData,id);
				return ResponseEntity.ok(resut);

			}
			
			
		}
		else {
			
			return ResponseEntity.ok("no user selected to add his own geofence");

			
		}
		
	}
	
	@RequestMapping(value = "/edit_geofence/{userId}", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> editGeofence(@RequestBody Map<String, Object> geofence,@PathVariable (value = "userId") Long id) {
		
		if(id != 0) {
			
		    Geofence queryData=new Geofence();
        
			if(geofence.get("name")!=null) {
				queryData.setName(geofence.get("name").toString());
				
			}
			else {
				queryData.setName("");
			}
			
			if(geofence.get("area")!=null) {
				queryData.setArea(geofence.get("area").toString());
				
			}
			else {
				queryData.setArea("");
			}
			
			if(geofence.get("type")!=null) {
				queryData.setType(geofence.get("type").toString());
			}
			else {
				queryData.setType("");
			}
            if(geofence.get("geofenceId") != null) {
    			Geofence checkAvaliable=geofenceServiceImpl.getGeofenceById(Long.parseLong(geofence.get("geofenceId").toString()));
    			if(checkAvaliable == null) {
    				
    				return ResponseEntity.ok("no data for this geofenceId");

    			}
    			else {
    				List<Geofence> checkDublicateInEdit=geofenceServiceImpl.checkDublicateGeofenceInEdit(Long.parseLong(geofence.get("geofenceId").toString()),id,queryData.getName());
    				if(!checkDublicateInEdit.isEmpty()) {
    					String message="";
    					for(int i=0;i<checkDublicateInEdit.size();i++) {
    						if(checkDublicateInEdit.get(i).getName().equalsIgnoreCase(queryData.getName())) {
    							message="name was add before";
    														
    						}
    						
    						
    					}
    					return ResponseEntity.ok(message);
    					
    				}
    				else {
    					queryData.setId(Long.parseLong(geofence.get("geofenceId").toString()));
    					geofenceServiceImpl.editGeofence(queryData);
    					return ResponseEntity.ok("Updated successfully.");

    				}

    				
    			}

			}
            else {
				return ResponseEntity.ok("no geofence to edit.");

            }
			
		}
		else {
			
			return ResponseEntity.ok("no user selected to edit his own driver");

			
		}
	}	

}
