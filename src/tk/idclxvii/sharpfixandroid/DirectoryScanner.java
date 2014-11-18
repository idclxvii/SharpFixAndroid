package tk.idclxvii.sharpfixandroid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import tk.idclxvii.sharpfixandroid.databasemodel.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import tk.idclxvii.sharpfixandroid.utils.*;

public class DirectoryScanner extends Service{

	private SQLiteHelper db;
	
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	
	private SharpFixApplicationClass SF;
	
	private class Task extends GlobalAsyncTask<File, String, Void>{

		private int dirCount  =0 , fileCount = 0;
		Object [] sd;
		Object[] dirs;
		Object[] files;
		ModelPreferences prefs;
		
		//List<ModelDirsInfo> dirsQueue = new ArrayList<ModelDirsInfo>();
		//List<ModelFilesInfo> filesQueue = new ArrayList<ModelFilesInfo>();
		
		
		@Override
		protected Void doTask(File... params) throws Exception {
			// TODO Auto-generated method stub
			Log.i(TAG, "########################################");
			Log.i(TAG, "Starting Directory Scanner");
			long start = System.currentTimeMillis();
			publishProgress(new String[] {"Initializing Directory Scanner", "Time start:" + (FileProperties.formatFileLastMod(start)) });
			SF.initScanQueue();
			
			sd = db.selectAll(Tables.sd_info, ModelSD.class, null);
			dirs = db.selectAll(Tables.dirs_info, ModelDirsInfo.class, null);
			files = db.selectAll(Tables.files_info, ModelFilesInfo.class, null);
			try{
				prefs = (ModelPreferences)db.selectAll(Tables.preferences, ModelPreferences.class , null)[0];
				
			}catch(Exception e){
				prefs = new ModelPreferences(0, 1,0,1,0,0,0,0,0,0,0,0,7,1,1);
			}
			
			Log.i(TAG, "########################################");
			Log.i(TAG, "Starting Reverse Scan");
			publishProgress(new String[] {"SharpFix Reverse Directory Scan", "Starting Reverse Scan Algorithm to Folders"});
			/*
			 * reverse scan is the scan that looks up on the database and checks if the 
			 * database values actually exists, if not then it is deleted. If however the 
			 * said value was modified, the reverse scan updates the values in the database
			 */
			
			// directories reverse scan
			Log.i(TAG, "########################################");
			Log.i(TAG, "Starting Reverse Directory Scan");
			for(Object o : dirs){
				File f = new File(((ModelDirsInfo) o).getPath());
				publishProgress(new String[] {"", f.getAbsolutePath()});
				if( f.isDirectory() && f.canRead() && f.canWrite() && /*!f.isHidden() && */ f.exists()){
					// the recorded data is a dir, readable, writeable, not hidden and exists
					Log.i(TAG, "Folder " +f.getAbsolutePath() + " still exists in the file system!");
					
					// check if the current file instance has greater lastMod than the recorded data on the database (recency check)
					Log.i(TAG, "Checking if " +f.getAbsolutePath() + " has been modified since the last scan . . .");
					if(Long.valueOf(f.lastModified()).compareTo(((ModelDirsInfo) o).getLast_mod()) > 0){
						// file was modified
						Log.i(TAG, "Folder " +f.getAbsolutePath() + " was modified!");
							
						
						
						int sdCard = 0;
						boolean notFound = true;
						File parent = f.getParentFile();
						while(notFound){
							ModelSD sdTemp = (ModelSD) db.select(Tables.sd_info, ModelSD.class, new Object[][] { {"path", parent.getAbsolutePath()} }, null);
							if( sdTemp.getId() != null){
								sdCard = sdTemp.getId();
								notFound = false;
							}else{
								parent = parent.getParentFile();
							}
						}
						// add to directories to be scanned later by duplication detection and file designation
						Log.i("DirectoryScanner",  "Adding this directory to directory queue . . .");
						SF.dirsQueue.add(new ModelDirsInfo(sdCard, f.getAbsolutePath(), f.lastModified()));
						
						// rewrite lastmod, crc32, md5, sha1, pathname to database
						// db.update(Tables.dirs_info, ((ModelDirsInfo)o),  new ModelDirsInfo(sdCard, f.getAbsolutePath(), f.lastModified()), null);
					}else{
						// file was unmodified
						Log.i(TAG, "Folder " +f.getAbsolutePath() + " was NOT modified!");
					}
					
				}else{
					// invalid file or unreadable, delete this record on the databse
					
					Log.i(TAG, "Folder " +f.getAbsolutePath() + " is unreadable! Deleting from database records . . .");
					  db.delete(Tables.dirs_info, new ModelDirsInfo(((ModelDirsInfo) o).getSd_id(), ((ModelDirsInfo) o).getPath(),  
							((ModelDirsInfo) o).getLast_mod()), null);
					
				}
			}
			Log.i(TAG, "########################################");
			Log.i(TAG, "Starting Reverse File Scan");
			publishProgress(new String[] {"SharpFix Reverse File Scan", "Starting Reverse Scan Algorithm to Files"});
			// files reverse scan
			for(Object o : files){
				File f = new File(((ModelFilesInfo) o).getPath());
				publishProgress(new String[] {"", f.getAbsolutePath()});
				if( !f.isDirectory() && f.canRead() && f.canWrite() && !f.isHidden() && f.exists()){
					Log.i(TAG, "File " +f.getAbsolutePath() + " still exists in the file system!");
					
					// check if the current file instance has greater lastMod than the recorded data on the database (recency check)
					Log.i(TAG, "Checking if " +f.getAbsolutePath() + " has been modified since the last scan . . .");
					// the recorded data is a dir, readable, writeable, not hidden and exists
					if(Long.valueOf(f.lastModified()).compareTo(((ModelFilesInfo) o).getLast_mod()) > 0){
						// file was modified
						Log.i(TAG, "Folder " +f.getAbsolutePath() + " was modified!");
						
		// public ModelFilesInfo(String path, String dir, Long lastMod, String crc32, String md5, String sha1, String size){
						Log.i("DirectoryScanner",  "Adding this directory to file queue . . .");
						SF.filesQueue.add(new ModelFilesInfo(f.getAbsolutePath(),f.getParent()));
						
						/*
						db.update(Tables.files_info, ((ModelFilesInfo)o),
								new ModelFilesInfo(f.getAbsolutePath(), f.getParent(), f.lastModified(), 
										Security.getCRC32Checksum(f.getAbsolutePath()), Security.getMD5Checksum(f.getAbsolutePath()),
												Security.getSHA1Checksum(f.getAbsolutePath()),
														Long.toString(f.length())), null);
						*/
						
					}else{
						Log.i(TAG, "Folder " +f.getAbsolutePath() + " was NOT modified!");
						
					}
					
				}else{
					// invalid file or unreadable, delete this record on the databse
					Log.i(TAG, "Folder " +f.getAbsolutePath() + " is unreadable! Deleting from database records . . .");
					//public ModelFilesInfo(String path, String dir, Long lastMod, String crc32, String md5, String sha1, String size){
					db.delete(Tables.files_info, new ModelFilesInfo(((ModelFilesInfo) o).getPath(), ((ModelFilesInfo) o).getDir(),
							((ModelFilesInfo) o).getLast_mod(), ((ModelFilesInfo) o).getCrc32(), ((ModelFilesInfo) o).getMd5(),
							((ModelFilesInfo) o).getSha1(), ((ModelFilesInfo) o).getSize()), null);
				}
			}
			
			// disable recursive scan for a while
			
			
			Log.i(TAG, "########################################");
			Log.i(TAG, "Starting Recursive Scan");
			publishProgress(new String[] {"SharpFix Recursive Scan", "Starting Recursive Scan Algorithm to Folders and Files"});
			for(Object f : sd){
				publishProgress(new String[] {"",((ModelSD)f).getPath()});
				checkDir(new File( ((ModelSD)f).getPath() ), ((ModelSD)f).getId() );
			}
			
			
			long end = System.currentTimeMillis(); 
			long runTime = end - start;
			
			publishProgress(new String[] {"SD-Card Scan Finished", "Time elapsed:" + 
					String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
							TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
							TimeUnit.MILLISECONDS.toSeconds(runTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))),
							"" });
			
