package tk.idclxvii.sharpfixandroid;

import java.io.*;
import java.security.*;
import java.util.zip.*;

public class Security {
	private final String TAG = this.getClass().getSimpleName();
	
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
	
	
	
	public  static String getMagicNumber(File f, int bytes)throws Exception{
		InputStream fis =  new FileInputStream(f.toString());
		String result = "";
		byte[] buffer = new byte[bytes];
		fis.read(buffer);
		for(int x = 0; x < buffer.length; x++){
			result += (Long.toHexString(buffer[x])).toUpperCase();
			if(x < buffer.length-1){
				result += " ";
			}
		}
		fis.close();
		return result;
	}
	
	public  static String getMD5Checksum(String filename) throws FileNotFoundException, NoSuchAlgorithmException, IOException     {
		   byte[] b = createMD5Checksum(filename);
		   String result = "";

		   for (int i=0; i < b.length; i++) {
			   result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		   }
		   return result;
	}
	
	public  static byte[] createMD5Checksum(String filename)  throws FileNotFoundException,
		IOException, NoSuchAlgorithmException{
		   InputStream fis =  new FileInputStream(filename);

		   byte[] buffer = new byte[1024];
		   MessageDigest complete = MessageDigest.getInstance("MD5");
		   int numRead;

		   do {
			   numRead = fis.read(buffer);
			   if (numRead > 0) {
				   complete.update(buffer, 0, numRead);
			   }
		   } while (numRead != -1);

		   fis.close();
		   return complete.digest();
	}
	
	
	public  static String getSHA1Checksum(String filename) throws FileNotFoundException, NoSuchAlgorithmException, IOException  {
		   byte[] b = createSHA1Checksum(filename);
		   String result = "";

		   for (int i=0; i < b.length; i++) {
			   result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		   }
		   return result;
	}
	
	public  static byte[] createSHA1Checksum(String filename) throws FileNotFoundException,
			IOException, NoSuchAlgorithmException   {
		   InputStream fis =  new FileInputStream(filename);

		   byte[] buffer = new byte[1024];
		   MessageDigest complete = MessageDigest.getInstance("SHA1");
		   int numRead;

		   do {
			   numRead = fis.read(buffer);
			   if (numRead > 0) {
				   complete.update(buffer, 0, numRead);
			   }
		   } while (numRead != -1);

		   fis.close();
		   return complete.digest();
	}
	
	public static String getCRC32Checksum(String filename) throws IOException, FileNotFoundException {

		 InputStream fis =  new BufferedInputStream(new FileInputStream(filename));

		   byte[] buffer = new byte[1024];
		   CRC32 crc = new CRC32(); 
		   int numRead;

		   do {
			   numRead = fis.read(buffer);
			   if (numRead > 0) {
				   crc.update(buffer, 0, numRead);
			   }
		   } while (numRead != -1);

		   fis.close();
		return Long.toHexString(crc.getValue());
	}
}
