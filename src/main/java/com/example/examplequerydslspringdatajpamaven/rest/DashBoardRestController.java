package com.example.examplequerydslspringdatajpamaven.rest;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.ReportServiceImpl;

/**
 * Services of Dashboard component
 * @author fuinco
 *
 */
@CrossOrigin
//@Component
@RestController
@RequestMapping(path = "/home")
public class DashBoardRestController {
	
	@Autowired
	private DeviceServiceImpl deviceService;
		
	@Autowired
	private ReportServiceImpl reportServiceImpl;

	@Autowired
	private UserRepository userRepository;

	private final DashboardService dashboardService;

	public DashBoardRestController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}


	@GetMapping(path ="/getDevicesStatuesAndAllDrivers")
	public ResponseEntity<?> devicesStatuesAndAllDrivers(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return deviceService.getDeviceStatus(TOKEN,userId);
	}
	
	@GetMapping(path = "/getAllDevicesLastInfo")
	public ResponseEntity<?> getAllDevicesLastInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId,
												   @RequestParam (value = "offset", defaultValue = "0")int offset,
												   @RequestParam (value = "search", defaultValue = "") String search ){
		return deviceService.getAllDeviceLiveData(TOKEN,userId, offset, search);

	}
	
	@GetMapping(path = "/getAllDevicesLastInfoMap")
	public ResponseEntity<?> getAllDevicesLastInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId
			                                       ){		
		return deviceService.getAllDeviceLiveDataMap(TOKEN,userId);
	}


	@RequestMapping(value = "/getNotifications", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotifications(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId,
															@RequestParam (value = "offset", defaultValue = "0") int offset,
															@RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  reportServiceImpl.getNotifications(TOKEN,userId, offset,search);

	}
	@RequestMapping(value = "/vehicleInfo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> vehicleInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                           @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                           @RequestParam(value = "userId",defaultValue = "0")Long userId){
		

		
    	return  deviceService.vehicleInfo(TOKEN,deviceId,userId);

	}



	@RequestMapping(value = "/dashboardStatisticsDriverStatus", method = RequestMethod.GET)
	public ResponseEntity<?> getDashboardStatisticsDriverStatus(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam(value = "userId",defaultValue = "0")Long userId){
		return dashboardService.getDashboardStatisticsDriverStatus(TOKEN, userId);
	}



}