			Log.i(TAG, "########################################");
			Log.i(TAG, "Directory Scanner finished successfully!!!");
			Log.i(TAG, "########################################");
			Log.i(TAG, "Directory count: " +dirCount);
			Log.i(TAG, "File count: " +fileCount);
			Log.i(TAG, "Running Time:" + String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
					TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
					TimeUnit.MILLISECONDS.toSeconds(runTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))));
			
			
			
			
			
			return null;
		}

		@Override
		protected void onProgressUpdate(String... params){
			
			if(params.length > 2){
				if(SF.getServiceNoti() == 1){
					mBuilder.setContentTitle(params[0]);
					mBuilder.setProgress(0, 0, true);
					mBuilder.setContentText(params[1]);
				mNotifyManager.notify(1, mBuilder.build());
				}
				DirectoryScanner.this.wakeLock.release();
				//NotifyManager.cancel(1);
				//stopForeground(true);
				
				
				if((SF.filesQueue.size() > 0 || SF.dirsQueue.size() > 0) && prefs.getFdd_switch() == 1){
					// file duplication detection is turned on
					Intent i = new Intent(DirectoryScanner.this, FileDuplicationDetectionScanner.class);
					
					
					// !!! FAILED BINDER TRANSACTION !!!
					// the code below produces errors when Binder exceeds the limit 1MB of data 
					/*
					i.putExtra("files", filesQueue.toArray(new Object[filesQueue.size()]));
					i.putExtra("dirs", dirsQueue.toArray(new Object[dirsQueue.size()]));
					*/
					DirectoryScanner.this.startService(i);
					
					
					/*	
						Log.i(TAG, "########################################");
						Log.i(TAG, "Exception Caught: No Files to Queue!");
						Log.i(TAG, "########################################");
						Logcat.logCaughtException("DirectoryScanner", e.getStackTrace());
					*/
				}else{
					if( prefs.getFdd_switch() != 1){
						Log.w(TAG, "########################################");
						Log.w(TAG, "File Duplication Detection is turned off!");
						// check if fd switch is turned on
						// this will call fd scanner when fd is turned on even if fdd did not run 
						Log.i(TAG, "########################################");
						Log.i(TAG, "Checking for FileDesignation switch since FDD switch is turned off . . .");
						if(prefs.getFd_switch() == 1){
							// file designation is turned on
							Log.i(TAG, "########################################");
							Log.i(TAG, "File Designation is turned on!");
							Intent i = new Intent(DirectoryScanner.this, FileDesignationScanner.class);
							DirectoryScanner.this.startService(i);
							
						}else{
							Log.i(TAG, "########################################");
							Log.i(TAG, "File Designation is turned off");
						}
					}else{
						Log.w(TAG, "########################################");
						Log.w(TAG, "File Duplication Detection is turned on! Checking for file and directory modifications . . .");
						if(!(SF.dirsQueue.size() > 0)){
							Log.w(TAG, "########################################");
							Log.w(TAG, "It seems that no directory under Sharpfix's scope was modified since the last scan");
							Log.w(TAG, "Checking report consistency . . .");
							
							if(SF.filesQueue.size() > 0){
								Log.e(TAG, "########################################");
								Log.e(TAG, "WARNING! It looks like the scan report was inconsistent because the system detected "+
										SF.filesQueue.size() + (SF.filesQueue.size() > 1 ? " files" : " file") + " has " +
										"been modified since the last scan!");
							}else{
								Log.i(TAG, "########################################");
								Log.i(TAG, "There are no conflicts in both directory and file scan reports!");
								Log.i(TAG, "Suspending File Duplication Detection Scan since no file has been modified since the last scan!");
								
							}
						}
						
					}
					
					
				}
				
				
				/*
				if((SF.filesQueue.size() > 0 || SF.dirsQueue.size() > 0) && prefs.getFd_switch() == 1){
					// file designation is turned on
					
					Intent i = new Intent(DirectoryScanner.this, FileDesignationScanner.class);
					
					DirectoryScanner.this.startService(i);
						Log.i(TAG, "########################################");
						Log.i(TAG, "Exception Caught: No Files to Queue!");
						Log.i(TAG, "########################################");
						Logcat.logCaughtException("DirectoryScanner", e.getStackTrace());
					
				}else{
					if( prefs.getFd_switch() != 1){
						Log.w(TAG, "########################################");
						Log.w(TAG, "File Designation is turned off!");
					}else{
						Log.w(TAG, "########################################");
						Log.w(TAG, "File Designation is turned on! Checking for file and directory modifications . . .");
						if(!(SF.dirsQueue.size() > 0)){
							Log.w(TAG, "########################################");
							Log.w(TAG, "It seems that no directory under Sharpfix's scope was modified since the last scan");
							Log.w(TAG, "Checking report consistency . . .");
							
							if(SF.filesQueue.size() > 0){
								Log.e(TAG, "########################################");
								Log.e(TAG, "WARNING! It looks like the scan report was inconsistent because the system detected "+
										SF.filesQueue.size() + (SF.filesQueue.size() > 1 ? " files" : " file") + " has " +
										"been modified since the last scan!");
							}else{
								Log.i(TAG, "########################################");
								Log.i(TAG, "There are no conflicts in both directory and file scan reports!");
							}
						}
						
						
						if(!(SF.filesQueue.size() > 0)){
							Log.w(TAG, "########################################");
							Log.w(TAG, "It seems that no file under Sharpfix's scope was modified since the last scan");
							Log.w(TAG, "Checking report consistency . . .");
							
							if(SF.dirsQueue.size() > 0){
								Log.e(TAG, "########################################");
								Log.e(TAG, "WARNING! It looks like the scan report was inconsistent because the system detected "+
										SF.dirsQueue.size() + (SF.dirsQueue.size() > 1 ? " directories" : " directory") + " has " +
										"been modified since the last scan!");
							}else{
								Log.i(TAG, "########################################");
								Log.i(TAG, "There are no conflicts in both directory and file scan reports!");
							}
						}
					}
				}
				*/
				stopSelf();
				
				
				
				
				
			}else{
				
				if(params[0].length() > 0){
					// there are changes in notification title
					if(SF.getServiceNoti() == 1){
						mBuilder.setContentTitle(params[0]);
						mBuilder.setProgress(0, 0, true);
						mBuilder.setContentText(params[1]);
						mNotifyManager.notify(1, mBuilder.build());
					}
				}else{
					// there are no changes in notification title
					if(SF.getServiceNoti() == 1){
						mBuilder.setProgress(0, 0, true);
						mBuilder.setContentText(params[1]);
						mNotifyManager.notify(1, mBuilder.build());
					}
					
				}
			}
			
		}
		
		@Override
		protected void onPostExecute(Void returnedResult){
			
			
			
		}
		
		@Override
		protected void onException(Exception e) {
			// TODO Auto-generated method stub
			Log.i(TAG, "########################################");
			Log.i(TAG, "Exception on Directory Scanner!!!");
			Logcat.logCaughtException("DirectoryScanner", e.getStackTrace());
			
		}
		
		@Override
		protected void onCancelled(){
			Log.i(TAG, "########################################");
			Log.i(TAG, "Directory Scanner's execution has been cancelled!");
			mBuilder.setProgress(0, 0, true);
			mBuilder.setContentText("Directory Scanner has been cancelled.");
			mNotifyManager.notify(1, mBuilder.build());
			mNotifyManager.cancel(1);
			
		}
		
		
		
		private void checkDir(File f, int sdCard) throws InterruptedException, Exception{
			String parentDir = f.getAbsolutePath();
			if (f.isDirectory() && f.canRead() && f.canWrite() &&  !f.isHidden() &&  f.exists()) {
				String s[] = f.list();
				for (int i=0; i < s.length; i++) {
					File ff = new File(parentDir,s[i]);
					if (ff.isDirectory()) {
						publishProgress(new String[] {"", ff.getAbsolutePath()});
						dirCount++;
						ModelDirsInfo mdi = 
								(ModelDirsInfo) db.select(Tables.dirs_info, ModelDirsInfo.class, new Object[][] {{"sd_id", sdCard},
									{"path", ff.getAbsolutePath()}}, null);
						if(mdi.getPath() != null){
							Thread.sleep(100);
							Log.i("DirectoryScanner", mdi.getPath() + " already exists in the database!");
							Log.i("DirectoryScanner", "Checking if directory has been modified since the last scan . . .");
							
							// check if directory last mod was changed
							if(Long.valueOf(ff.lastModified()).compareTo(mdi.getLast_mod()) > 0){
								Log.i("DirectoryScanner",  mdi.getPath() + " was modified!");
								Log.i("DirectoryScanner",  "Traversing this directory and adding to directory queue . . .");
								
								// dir was modified
								
								SF.dirsQueue.add(mdi);
								// update dirs_info
								//db.update(Tables.dirs_info, mdi,  new ModelDirsInfo(sdCard, ff.getAbsolutePath(), ff.lastModified()), null);
							
								// traverse the current directory only when modified!
								checkDir(ff, mdi.getSd_id());
							}else{
								Log.i("DirectoryScanner",  mdi.getPath() + " was not modified!");
								Log.i("DirectoryScanner",  "Skipping . . .");
								
							}
							
							
							// if it does, add it to the dirQueue
							
							
							
						}else{
							Log.i("DirectoryScanner", "Directory " + ff.getPath() + " does not exist in the database!");
							Log.i("DirectoryScanner",  "Traversing this directory and adding to directory queue . . .");
							// add this dir to dirQueue
							SF.dirsQueue.add(new ModelDirsInfo(sdCard, ff.getAbsolutePath(), ff.lastModified()));
							// disable adding to database because scans can be cancelled
							// add it later to database when the current item was successfully scanned
							//db.insert(Tables.dirs_info, new ModelDirsInfo(sdCard, ff.getAbsolutePath(), ff.lastModified()), null);
							Thread.sleep(100);
							checkDir(ff, sdCard);
						}
						
					}else{
						publishProgress(new String[] {"", ff.getAbsolutePath()});
						ModelFilesInfo mfi = 
								(ModelFilesInfo) db.select(Tables.files_info, ModelFilesInfo.class, 
									new Object[][] {
										{"path", ff.getAbsolutePath()},
										{"dir", ff.getParent()}
										
										/*,
										{"md5", Security.getMD5Checksum(ff.getAbsolutePath())},
										{"sha1", Security.getSHA1Checksum(ff.getAbsolutePath())},
										{"size", Long.toString(ff.length())},
										{"last_mod", ff.lastModified()}
										*/
									}
								, null);
						
						Thread.sleep(100);
						if(mfi.getPath() != null){
							Log.i("DirectoryScanner",  "File " + mfi.getPath() + " already exists in the database!");
							Log.i("DirectoryScanner", "Checking if file has been modified since the last scan . . .");
							if(Long.valueOf(ff.lastModified()).compareTo(mfi.getLast_mod()) > 0){
								// file was modified
								Log.i("DirectoryScanner", "File " + mfi.getPath() + " was modified!");
								Log.i("DirectoryScanner",  "Adding this file to file queue . . .");
								
								SF.filesQueue.add(mfi);
								
								// update dirs_info
								//db.update(Tables.dirs_info, mdi,  new ModelDirsInfo(sdCard, ff.getAbsolutePath(), ff.lastModified()), null);
								
							}
							
							// check if file's last mod was changed
							// if it does, add it to the filesQueue
							
							// update files_info
							
							
						}else{
							Log.i("DirectoryScanner",  "File " + ff.getPath() + " does not exist in the database, adding to files queue . . .");
							
							// add this file to fileQueue
							SF.filesQueue.add(new ModelFilesInfo(ff.getAbsolutePath(),ff.getParent()));
							/*
							db.insert(Tables.files_info, new ModelFilesInfo(ff.getAbsolutePath(), ff.getParent(), ff.lastModified(), 
								Security.getCRC32Checksum(ff.getAbsolutePath()), Security.getMD5Checksum(ff.getAbsolutePath()),
								Security.getSHA1Checksum(ff.getAbsolutePath()), Long.toString(ff.length())), null);
							*/
							
						}
						Thread.sleep(100);
						fileCount++;
						
					}
				}
				
				
				
			}else{
				if(f.canRead() && f.canWrite() &&  !f.isHidden() &&  f.exists()){
					publishProgress(new String[] {"", f.getAbsolutePath()});
					ModelFilesInfo mfi = 
							(ModelFilesInfo) db.select(Tables.files_info, ModelFilesInfo.class, 
								new Object[][] {
									{"path", f.getAbsolutePath()},
									{"dir", f.getParent()}
									
									/*,
									{"md5", Security.getMD5Checksum(f.getAbsolutePath())},
									{"size", Long.toString(f.length())},
									{"last_mod", f.lastModified()}
									
									*/
								}
							, null);
					Thread.sleep(10);
					if(mfi.getPath() != null){
						Log.i("DirectoryScanner", "File " + mfi.getPath() + " already exists in the database, skipping . . .");
						
						if(Long.valueOf(f.lastModified()).compareTo(mfi.getLast_mod()) > 0){
							// file was modified
							
							SF.	filesQueue.add(mfi);
							
							// update dirs_info
							//db.update(Tables.dirs_info, mdi,  new ModelDirsInfo(sdCard, ff.getAbsolutePath(), ff.lastModified()), null);
							
						}
						
					}else{
						Log.i("DirectoryScanner", "File " +  f.getPath() + " does not exist in the database, adding to files queue . . .");
						
						
						// add this file to fileQueue
						SF.filesQueue.add(new ModelFilesInfo(f.getAbsolutePath(),f.getParent()));
						/*
						db.insert(Tables.files_info, new ModelFilesInfo(f.getAbsolutePath(), f.getParent(), f.lastModified(), 
							Security.getCRC32Checksum(f.getAbsolutePath()), Security.getMD5Checksum(f.getAbsolutePath()),
							Security.getSHA1Checksum(f.getAbsolutePath()), Long.toString(f.length())), null);
							*/
					}
					Thread.sleep(10);
					fileCount++;
				}
			}
		}
	}
	
	
	
	private Task TASKHANDLER = new Task();
	private NotificationManager mNotifyManager;
	private NotificationCompat.Builder mBuilder;
	
	private String TAG = "DirectoryScanner";
	
	private PowerManager powerManager;
	private WakeLock wakeLock;
	
	@Override
	public void onCreate() {
		super.onCreate();
		db = getDb(this);
		this.SF = ((SharpFixApplicationClass)getApplication() );
		try{
			 ModelPreferences result = (ModelPreferences) db.selectAll(Tables.preferences, ModelPreferences.class,null)[0];
				((SharpFixApplicationClass) getApplication()).setAccountId(result.getAccount());
				((SharpFixApplicationClass) getApplication()).setFddPref(result.getFdd_pref());
				((SharpFixApplicationClass) getApplication()).setFddSwitch(result.getFdd_switch());
				((SharpFixApplicationClass) getApplication()).setFdSwitch(result.getFd_switch());
				((SharpFixApplicationClass) getApplication()).setFddFilterSwitch(result.getFdd_Filter_switch());
				((SharpFixApplicationClass) getApplication()).setFdFilterSwitch(result.getFd_Filter_switch());
				((SharpFixApplicationClass) getApplication()).setServiceSwitch(result.getSss_switch());
				((SharpFixApplicationClass) getApplication()).setServiceHour(result.getSss_hh());
				((SharpFixApplicationClass) getApplication()).setServiceMin(result.getSss_mm());
				((SharpFixApplicationClass) getApplication()).setServiceAMPM(result.getSss_ampm());
				((SharpFixApplicationClass) getApplication()).setServiceUpdateSwitch(result.getSss_update());
				((SharpFixApplicationClass) getApplication()).setServiceRepeat(result.getSss_repeat());
				((SharpFixApplicationClass) getApplication()).setServiceNoti(result.getSss_noti());
				((SharpFixApplicationClass) getApplication()).setAuSwitch(result.getAu_switch());
		}catch(Exception e){
			
		}
		powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		        "MyWakelockTag");
		wakeLock.acquire();
		

		
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "########################################");
		Log.i(TAG, "Destroying Directory Scanner");
		TASKHANDLER.cancel(true);
		
	}
	
	/*
	NotificationManager mNotifyManager;
	NotificationCompat.Builder mBuilder;
	  */     
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "########################################");
		Log.i(TAG, "Creating Directory Scanner");
		
		Intent notificationIntent = new Intent(this, SubMenuDirectScanControls.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent dspendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("SharpFix Directory Scanner");
		mBuilder.setContentIntent(dspendingIntent);
		mBuilder.setContentText("Scanning SD-Card changes");
	    mBuilder.setSmallIcon(R.drawable.ic_launcher);
		
	   

	    
	    
	    
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
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
