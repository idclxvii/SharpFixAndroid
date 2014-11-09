package tk.idclxvii.sharpfixandroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import tk.idclxvii.sharpfixandroid.databasemodel.*;
import tk.idclxvii.sharpfixandroid.utils.FileProperties;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class FileDesignationScanner extends Service{

	public FileDesignationScanner(){
		
	}
	
	private SQLiteHelper db;
	private Context c;
	
	private PowerManager powerManager;
	private WakeLock wakeLock;
	private Task TASKHANDLER = new Task();
	private NotificationManager mNotifyManager;
	private NotificationCompat.Builder mBuilder;
	
	private String TAG = "FileDesignationScanner";
	
	private Object[] tempFilesQueue;
	//private Object[] tempDirsQueue;
	private List<Object> filesQueue;
	//private List<Object> dirsQueue;
	private ModelPreferences prefs; 
	
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	
	private class Task extends GlobalAsyncTask<File, String, Void>{

		@Override
		protected Void doTask(File... params) throws Exception {
			// TODO Auto-generated method stub
			long start = System.currentTimeMillis();
			publishProgress(new String[] {"File Designation Scan", "Initializing Scan: " + (FileProperties.formatFileLastMod(start))});
			
			prefs = (ModelPreferences)db.selectAll(Tables.preferences, ModelPreferences.class, null)[0];
			
			for(Object o : filesQueue){
				checkPreferences(new File(((ModelFilesInfo)o).getPath()));
			}
			
			long end = System.currentTimeMillis();
			long runTime = end - start;
			
			publishProgress(new String[] {"File Designation Scan Finished", "Time elapsed:" + 
					String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
							TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
							TimeUnit.MILLISECONDS.toSeconds(runTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))),
							"" });
			//checkPreferences(params[0]);
			
			
			
			
			
			return null;
		}

		@Override
		protected void onException(Exception e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void onProgressUpdate(String... params){
			if(params.length > 2){
				mBuilder.setProgress(0, 0, true);
				mBuilder.setContentTitle(params[0]);
				mBuilder.setContentText(params[1]);
				mNotifyManager.notify(2, mBuilder.build());
				//mNotifyManager.cancel(2);
				//stopForeground(true);
				stopSelf();
			}else{
				
				if(params[0].length() > 0){
					// there are changes in notification title
					mBuilder.setContentTitle(params[0]);
					mBuilder.setProgress(0, 0, true);
					mBuilder.setContentText(params[1]);
					mNotifyManager.notify(2, mBuilder.build());
				}else{
					// there are no changes in notification title
					mBuilder.setProgress(0, 0, true);
					mBuilder.setContentText(params[1]);
					mNotifyManager.notify(21, mBuilder.build());
				}
			}
			
		}
		
		private void checkPreferences(File f) throws IllegalArgumentException, InstantiationException, IllegalAccessException, 
		NoSuchMethodException, InvocationTargetException, FileNotFoundException, IOException{
			publishProgress(new String[] {"", f.getAbsolutePath()});
			if(prefs.getFdd_switch() == 1){
				Log.i(TAG, "########################################");
				Log.i(TAG, "File Designation is turned on!");
				// File Designation switch is turned on
				if( f.isDirectory() && f.canRead() && f.canWrite() && !f.isHidden() && f.exists() ){
					// legal directory
					if(prefs.getFdd_Filter_switch() == 1){
						// fdd filter switch is turned on
						Log.i(TAG, "########################################");
						Log.i(TAG, "FDD Filtering is turned on!");
						ModelDirFilter mdf = (ModelDirFilter) db.select(Tables.dir_filter, ModelDirFilter.class,
								new Object[][] {{"dir", f.getAbsolutePath()}},null);
						if( mdf.getDir() != null && mdf.getDir().equals(f.getAbsolutePath())){
							// this dir is being filtered!
							Log.i(TAG, "########################################");
							Log.i(TAG, "Directory " + f.getAbsolutePath() + " is being filtered!");
							
						}else{
							// this dir is not being filtered!
							// scan this directory
							Log.i(TAG, "########################################");
							Log.i(TAG, "Directory " + f.getAbsolutePath() + " is NOT being filtered!");
							Log.i(TAG, "Initializing File Duplication Scan");
							scan(f);
						}
					}else{
						// fdd filter switch is turned off
						// scan this directory
						Log.i(TAG, "########################################");
						Log.i(TAG, "FDD Filtering is turned off!");
						Log.i(TAG, "Scanning directory: " + f.getAbsolutePath());
						scan(f);
						
						
					}
					
					
				}else{
					if(!f.isDirectory() && f.canRead() && f.canWrite() && !f.isHidden() && f.exists()){
						// legal file
						if(prefs.getFdd_Filter_switch() == 1){
							// fdd filter switch is turned on
							Log.i(TAG, "########################################");
							Log.i(TAG, "FDD Filtering is turned on!");
							ModelFileFilter mff = (ModelFileFilter) db.select(Tables.file_filter, ModelFileFilter.class,
									new Object[][] {{"file", f.getAbsolutePath()}},null);
							if( mff.getFile() != null && mff.getFile().equals(f.getAbsolutePath())){
								// this file is being filtered!
								Log.i(TAG, "########################################");
								Log.i(TAG, "File " + f.getAbsolutePath() + " is being filtered!");
							}else{
								// this dir is not being filtered!
								// scan this directory
								Log.i(TAG, "########################################");
								Log.i(TAG, "File " + f.getAbsolutePath() + " is NOT being filtered!");
								Log.i(TAG, "Initializing File Duplication Scan");
								scan(f);
							}
						}else{
							// fdd filter switch is turned off
							Log.i(TAG, "########################################");
							Log.i(TAG, "FDD Filtering is turned off!");
							Log.i(TAG, "Scanning file: " + f.getAbsolutePath());
							scan(f);
						}
					}
				}
				
				
			}else{
				Log.i(TAG, "########################################");
				Log.i(TAG, "File Designation is turned off!");
			}
		}
		
	
	
		private void scan(File f) throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException, FileNotFoundException, IOException{
			
			// default implementation is crc32
			
			Object[] result = 
					db.selectMulti(Tables.files_info, ModelFilesInfo.class,
							new Object[][] { {"crc32", Security.getCRC32Checksum(f.getAbsolutePath())} }
			, null);
			
			if(result.length > 0){
			
				if(result.length > 1){
					// there are 2 or more occurrence duplicate files (3 or more duplicate files)
					Log.i(TAG, "########################################");
					Log.i(TAG, "2 or more files detected as duplicates of " + f.getAbsolutePath());
					Log.i(TAG, "########################################");
					
					for(Object o : result){
						Log.i(TAG, ((ModelFilesInfo)o).getPath() + ((ModelFilesInfo)o).getCrc32()  );
					}
				}else{
					// there's only 1 occurrence of duplicate file (2 duplicate files)
					Log.i(TAG, "########################################");
					Log.i(TAG, "1 file is detected as duplicate of " + f.getAbsolutePath());
					Log.i(TAG, "########################################");
					for(Object o : result){
						Log.i(TAG, ((ModelFilesInfo)o).getPath() + ((ModelFilesInfo)o).getCrc32()  );
					}
				}
			}
		}
	
	}

	
	
	@Override
	public void onCreate() {
		super.onCreate();
		db = getDb(this);
		powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		        "MyWakelockTag");
		wakeLock.acquire();
		
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "########################################");
		Log.i(TAG, "Destroying File Designation Scanner");
		wakeLock.release();
		TASKHANDLER.cancel(true);
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "########################################");
		Log.i(TAG, "Creating File Designation Scanner");
		
		Intent notificationIntent = new Intent(this, SubMenuDirectScanControls.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent dspendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("SharpFix File Designation Scanner");
		mBuilder.setContentIntent(dspendingIntent);
		mBuilder.setContentText("Scanning For Duplicate Files");
	    mBuilder.setSmallIcon(R.drawable.ic_launcher);
		
	   /*
	    tempFilesQueue = (Object[]) intent.getExtras().getSerializable("files");
	    //tempDirsQueue = (Object[]) intent.getExtras().getSerializable("dirs");
	   
	    filesQueue = new ArrayList<Object>(Arrays.asList(tempFilesQueue));
	    //dirsQueue = new ArrayList<Object>(Arrays.asList(tempDirsQueue));
	    
	    */
		TASKHANDLER.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
		
		
		// Start a lengthy operation in a background thread
		
		/*
		Notification noti = new Notification(R.drawable.ic_launcher, "SharpFix Directory Scanner",System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, SubMenuServicesActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent dspendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		noti.setLatestEventInfo(this, "SharpFix Directory Scanner","Scanning SD-Card changes", dspendingIntent);
		startForeground(1, noti);
		
		TASKHANDLER.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
		*/
		
		return START_STICKY;
	}
	
}
