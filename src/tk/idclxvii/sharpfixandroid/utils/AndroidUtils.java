package tk.idclxvii.sharpfixandroid.utils;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import tk.idclxvii.sharpfixandroid.GlobalAsyncTask;
import tk.idclxvii.sharpfixandroid.MainMenuActivity;
import tk.idclxvii.sharpfixandroid.SharpFixApplicationClass;
import tk.idclxvii.sharpfixandroid.databasemodel.ModelFdSettings;
import tk.idclxvii.sharpfixandroid.serverutils.MailSender;
import tk.idclxvii.sharpfixandroid.serverutils.ServerCommunication;

import android.content.*;
import android.net.*;
import android.os.*;
import android.util.Log;

public abstract class AndroidUtils {
	
	private final String TAG = this.getClass().getSimpleName();
	
	public static enum API_CODE_NAME{
		BASE,
		BASE_1_1,
		CUPCAKE,
		DONUT,
		ECLAIR,
		ECLAIR_0_1,
		ECLAIR_MR1,
		FROYO,
		GINGERBREAD,
		GINGERBREAD_MR1,
		HONEYCOMB,
		HONEYCOMB_MR1,
		HONEYCOMB_MR2,
		ICE_CREAM_SANDWICH,
		ICE_CREAM_SANDWICH_MR1,
		JELLY_BEAN,
		JELLY_BEAN_MR1,
		JELLY_BEAN_MR2,
		KITKAT,
		CUR_DEVELOPMENT,
	};
	
	public static String[] API_NAME = {
		"No Code Name",
		"Petit Four",
		"Cupcake",
		"Donut",
		"Eclair",
		"Eclair",
		"Eclair Minor Revision 1",
		"Froyo",
		"Gingerbread",
		"Gingerbread Minor Revision 1",
		"Honeycomb",
		"Honeycomb Minor Revision 1",
		"Honeycomb Minor Revision 2",
		"Ice Cream Sandwich",
		"Ice Cream Sandwich Minor Revision 1",
		"Jellybean",
		"Jellybean Minor Revision 1",
		"Jellybean Minor Revision 2",
		"KitKat",
		"Current Development Build",
	};
	
