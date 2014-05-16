package com.freetzi.idclxvii.sharpfixandroid;
import android.app.Service;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.util.Log;
import android.widget.*;
import java.io.*;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.*;

public class MyService extends Service{ 
	
	private static final String TAG = "SharpFix Android";
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	private int mInterval = 5000; // 10 minutes full scanning interval |5 seconds by default, can be changed later
	
	
	private Handler mHandler;
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "Codesix has started.", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
	    mHandler = new Handler();
	 
	    
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Codesix has stopped", Toast.LENGTH_LONG).show();
		stopRepeatingTask();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "onStart");
		startRepeatingTask();
	}
	
	/*
	public static void requestNewThread(DirectoryScannerThread dst, File f, String dir){
		
		new Handler().postDelayed(new DirectoryScannerThread(f,dir),1000);
	}
	
	public static void requestNewThread(DuplicateFilesScannerThread dfst, File f, String dir){
		Looper.getMainLooper();
		Looper.prepareMainLooper();
		new Handler().postDelayed(new DuplicateFilesScannerThread(f,dir),1000);
		
	}
	*/
	private DirectoryScannerThread dst;
	private DuplicateFilesScannerThread dfst;
	private int duplicateScan = 0;
	private int fileTypeScan = 0;
	
	// timers
	private long startTimeDST = 0; //System.currentTimeMillis();
	private long startTimeDFST = 0; //System.currentTimeMillis();
	
	private long endTimeDST   = 0; // System.currentTimeMillis();
	private long endTimeDFST   = 0; // System.currentTimeMillis();
	
	private long totalTimeDST = 0 ; //endTime - startTime;
	private long totalTimeDFST = 0; 
	
	
	public  void runFileDesignation(){
		
		//DirectoryScannerThread dst = new DirectoryScannerThread();
		//mChecker.postDelayed(dst,sInterval);
		
		// Toast.makeText(this, "Full SD Card Scan finished!", Toast.LENGTH_LONG).show();
		try{
			if(!this.dst.isRunning()){
				this.endTimeDST = System.currentTimeMillis();
				
				long hours = ((( (endTimeDST - startTimeDST) / 1000) / 60) / 60);
				long minutes = (( (endTimeDST - startTimeDST) / 1000) - (hours * 60 * 60)) / 60;
				long seconds = ( ( (endTimeDST - startTimeDST) / 1000) %60)%60;
				
				this.logger("\nSHARPFIX FILE DESIGNATION SCANNER FINISHED!\nFiles scanned:  " 
						+ this.dst.getFileCount() +"\nFolders scanned: " +
						this.dst.getDirCount() +"\nScan Time Elapsed: " + 
						hours +" hours " + minutes +" min(s) " + seconds +" sec(s)\n"  );
				
				fileTypeScan++;
				this.startTimeDST = System.currentTimeMillis();
				this.dst = new DirectoryScannerThread();
				this.logger("\nSHARPFIX FILE DESIGNATION SCANNER INITIATED! Scan count " + +this.fileTypeScan +"\n");
			}
		//	Thread.sleep(1000);
			
		}catch(Exception e){
			fileTypeScan++;
			this.startTimeDST = System.currentTimeMillis();
			this.dst = new DirectoryScannerThread();
			this.logger("\nSHARPFIX FILE DESIGNATION SCANNER INITIATED!\nScan count: " + +this.fileTypeScan +"\n");
			
		}
	}
	
	private void runDuplicateFileDetection(){
		
		//DuplicateFilesScannerThread dfst = new DuplicateFilesScannerThread();
		//mChecker.postDelayed(dfst,sInterval);
		
		try{
			if(!this.dfst.isRunning()){

				this.endTimeDFST = System.currentTimeMillis();
				
				long hours = ((( (endTimeDFST - startTimeDFST) / 1000) / 60) / 60);
				long minutes = (( (endTimeDFST - startTimeDFST) / 1000) - (hours * 60 * 60)) / 60;
				long seconds = ( ( (endTimeDFST - startTimeDFST) / 1000) %60)%60;
				
				this.logger("\nSHARPFIX FILE DUPLICATION SCANNER FINISHED!\nDuplicate files detected:  " 
			+ this.dfst.getDuplicateCount() +"\nScan Time Elapsed: " + 
			hours +" hours " + minutes +" min(s) " + seconds +" sec(s)\n");
				
				duplicateScan++;
				this.startTimeDFST = System.currentTimeMillis();
				this.dfst = new DuplicateFilesScannerThread();
				this.logger("\nSHARPFIX FILE DUPLICATION SCANNER INITIATED! Scan count " + +this.duplicateScan +"\n");
			}
		//	Thread.sleep(1000);
			
		}catch(Exception e){
			duplicateScan++;
			this.startTimeDFST = System.currentTimeMillis();
			this.dfst = new DuplicateFilesScannerThread();
			this.logger("\nSHARPFIX FILE DUPLICATION SCANNER INITIATED! Scan count " + +this.duplicateScan +"\n");
		}
	}
	
	
	
	private Runnable mStatusChecker = new Runnable() {
		@Override 
	    public void run() {
	//      updateStatus(); //this function can change value of mInterval.
	    		// logger("\n\n===Checking Thread Status===\n\n");
	    		
		    	runFileDesignation();
				runDuplicateFileDetection();
				mHandler.postDelayed(mStatusChecker,mInterval);
			    //mHandler.postAtFrontOfQueue(mStatusChecker);
	    }
	    
	};

	

	
	  
	public void startRepeatingTask() {
	    mStatusChecker.run();
	    
	    //runTask();
	}

	public void stopRepeatingTask() {
	    mHandler.removeCallbacks(mStatusChecker);
	   
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
			String FILENAME = "SharpFix.log";
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
	
	  
	  
}