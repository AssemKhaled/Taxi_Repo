package com.example.examplequerydslspringdatajpamaven.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Random;

public class DecodePhoto {
	
	
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
		
		return file.delete();
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