	public static String[] getCurrentAndroidVersionInfo(){
		
		return (new String[]{
				("Detected Full Android Version Information: Android " + android.os.Build.VERSION.RELEASE + " (" +
						API_CODE_NAME.values()[android.os.Build.VERSION.SDK_INT - 1].toString() + ")"),
				("Android Build Release: Android " + android.os.Build.VERSION.RELEASE),
				("API level " + android.os.Build.VERSION.SDK_INT + " " +API_NAME[android.os.Build.VERSION.SDK_INT - 1])
				
		});
		
	}
	
	
	public static String[] getMountedVolumes(){
		final String state = Environment.getExternalStorageState();
		if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {  // we can read the External Storage...           
		    //Retrieve the primary External Storage:
		    final File primaryExternalStorage = Environment.getExternalStorageDirectory();

		    //Retrieve the External Storages root directory:
		    String externalStorageRootDir = null;
		    if ( (externalStorageRootDir = primaryExternalStorage.getParent()) == null ) {  // no parent...
		    	return (
		    			new String[] {
			    			"ONLY A SINGLE VOLUME HAS BEEN DETECTED!",
			    			(Environment.isExternalStorageRemovable() ? "(REMOVABLE SD-CARD)" : "(INTERNAL STORAGE)")
			    			+ " PRIMARY STORAGE: " + primaryExternalStorage
		    	});
		    }
		    else {
		        final File externalStorageRoot = new File( externalStorageRootDir );
		        final File[] files = externalStorageRoot.listFiles(new FilenameFilter(){

					@Override
					public boolean accept(File dir, String filename) {
						// TODO Auto-generated method stub
						
						File file = new File(dir, filename);
						if(file.isDirectory() && file.canRead() && file.canWrite() 
		            		&& !file.isHidden()){
							return true;
						}
						return false;
					}
		        	
		        }); //.listFiles();
		        List<String> data = new ArrayList<String>();
		        if(files.length > 1){
		        	data.add("MULTIPLE VOLUMES HAS BEEN DETECTED!");
			        data.add("Enumerating detected volumes . . .");
			    }else{
		        	data.add("ONLY A SINGLE VOLUME HAS BEEN DETECTED!");
			    }
		        
		        for ( final File file : files ) {
		            if ( file.isDirectory() && file.canRead() && file.canWrite() 
		            		&& !file.isHidden() && (files.length > 0) ) {  // it is a real directory (not a USB drive)...
		            	if(file.toString().equals(primaryExternalStorage.toString()))
		            		data.add((Environment.isExternalStorageRemovable() ? "(REMOVABLE SD-CARD)" : "(INTERNAL Memory)")
					    			+ " PRIMARY STORAGE: " + file.getAbsolutePath());
		            	else{
		            		data.add( ((file.toString().contains("usb") || 
		            				file.toString().contains("USB")) ? "MOUNTED USB" : "MOUNTED") + " STORAGE: " + file.getAbsolutePath());
		            	}
		            }
		        }
		        return data.toArray(new String[data.size()]);
		    }
		}else{
			// we cannot read the External Storage..., return null
			return null;
		}
		
	}
	
	
	public static File[] getMountedVolumesAsFile(){
		final String state = Environment.getExternalStorageState();
		if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {  // we can read the External Storage...           
		    //Retrieve the primary External Storage:
		    final File primaryExternalStorage = Environment.getExternalStorageDirectory();

		    //Retrieve the External Storages root directory:
		    String externalStorageRootDir = null;
		    if ( (externalStorageRootDir = primaryExternalStorage.getParent()) == null ) {  // no parent...
		    	return (new File[] {primaryExternalStorage});
		    }else{
		        final File externalStorageRoot = new File( externalStorageRootDir );
		        final File[] files = externalStorageRoot.listFiles(new FilenameFilter(){

					@Override
					public boolean accept(File dir, String filename) {
						// TODO Auto-generated method stub
						
						File file = new File(dir, filename);
						if(file.isDirectory() && file.canRead() && file.canWrite() 
		            		&& !file.isHidden()){
							return true;
						}
						return false;
					}
		        	
		        }); //.listFiles();
		        List<File> data = new ArrayList<File>();
		        
		        
		        for ( final File file : files ) {
		            if ( file.isDirectory() && file.canRead() && file.canWrite() 
		            		&& !file.isHidden() && (files.length > 0) ) {  // it is a real directory (not a USB drive)...
		            	if(file.toString().equals(primaryExternalStorage.toString()))
		            		data.add(file);
		            	else{
		            		if(!file.toString().contains("usb") || !file.toString().contains("USB")){
		            			data.add(file);
		            		}
		            	}
		            }
		        }
		        return data.toArray(new File[data.size()]);
		    }
		}else{
			// we cannot read the External Storage..., return null
			return null;
		}
	}
		
