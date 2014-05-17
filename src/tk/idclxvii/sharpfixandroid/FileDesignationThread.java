package tk.idclxvii.sharpfixandroid;

import java.io.*;
import java.util.*;
import android.os.*;
import java.net.*;
import java.security.*;
import android.app.*;

public class FileDesignationThread  implements Runnable, Serializable  {
	
	// CONSTRUCTORS
	public FileDesignationThread(){
		this.currentFile = new File(dirname);
		this.currentDir = dirname;
		this.t = new Thread(this);
		t.start();
		
	}
	
	public FileDesignationThread(File f, String currentDir){
		this.currentDir = currentDir;
		this.currentFile = f;
		this.t = new Thread(this);
		t.start();
		
	}
	
	// DATA STRUCTURE
	private File currentFile;
	private String currentDir = "";
	public  final String dirname = Environment.getExternalStorageDirectory().getAbsolutePath();
	private  int fileCount =0;
	private  int dirCount =0;
	private  ArrayList<String[]> hash = new ArrayList<String[]>();
	private  ArrayList ignore = new ArrayList();
	private  final String mime[][]  = {
	
	// 8 bytes Magic numbers
	
	{"FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFD8 FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFE0 0 10 4A 46","MIME: image/jpg","(*.jpeg, *.jpg) JPG Image Format"},
	{"FFFFFFFFFFFFFF89 50 4E 47 D A 1A A","MIME: image/png","(*.png) PNG Image Format"},
	{"50 4B 3 4 14 0 6 0","MIME: application/msoffice", "(*.docx,*.pptx, *.xlsx) Microsoft Office Document Format"},
	{"4C 0 0 0 1 14 2 0","MIME: application/shortcut", "(*.lnk) Microsoft Windows Application Link"},
	{"52 61 72 21 1A 7 0 FFFFFFFFFFFFFFCF","MIME: application/rar", "(*.rar) WinRAR Archive"},
	{"50 4B 3 4 A 0 0 0","MIME: application/zip","(*.zip) WinRAR Zip Archive"},
	{"50 4B 3 4 14 0 2 0","MIME: application/zip","(*.zip) WinRAR Zip Archive"},
	{"FFFFFFFFFFFFFFCA FFFFFFFFFFFFFFFE FFFFFFFFFFFFFFBA FFFFFFFFFFFFFFBE 0 0 0 33", "MIME: application/java-vm", "(*.class) Java Class Application File"},
	{"4D 5A FFFFFFFFFFFFFF90 0 3 0 0 0","application/win32", "(*.exe, *.dll) Microsoft Windows Executable/Application"},
	{"FFFFFFFFFFFFFFD0 FFFFFFFFFFFFFFCF 11 FFFFFFFFFFFFFFE0", "application/win32", "(*.msi) Microsoft Windows Application Installer"},
	{"3C 3F 78 6D 6C 20 76 65","MIME: application/xml", "(*.xml, *xspf) Extensible Markup Language File"},
	{"FFFFFFFFFFFFFFEF FFFFFFFFFFFFFFBB FFFFFFFFFFFFFFBF D A 4D 69 63","MIME: application/msvs","(*.sln) Microsoft Visual Studio Solution"},
	{"FFFFFFFFFFFFFFD0 FFFFFFFFFFFFFFCF 11 FFFFFFFFFFFFFFE0 FFFFFFFFFFFFFFA1 FFFFFFFFFFFFFFB1 1A FFFFFFFFFFFFFFE1","MIME: application/msvs","(*.suo, *.db) Microsoft Visual Studio Solution User Options | Database File"},
	{"4D 69 63 72 6F 73 6F 66", "MIME: application/win32", "(*.pdb) Microsoft Program Debug Database"},
	{"49 44 33 3 0 0 0 0","MIME: audio/mp3","(*.mp3) Audio File MP3 Format"},
	{"FFFFFFFFFFFFFFCF 77 54 FFFFFFFFFFFFFFAB 3 0 0 0","MIME: application/bin","(*.bin) Native Binary File"},
	{"0 FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF","MIME: application/img","(*.bin, *.img) Alcohol 120% CD Image"},
	{"43 49 53 4F 0 0 0 0","MIME: application/cso","(*.cso ) Sony Playstation Portable CSO Image"},
	{"FFFFFFFFFFFFFFE4 52 5C 7B FFFFFFFFFFFFFF8C FFFFFFFFFFFFFFD8 FFFFFFFFFFFFFFA7 4D","MIME: application/msonenote","(*.one Microsoft OneNote Section)"},
	{"64 38 3A 61 6E 6E 6F 75","MIME: application/torrent","(*.torrent) Torrent File"},
	{"38 42 50 53 0 1 0 0","MIME: application/psd","(*.psd) Adobe Photoshop Image File"},
	{"50 4B 3 4 14 0 8 8","MIME: application/java-vm","(*.jar, *.apk) Executable Java Archive File"},
	{"50 4B 3 4 14 0 8 0","MIME: application/java-vm","(*.jar) Java Archive File"},
	{"50 4B 3 4 14 0 0 0","MIME: application/java-vm","(*.jar) Java Archive File"},
	{"23 20 54 68 69 73 20 66","MIME: application/eclipse","(*.properties) Eclipse Project Properties Configuration File"},
	{"49 54 53 46 3 0 0 0","MIME: application/win32","(*.chm) Microsoft Windows Compiled HTML Help File"},
	{"0 0 0 20 66 74 79 70","MIME: video/mp4","(*.mp4) Video File MP4 Format"},
	{"21 3C 61 72 63 68 3E A","MIME: application/*","(*.lib, *.a, *.coff) Linux Executable Library |Unix Archiver Files | Microsoft Program Library Common Object File Format"},
	
	//{"25 50 44 46 2D 31 2E 33","MIME: unidentified/undefined","(*.pdf) Adobe Portable Document Format"},
	//{"25 50 44 46 2D 31 2E 34","MIME: unidentified/undefined","(*.pdf) Adobe Portable Document Format"},
	// {"25 50 44 46 2D 31 2E 35","MIME: unidentified/undefined","(*.pdf) Adobe Portable Document Format"},
	{"0 0 1 0 4 0 10 10","MIME: image/icon","(*.ico) Icon Image Format"},
	
	// 4 bytes Magic Numbers
	
	{"FFFFFFFFFFFFFFCA FFFFFFFFFFFFFFFE FFFFFFFFFFFFFFBA FFFFFFFFFFFFFFBE", "MIME: application/java-vm", "(*.class) Java Class Application File"},
	{"3C 72 65 73","MIME: application/xml", "(*.xml, *xspf) Extensible Markup Language File"},
	{"3C 6D 65 6E","MIME: application/xml", "(*.xml, *xspf) Extensible Markup Language File"},
	{"FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFD8 FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFE1","MIME: image/jpg","(*.jpeg, *.jpg) JPG Image Format"},
	{"52 49 46 46","MIME: audio/x-wav","(*.wav) Audio File Wave Format"},
	{"25 50 44 46","MIME: application/pdf","(*.pdf) Adobe Portable Document Format"},
	{"47 49 46 38","MIME: image/gif","(*.gif) Image GIF Format"},
	//{"50 4B 3 4","MIME: unidentified/undefined","(*.zip) WinRAR Zip Archive"},
	{"52 61 72 21","MIME: application/rar", "(*.rar) WinRAR Archive"},
	{"49 44 33 4","MIME: audio/mp3","(*.mp3) Audio File MP3 Format"},
	{"49 44 33 3","MIME: audio/mp3","(*.mp3) Audio File MP3 Format"},
	{"49 44 33 2","MIME: audio/mp3","(*.mp3) Audio File MP3 Format"},
	{"7F 45 4C 46","application/linux", "(*.o, *.*) Linux/Unix Executable and Linking File Format"},
	{"2F 2F 20 54","application/linux", "(*.*) Linux/Unix Binary File"},
	{"64 65 78 A","application/android", "(*.dex) Dalvik Executable File | Android Executable File"},
	
	
	};
	
