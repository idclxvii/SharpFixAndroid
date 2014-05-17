package tk.idclxvii.sharpfixandroid;
import android.app.Notification;
import android.app.PendingIntent;
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

public class FileDuplicationDetectionService extends Service{ 
	
	private static final String TAG = "SharpFix Android";
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	/*
	@Override
	public int onStartCommand(Intent intent, int flags, int stardId){
		
		/*
		final  int myID = 1234;
	      intent = new Intent(this, FileDesignationThread.class);
	      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	      PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);

	      //This constructor is deprecated. Use Notification.Builder instead
	      Notification notice = new Notification(R.drawable.ic_launcher, "FDD ~", System.currentTimeMillis());

	      //This method is deprecated. Use Notification.Builder instead.
	      notice.setLatestEventInfo(this, "SharpFix", "FDD Service Running", pendIntent);

	      notice.flags |= Notification.FLAG_NO_CLEAR;
	      startForeground(myID, notice);
		*/
		
		
		/*
		Intent i= new Intent(this, MyService.class);
		// potentially add data to the intent
		i.putExtra("KEY1", "Value to be used by the service");
		this.startService(i); 
		
		return Service.START_REDELIVER_INTENT;
	}
	*/
	private int mInterval = 5000; // 10 minutes full scanning interval |5 seconds by default, can be changed later
	
	
	private Handler mHandler;
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "SHARPFIX File Duplication Detection Service has started.", Toast.LENGTH_LONG).show();Log.d(TAG, "onCreate");
	    mHandler = new Handler();
	 
	    
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "SHARPFIX File Duplication Detection Service has stopped.", Toast.LENGTH_LONG).show();Log.d(TAG, "onCreate");
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
	private FileDuplicationDetectionThread fdds;
	private int duplicateScan = 0;
	
	// timers
	private long startTime = 0; //System.currentTimeMillis();
	
	private long endTime   = 0; // System.currentTimeMillis();
	
	private long totalTime = 0; 
	
	
	
	
	private void runDuplicateFileDetection(){
		
		//DuplicateFilesScannerThread dfst = new DuplicateFilesScannerThread();
		//mChecker.postDelayed(dfst,sInterval);
		
		try{
			if(!this.fdds.isRunning()){

				this.endTime = System.currentTimeMillis();
				
				long hours = ((( (endTime - startTime) / 1000) / 60) / 60);
				long minutes = (( (endTime - startTime) / 1000) - (hours * 60 * 60)) / 60;
				long seconds = ( ( (endTime - startTime) / 1000) %60)%60;
				
				this.logger("\nSHARPFIX FILE DUPLICATION SCANNER FINISHED!\nDuplicate files detected:  " 
			+ this.fdds.getDuplicateCount() +"\nScan Time Elapsed: " + 
			hours +" hours " + minutes +" min(s) " + seconds +" sec(s)\n");
				
				duplicateScan++;
				this.startTime = System.currentTimeMillis();
				this.fdds = new FileDuplicationDetectionThread();
				this.logger("\nSHARPFIX FILE DUPLICATION SCANNER INITIATED! Scan count " + +this.duplicateScan +"\n");
			}
		//	Thread.sleep(1000);
			
		}catch(Exception e){
			duplicateScan++;
			this.startTime = System.currentTimeMillis();
			this.fdds = new FileDuplicationDetectionThread();
			this.logger("\nSHARPFIX FILE DUPLICATION SCANNER INITIATED! Scan count " + +this.duplicateScan +"\n");
		}
	}
	
	
	
	private Runnable mStatusChecker = new Runnable() {
		@Override 
	    public void run() {
	//      updateStatus(); //this function can change value of mInterval.
	    		// logger("\n\n===Checking Thread Status===\n\n");
	    		
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
	
	  
	  
}