	 public static void openFile(Context context, File url) throws IOException {
	        // Create URI
	        File file=url;
	        Uri uri = Uri.fromFile(file);
	        
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        // Check what kind of file you are trying to open, by comparing the url with extensions.
	        // When the if condition is matched, plugin sets the correct intent (mime) type, 
	        // so Android knew what application to use to open the file
	        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
	            // Word document
	            intent.setDataAndType(uri, "application/msword");
	        } else if(url.toString().contains(".pdf")) {
	            // PDF file
	            intent.setDataAndType(uri, "application/pdf");
	        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
	            // Powerpoint file
	            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
	        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
	            // Excel file
	            intent.setDataAndType(uri, "application/vnd.ms-excel");
	        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
	            // WAV audio file
	            intent.setDataAndType(uri, "application/zip");
	        } else if(url.toString().contains(".rtf")) {
	            // RTF file
	            intent.setDataAndType(uri, "application/rtf");
	        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
	            // WAV audio file
	            intent.setDataAndType(uri, "audio/x-wav");
	        } else if(url.toString().contains(".gif")) {
	            // GIF file
	            intent.setDataAndType(uri, "image/gif");
	        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
	            // JPG file
	            intent.setDataAndType(uri, "image/jpeg");
	        } else if(url.toString().contains(".txt")) {
	            // Text file
	            intent.setDataAndType(uri, "text/plain");
	        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
	            // Video files
	            intent.setDataAndType(uri, "video/*");
	        } else {
	            //if you want you can also define the intent type for any other file
	            
	            //additionally use else clause below, to manage other unknown extensions
	            //in this case, Android will show all applications installed on the device
	            //so you can choose which application to use
	            intent.setDataAndType(uri, "*/*");
	        }
	        
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	        context.startActivity(intent);
	    }
	
	public static void tryCatchLogVoid(){
		
		
		
	}
	
	public static boolean tryCatchLogBoolean(){
		
		return true;
	}
	
	
	public static Class<?> tryCatchLogReturn(){
		
		return null;
	}
	
	public static String convertMillis(long millis){
		return new SimpleDateFormat("\n[MM/dd/yyyy hh:mm:ss aaa] ", Locale.getDefault()).format(millis);
		
	}
	
	public static void zipLogs(Context c){
		String dir = c.getExternalFilesDir(null).getParent() + "/logs";
		String logsDir = c.getExternalFilesDir(null).getParent();
		
		if(!new File(dir).exists()){
			new File(dir).mkdir();
		}
		
		 File lastScanLogs = new File(logsDir + "/quick_logs.log");
         File reportLogs = new File(logsDir + "/full_logs.log");
         File errorLogs = new File(logsDir + "/error_logs.log");
         List<String> filesToZip = new ArrayList<String>();
            
         if(lastScanLogs.exists()){
        	 filesToZip.add(new File(logsDir + "/quick_logs.log").getAbsolutePath());
         }
         
         if(reportLogs.exists()){
        	 filesToZip.add(new File(logsDir + "/full_logs.log").getAbsolutePath());
         }
    	
         if(errorLogs.exists()){
        	 filesToZip.add(new File(logsDir + "/error_logs.log").getAbsolutePath());
    	 }
    		
         Zip zip = new Zip(filesToZip.toArray(new String[filesToZip.size()]), 
          		new File(dir + "/" + FileProperties.formatLongToDate(System.currentTimeMillis()) + ".zip").getAbsolutePath());
         zip.zip();
		
	}
	
	public static void emailLogs(final Context c, final String email) throws Exception{
		
		new  GlobalAsyncTask<Void, Void, Void>(){

			@Override
			protected Void doTask(Void... params) throws Exception {
				// TODO Auto-generated method stub
				

				MailSender sender = new MailSender("developers.sharpfixandroid@gmail.com", 
						new String( new ServerCommunication().getEmail()));
				
					
			        
				for(File zip :  new File(c.getExternalFilesDir(null).getParent() + "/logs").listFiles()){
					try{
						Log.e("AndroidUtils","Adding " + zip.getAbsolutePath() + " to Attachment");
						sender.addAttachment(zip.getAbsolutePath(),
					        		zip.getName());
					        //Thread.sleep(1000);
					} catch (Exception e) {   
						Log.e("AndroidUtils", "AN ERROR HAS BEEN ENCOUNTERED WHILE SENDING MAIL");
					 	Log.e("AndroidUtils", e.getMessage(), e);   
					} 
				}
				sender.sendMail("SharpFix Android Automatic Logs Report",   
			            "",   
			            "developers.sharpfixandroid@gmail.com",   
			            email);   
				Log.e("AndroidUtils", "SUCCESSFULLY SENT MAIL");
				Thread.sleep(1000);
				File dir = new File(c.getExternalFilesDir(null).getParent() + "/logs"); 
				if (dir.isDirectory()) {
				        String[] children = dir.list();
				        for (int i = 0; i < children.length; i++) {
				            new File(dir, children[i]).delete();
				        }
				    }
				return null;
				
			}

			@Override
			protected void onException(Exception e) {
				// TODO Auto-generated method stub
				Log.e("AndroidUtils", "AN ERROR HAS BEEN ENCOUNTERED WHILE SENDING MAIL");
				 e.getStackTrace();
                Log.e("AndroidUtils", e.getMessage(), e); 
               
			}
			
		}.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
    	
	}
	
	public static void logScanReport(Context c, String[] logs){
		String dir = c.getExternalFilesDir(null).getParent();
		try{
			File file = new File(dir + "/quick_logs.log");
			boolean append = false;
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}else{
				
				/*
				if(file.length() >= 2097152){
					// set log file max size to 2mb
					append = false;
				}
				*/
			}
			FileOutputStream fos = new FileOutputStream(file, append);
			
			for(String log : logs){
				fos.write((log+"\n\n").getBytes());
			}
			fos.close();
		}catch(IOException ioe){
			Log.e("AndroidUtils", "An exception has occured in logScanReport!");
			ioe.printStackTrace();
		}
	}
	
	public static void logGlobalException(Context c, String[] logs){
		String dir = c.getExternalFilesDir(null).getParent();
		try{
			File file = new File(dir + "/error_logs.log");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file, true);
			
			for(String log : logs){
				fos.write((log+"\n\n").getBytes());
			}
			fos.close();
		}catch(IOException ioe){
			Log.e("AndroidUtils", "An exception has occured in logGlobalException!");
			ioe.printStackTrace();
		}
	}
	
	
	public static void logProgressReport(Context c, String[] logs){
		String dir = c.getExternalFilesDir(null).getParent();
		try{
			File file = new File(dir + "/full_logs.log");
			boolean append = false;
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}else{
				/*
				if(file.length() >= 2097152){
					// set log file max size to 2mb
					append = false;
				}
				*/
				
			}
			FileOutputStream fos = new FileOutputStream(file, append);
			
			for(String log : logs){
				fos.write((log+"\n\n").getBytes());
			}
			fos.close();
		}catch(IOException ioe){
			Log.e("AndroidUtils", "An exception has occured in logProgressReport!");
			ioe.printStackTrace();
		}
	}
	
	public static boolean cutPasteFile(File inputFile, File outputFile, int currentCount){
		
		// ######################## ALPHA RELEASE 1.1.2 ########################
		
					/* ALPHA RELEASE 1.1.2 BUG FIX
					 * 
					 * BUG DESCRIPTION:
					 * 
					 * This patch fixes the bug  that is encountered when a file has been 
					 * detected to be designated and its file name already exists in the 
					 * designation folder, the File Designation Scan overwrites the file 
					 * inside the designation folder, creating a bug which deletes the file,
					 * losing the said file permanently.
					 * 
					 * GITHUB Issue Link:  https://github.com/idclxvii/SharpFixAndroid/issues/1
					 * 
					 * Solution:
					 * 
					 * 		Renames the file name and checks if the renamed file already exists.
					 * 		If it does, call this method recursively until there's no occurrences 
					 * 		of the current file name chosen. If the file doesn't exist yet, then 
					 * 		proceed the cut and paste procedures
					 * 
					 * */
		
		if(outputFile.exists()){
			File renamedFile = new File("");
			if(currentCount > 0){
				// this is the 2nd time occurrence, in short Test (x).jpg, where x is any integer greater than 1
				
				currentCount++;
				renamedFile = new File(outputFile.getParent(),  
						outputFile.getName().substring(0,outputFile.getName().lastIndexOf("(")) // "test (0).jpg" returns "test ("
						+ " ("+currentCount + ")" 
								+ outputFile.getName().substring(outputFile.getName().lastIndexOf(".")) // test.xml returns .xml
				);
				
			}else{
				// Test case: File.txt -> File (1).txt
				currentCount++;
				renamedFile = new File(outputFile.getParent(),   
						outputFile.getName().substring(0,outputFile.getName().lastIndexOf(".")) // test.xml returns test
						+ " (" +currentCount + ")" 
								+ outputFile.getName().substring(outputFile.getName().lastIndexOf(".")) // test.xml returns .xml
				);
			}
			// rename output file
			
			return cutPasteFile(inputFile,renamedFile, currentCount);
			// recursively call 
		}else{
			try{
				InputStream is = new FileInputStream(inputFile);
	    	    OutputStream os = new FileOutputStream(outputFile);
	    	    byte[] buffer = new byte[1024];
	    	    int length;
	    	    //copy the file content in bytes 
	    	    while ((length = is.read(buffer)) > 0){
	    	    	os.write(buffer, 0, length);
	    	    }
	    	    is.close();
	    	    os.close();
	    	    //delete the original file
	    	    inputFile.delete();
	    	    return true;
	    	}catch(IOException e){
	    	    e.printStackTrace();
	    	    return false;
	    	}
			
			
		}

	}
		
		
		// ######################## ALPHA RELEASE 1.1.2 ########################
		
		
	
	
}