	private final String TAG = "SHARPFIX FILE TYPE SCANNER\n";
	// HANDLERS:
	private Handler dstHandler = new Handler();
	private long dstInterval = 3000;
	
	// Thread Check
	private Thread t;
	
	public boolean isRunning(){
		return this.t.isAlive();
	}
	
	public int getFileCount(){
		return this.fileCount;
		
	}
	public int getDirCount(){
		return this.dirCount;
		
	}
	
	
	public void run(){
			checkDir(this.currentFile, this.currentDir);
			
	}			
	
	private void checkDir(File f,String currentDir){
		
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
							dirCount++;
							
							Thread.sleep(1000);
							checkDir(ff, currentDir + "/" + s[i]);
							
							// DirectoryScannerThread dst = new DirectoryScannerThread(ff,currentDir + "/" + s[i]);
							//dstHandler.postDelayed(dst,dstInterval);
							
							//new DirectoryScannerThread(ff,currentDir + "/" + s[i]);
							
							//MyService.requestNewThread(this, ff,currentDir + "/" + s[i] );
							
						}else{
							//hash.add(getMD5Checksum(currentDir + "/" + s[i]));
							
							Thread.sleep(1000);
							InputStream is = new BufferedInputStream(new FileInputStream(new File(currentDir + "/" + s[i])));
							String mimeType = URLConnection.guessContentTypeFromStream(is);
							
							// this.logger("< "+s[i] + " > is a file");
							
							// System.out.println("MD5 Checksum: "+getMD5Checksum(currentDir+"/"+s[i]));
							
							//this.logger("Java Virtual Machine MIME-Type Detection: " +mimeType);
							
							String mimeCode = getMagicNumber(ff,8);
							boolean found = false;
							for(int x=0; x < mime.length;x++){
								String currentMime = mime[x][0];
								if(mimeCode.equals(currentMime)){
									found = true;
									// this.logger("SharpFix MIME-Type Detection: "+ mime[x][1] + " " + mime[x][2]);
								}
							}
							
							
								
							/*
							
							// duplicate file detection
							// { "hash" , "file absolute path", "date last modified"}
							
							String currentHash = getMD5Checksum(currentDir + "/" + s[i]);
							long lastMod = ff.lastModified();
							String[][] temp = new String[hash.size()][3];
							hash.toArray(temp);
							boolean duplicate = false;
							for(int x =0; x < temp.length; x++){
								if(temp[x][0].equals(currentHash)){
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
									hash.add(new String[] {getMD5Checksum(currentDir + "/" + s[i]),
											currentDir + "/" + s[i],
									Long.toString(ff.lastModified())});
							}
							
							
							*/
							
							 if(!found){
								
								 // this.logger("Failed to get the file signature [8 bytes].\nRe-scan feature initiated [4 bytes]");
								 mimeCode = getMagicNumber(ff,4);
								 for(int x=0; x < mime.length;x++){
									 String currentMime = mime[x][0];
									 if(mimeCode.equals(currentMime)){
										 found = true;
										// this.logger("SharpFix MIME-Type Detection: "+ mime[x][1] + " " + mime[x][2]);
									 }
								 }
								 if(!found){
									 String report ="SHARPFIX INTEGRATED FILE TYPE DETECTION SYSTEM FAILED! Report Logs:\n"+
									"SharpFix MIME-Type Detection:\n"+"Unknown file type: " +ff.toString()+
									"\nFile Signature (4 bytes): " + getMagicNumber(ff,4)+
									"\nFile Signature (8 bytes): " + getMagicNumber(ff,8);
									// this.logger(report);
									
									  
								 }
								   
							 }
							
								
							 fileCount++;
							
						}
					}
				}else{
							
						Thread.sleep(1000);
						InputStream is = new BufferedInputStream(new FileInputStream(new File(f.toString())));
						String mimeType = URLConnection.guessContentTypeFromStream(is);
						
						// this.logger("< "+s[i] + " > is a file");
						
						// System.out.println("MD5 Checksum: "+getMD5Checksum(currentDir+"/"+s[i]));
						
						//this.logger("Java Virtual Machine MIME-Type Detection: " +mimeType);
						
						String mimeCode = getMagicNumber(f,8);
						boolean found = false;
						for(int x=0; x < mime.length;x++){
							String currentMime = mime[x][0];
							if(mimeCode.equals(currentMime)){
								found = true;
								// this.logger("SharpFix MIME-Type Detection: "+ mime[x][1] + " " + mime[x][2]);
							}
						}
						
						
							
						
						
						 if(!found){
							
							 // this.logger("Failed to get the file signature [8 bytes].\nRe-scan feature initiated [4 bytes]");
							 mimeCode = getMagicNumber(f,4);
							 for(int x=0; x < mime.length;x++){
								 String currentMime = mime[x][0];
								 if(mimeCode.equals(currentMime)){
									 found = true;
									// this.logger("SharpFix MIME-Type Detection: "+ mime[x][1] + " " + mime[x][2]);
								 }
							 }
							 if(!found){
								 String report ="SHARPFIX INTEGRATED FILE TYPE DETECTION SYSTEM FAILED! Report Logs:\n"+
								"SharpFix MIME-Type Detection:\n"+"Unknown file type: " +f.toString()+
								"\nFile Signature (4 bytes): " + getMagicNumber(f,4)+
								"\nFile Signature (8 bytes): " + getMagicNumber(f,8);
								// this.logger(report);
								
								  
							 }
							   
						 }
				}
			}catch(Exception e){
				this.logger("AN EXCEPTION HAS BEEN CAUGHT:" + currentDir + " " + f.toString() + "\n"  +e.toString()+ "\n");
				this.logger("Exiting Thread ["+this.t+"]");
				try{
					Thread.sleep(1000);
				}catch(InterruptedException ie){}
			}
		}
		
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
	
	public  String getMD5Checksum(String filename) throws Exception {
		   byte[] b = createChecksum(filename);
		   String result = "";

		   for (int i=0; i < b.length; i++) {
			   result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		   }
		   return result;
	}
	
	public  String getMagicNumber(File f, int bytes)throws Exception{
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
			String FILENAME = "SharpFixFDS.log";
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

	
	public  long getFolderSize(File directory) {
		long foldersize = 0;

		File[] currentFolder = directory.listFiles();

		for (int q = 0; q < currentFolder.length; q++) {
			if (currentFolder[q].isDirectory()) {
            //if folder run self on q'th folder - in which case the files.length will be counted for the files inside
            foldersize += getFolderSize(currentFolder[q]); 
			} else {
				//else get file size
				foldersize += currentFolder[q].length();
			}
		}
		return foldersize;
	}
	
	public  long getFolderSize(File[] selectedDirectories) {
		long foldersize = 0;

		for(int i = 0; i < selectedDirectories.length; i++){
			foldersize += getFolderSize(selectedDirectories[i]);
		}
		return foldersize;
	}

}