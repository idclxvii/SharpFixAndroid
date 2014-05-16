package com.freetzi.idclxvii.sharpfixandroid;

import java.security.MessageDigest;

public class Security {
	
	public static String md5Hash(String plain){
  		StringBuffer sb = new StringBuffer();
  		try{
  			MessageDigest md =  MessageDigest.getInstance("MD5");
  			md.update(plain.getBytes());
  			byte byteData[] = md.digest();
  			 
  	        //convert the byte to hex format method 1
  	        
  	        for (int i = 0; i < byteData.length; i++) {
  	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
  	        }
  	 
  			
  		}catch(Exception e){
  			
  		}
  		
  		return sb.toString();
  	}
}
