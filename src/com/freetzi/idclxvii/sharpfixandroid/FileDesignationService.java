package com.freetzi.idclxvii.sharpfixandroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.widget.*;
import java.io.*;
import android.app.*;

public class FileDesignationService extends Service{ 
	
	


	private static final String TAG = "SharpFix Android";
	private String currentDir = "";
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	 
	private int mInterval = 5000; // 10 minutes full scanning interval |5 seconds by default, can be changed later
	
	
	private Handler mHandler;
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "SHARPFIX File Designation Service has started.", Toast.LENGTH_LONG).show();
	    mHandler = new Handler();
	    ServiceManager.startFileDesignationService();
	    
	}
	
	

	@Override
	public void onDestroy() {
		Toast.makeText(this, "SHARPFIX File Designation Service has stopped.", Toast.LENGTH_LONG).show();
		stopRepeatingTask();
		
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "onStart");
		startRepeatingTask();
	}
	
	private FileDesignationThread fdt;
	private int fileTypeScan = 0;
	
	// timers
	private long startTime = 0; //System.currentTimeMillis();
	
	private long endTime   = 0; // System.currentTimeMillis();
	
	private long totalTime = 0 ; //endTime - startTime;
	
	
	public  void runFileDesignation(){
		
		//DirectoryScannerThread dst = new DirectoryScannerThread();
		//mChecker.postDelayed(dst,sInterval);
		
		// Toast.makeText(this, "Full SD Card Scan finished!", Toast.LENGTH_LONG).show();
		try{
			if(!this.fdt.isRunning()){
				this.endTime = System.currentTimeMillis();
				
				long hours = ((( (endTime - startTime) / 1000) / 60) / 60);
				long minutes = (( (endTime - startTime) / 1000) - (hours * 60 * 60)) / 60;
				long seconds = ( ( (endTime - startTime) / 1000) %60)%60;
				
				this.logger("\nSHARPFIX FILE DESIGNATION SCANNER FINISHED!\nFiles scanned:  " 
						+ this.fdt.getFileCount() +"\nFolders scanned: " +
						this.fdt.getDirCount() +"\nScan Time Elapsed: " + 
						hours +" hours " + minutes +" min(s) " + seconds +" sec(s)\n"  );
				
				fileTypeScan++;
				this.startTime = System.currentTimeMillis();
				this.fdt = new FileDesignationThread();
				//this.fdt = (FileDesignationThread) this.i.getSerializableExtra("FileDesignationThread");
				 // this.fdt.run();
				this.logger("\nSHARPFIX FILE DESIGNATION SCANNER INITIATED! Scan count " + +this.fileTypeScan +"\n");
			}/*else{
				this.i = new Intent(this, FileDesignationThread.class);
				this.i.putExtra("FileDesignationThread",this.fdt);
			}*/
		//	Thread.sleep(1000);
			
			
			
		}catch(Exception e){
			fileTypeScan++;
			this.startTime = System.currentTimeMillis();
			this.fdt = new FileDesignationThread();
			 // this.fdt.run();
			this.logger("\nSHARPFIX FILE DESIGNATION SCANNER INITIATED!\nScan count: " + +this.fileTypeScan +"\n");
			
		}
	}
	
	
	
	
	
	private Runnable mStatusChecker = new Runnable() {
		@Override 
	    public void run() {
	//      updateStatus(); //this function can change value of mInterval.
	    		// logger("\n\n===Checking Thread Status===\n\n");
	    		
		    	runFileDesignation();
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
	
	  
	  
}