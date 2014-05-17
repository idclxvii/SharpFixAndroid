package tk.idclxvii.sharpfixandroid;

import java.io.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;

import android.os.*;

public class FileDuplicationDetectionThread  implements Runnable, Serializable{
	
	
	public FileDuplicationDetectionThread(){
		this.currentFile = new File(dirname);
		this.currentDir = dirname;
		this.t = new Thread(this);
		t.start();
		
	}
	
	public FileDuplicationDetectionThread(File f, String currentDir){
		this.currentDir = currentDir;
		this.currentFile = f;
		this.t = new Thread(this);
		t.start();
		
	}
	private final String TAG = "SHARPFIX DUPLICATE FILE SCANNER\n";
	public  final String dirname = Environment.getExternalStorageDirectory().getAbsolutePath();
	private  ArrayList<String[]> hash = new ArrayList<String[]>();
	private File currentFile;
	private String currentDir = "";
	
	
	private Thread t;
	private int duplicateCount = 0;
	public boolean isRunning(){
		return this.t.isAlive();
	}
	
	public int getDuplicateCount(){
		return this.duplicateCount;
		
	}
	
	public void run(){
			checkDir(this.currentFile, this.currentDir);
			
	}			
	
	
	private void checkDir(File f, String currentDir){
		
		if(!f.canRead() || f.isHidden()){
			this.logger("SHARPFIX cannot open the directory:\n"+f.toString());
		}else{
			try{
				// this.logger( "Current directory: " + currentDir);
				
				if (f.isDirectory()) {
					
					String s[] = f.list();
					for (int i=0; i < s.length; i++) {
						File ff = new File(currentDir + "/" + s[i]);
						if (ff.isDirectory()) {
							//System.out.println(s[i] + " is a directory");
							Thread.sleep(1000);
							checkDir(ff, currentDir + "/" + s[i]);
							//DuplicateFilesScannerThread dfst = new DuplicateFilesScannerThread(ff,currentDir + "/" + s[i]);
							//dfstHandler.postDelayed(dfst,dfstInterval);
							
							//new DuplicateFilesScannerThread(ff,currentDir + "/" + s[i]);
							//MyService.requestNewThread(this, ff,currentDir + "/" + s[i]);
							
						
						}else{
							//hash.add(getMD5Checksum(currentDir + "/" + s[i]));
							Thread.sleep(1000);
							// this.logger("< "+s[i] + " > is a file");
								
							
							// duplicate file detection
							// { "hash" , "file absolute path", "date last modified"}
							
							String currentHash = /*getMD5Checksum*/getCRC32Checksum(currentDir + "/" + s[i]);
							long lastMod = ff.lastModified();
							String[][] temp = new String[hash.size()][3];
							hash.toArray(temp);
							boolean duplicate = false;
							for(int x =0; x < temp.length; x++){
								
								if(temp[x][0].equals(currentHash)){
									this.duplicateCount++;
									if(lastMod > Long.parseLong(temp[x][2])){
										// ff.delete();
										duplicate = true;
										this.logger("SHARPFIX INTEGRATED DETECTION SYSTEM DETECTED DUPLICATE FILES! Report Logs:" +
												"FILE #1: " + ff.toString() + "\n" +
												"HASH #1 :"+ currentHash + "\n" +
												"LAST MODIFIED: " +lastMod + "\n" +
												"----------------------------------\n" +
												"FILE #2: " + temp[x][1] + "\n" +
												"HASH #2 :"+ temp[x][0] + "\n" +
												"LAST MODIFIED: " + temp[x][2] + "\n");
									}else{
										
										// (new File(temp[x][1])).delete();
										duplicate = true;
										this.logger("SHARPFIX INTEGRATED DETECTION SYSTEM DETECTED DUPLICATE FILES! Report Logs:" +
												"FILE #1: " + ff.toString() + "\n" +
												"HASH #1 :"+ currentHash + "\n" +
												"LAST MODIFIED: " +lastMod + "\n" +
												"----------------------------------\n" +
												"FILE #2: " + temp[x][1] + "\n" +
												"HASH #2 :"+ temp[x][0] + "\n" +
												"LAST MODIFIED: " + temp[x][2] + "\n");
									}
								}
							}
							if(!duplicate){
									hash.add(new String[] {/*getMD5Checksum*/getCRC32Checksum(currentDir + "/" + s[i]),
											currentDir + "/" + s[i],
									Long.toString(ff.lastModified())});
							}
							
						}
					}
	
				}else{
				//	this.logger(dirname + " is not a directory");
					Thread.sleep(1000);
					// this.logger("< "+s[i] + " > is a file");
						
					
					// duplicate file detection
					// { "hash" , "file absolute path", "date last modified"}
					File ff = new File(f.toString());
					String currentHash = /*getMD5Checksum*/getCRC32Checksum(ff.toString());
					long lastMod = ff.lastModified();
					String[][] temp = new String[hash.size()][3];
					hash.toArray(temp);
					boolean duplicate = false;
					for(int x =0; x < temp.length; x++){
						
						if(temp[x][0].equals(currentHash)){
							this.duplicateCount++;
							if(lastMod > Long.parseLong(temp[x][2])){
								// ff.delete();
								duplicate = true;
								this.logger("SHARPFIX INTEGRATED DETECTION SYSTEM DETECTED DUPLICATE FILES! Report Logs:" +
										"FILE #1: " + ff.toString() + "\n" +
										"HASH #1 :"+ currentHash + "\n" +
										"LAST MODIFIED: " +lastMod + "\n" +
										"----------------------------------\n" +
										"FILE #2: " + temp[x][1] + "\n" +
										"HASH #2 :"+ temp[x][0] + "\n" +
										"LAST MODIFIED: " + temp[x][2] + "\n");
							}else{
								
								// (new File(temp[x][1])).delete();
								duplicate = true;
								this.logger("SHARPFIX INTEGRATED DETECTION SYSTEM DETECTED DUPLICATE FILES! Report Logs:" +
										"FILE #1: " + ff.toString() + "\n" +
										"HASH #1 :"+ currentHash + "\n" +
										"LAST MODIFIED: " +lastMod + "\n" +
										"----------------------------------\n" +
										"FILE #2: " + temp[x][1] + "\n" +
										"HASH #2 :"+ temp[x][0] + "\n" +
										"LAST MODIFIED: " + temp[x][2] + "\n");
							}
						}
					}
					if(!duplicate){
							this.hash.add(new String[] {/*getMD5Checksum*/getCRC32Checksum(ff.toString()),
									ff.toString(),
							Long.toString(ff.lastModified())});
					}
				}
				
			}catch(Exception e){
				this.logger("AN EXCEPTION HAS BEEN CAUGHT:" + currentDir + " " + f.toString() + "\n" +e.toString() + "\n");
				this.logger("Exiting Thread ["+this.t+"]");
				try{
					Thread.sleep(1000);
				}catch(InterruptedException ie){}
			}
		}
		
		
	}
	
