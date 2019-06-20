package com.example.examplequerydslspringdatajpamaven.photo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Random;

public class DecodePhoto {
	
public String Base64_Image(String photo) {
		
		int pos1=photo.indexOf(":");
		int pos2=photo.indexOf(";");
		
		String type=photo.substring(pos1+1,pos2);
		
		if(type.equalsIgnoreCase("image/png"))
        {
        	type=".png";
        }
        else
        {
        	type=".jpg";
        }
		Random rand = new Random();
		int n = rand.nextInt(999999);
		
		String fileName=n + type;
		String path="E:/"+fileName;
		

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
