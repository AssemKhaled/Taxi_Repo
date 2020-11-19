package com.example.examplequerydslspringdatajpamaven.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Decode photos and icons to get name only saving in DB
 * @author fuinco
 *
 */
public class DecodePhoto {
	
	
	public Boolean deleteIcon(String icon,String modleType) {
		String path ="";
		if(modleType.equals("icon")) {
			path ="/var/www/html/sareb_photo/icons/"+icon;
		}
	   
		File file = new File(path);
		if (file.isFile()) {
			file.delete();
		}
		return true;
        
	}
	public List<Map> getAllIcons() {
		String path ="/var/www/html/sareb_photo/icons/default/";
		File folder = new File(path);

		File[] listOfFiles = folder.listFiles();
		List<Map> dataMap=new ArrayList<>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			
		    Map<String, String> list =new HashMap<String, String>();
			if (listOfFiles[i].isFile()) {
				list.put("icon",listOfFiles[i].getName());
			}
			dataMap.add(list);
		}
		
		return dataMap;
		
        
	}
	public Boolean checkIconDefault(String icon,String modleType) {
		String path ="";
		if(modleType.equals("icon")) {
			path ="/var/www/html/sareb_photo/icons/default/"+icon;
		}
	   
		File file = new File(path);
		if (file.isFile()) {
			return true;
		}
		else {
			return false;
		}
        
	}
	
	public Boolean deletePhoto(String photo,String modleType) {
		String path ="";
		if(modleType.equals("user")) {
			path ="/var/www/html/sareb_photo/user_photos/"+photo;
		}
		if(modleType.equals("driver")) {
			path ="/var/www/html/sareb_photo/driver_photos/"+photo;
		}
		if(modleType.equals("vehicle")) {
			path ="/var/www/html/sareb_photo/vehicle_photos/"+photo;
		}
		File file = new File(path);
		if (file.isFile()) {
			file.delete();
		}		
		return true;
	}
	public String Base64_Image(String photo,String modleType) {
		
		int pos1=photo.indexOf(":");
		int pos2=photo.indexOf(";");
		
		String type=photo.substring(pos1+1,pos2);
		
		if(type.equals("image/png"))
        {
        	type=".png";
        }
        if(type.equals("image/jpg"))
        {
        	type=".jpg";
        }
        if(type.equals("image/jpeg"))
        {
        	type=".jpeg";
        }
		Random rand = new Random();
		int n = rand.nextInt(999999);
		
		String fileName=n + type;
		String path ="";
		if(modleType.equals("user")) {
			path ="/var/www/html/sareb_photo/user_photos/"+fileName;
		}
		if(modleType.equals("driver")) {
			path ="/var/www/html/sareb_photo/driver_photos/"+fileName;
		}
		if(modleType.equals("vehicle")) {
			path ="/var/www/html/sareb_photo/vehicle_photos/"+fileName;
		}
		if(modleType.equals("icon")) {
			path ="/var/www/html/sareb_photo/icons/"+fileName;
		}
		if(modleType.equals("point")) {
			path ="/var/www/html/sareb_photo/points/"+fileName;
		}
		
		int pos=photo.indexOf(",");
		byte[] data=Base64.getDecoder().decode(photo.substring(pos+1));	

		try {
			OutputStream outputStream = new FileOutputStream(path);
			try {
				outputStream.write(data);
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return fileName;
		
	}

}