	private void logger(String msg ){
		
		try{
			
			String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
			//String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			/*
			if( (new File(SDCARD+File.separator+Integer.toString(logFileName) + ".log").length()) > 256000 ){
				logFileName++;
			}
			String FILENAME = Integer.toString(logFileName) + ".log";
			*/
			String FILENAME = "SharpFixFDDS.log";
			File outfile = new File(SDCARD+File.separator+FILENAME);
			if(outfile.exists()){
				FileOutputStream fos = new FileOutputStream(outfile,true);
				fos.write((this.TAG + msg +"\n").getBytes());
	   		  	fos.close();
			
				 
				 
			}else{
				outfile.createNewFile();
				FileOutputStream fos = new FileOutputStream(outfile,true);
				fos.write((this.TAG + msg + "\n").getBytes());
				fos.close();
	   		 
			 }
		}catch(Exception e){
			//Ingore errors
		}
	}
	
	public String getCRC32Checksum(String filename) throws Exception{

		 InputStream fis =  new FileInputStream(filename);

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
	
	
	
	
	public String getMD5Checksum(String filename) throws Exception {
		   byte[] b = createChecksum(filename);
		   String result = "";

		   for (int i=0; i < b.length; i++) {
			   result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		   }
		   return result;
	}
	
	public  byte[] createChecksum(String filename) throws Exception {
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


}
