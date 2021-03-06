/*
 * FileDesignationScanner.java
 * 1.1.2 Alpha Release Version
 * 
 * Magarzo, Randolf Josef V.

 * Copyright (c) 2013 Magarzo, Randolf Josef V.
 * Project SharpFix Android
 * 
 * SHARPFIX ANDROID FILE MANAGEMENT UTILITY 2014 - 2015 
 * Area of Computer Science College of Accountancy, 
 * Business Administration and Computer Studies
 * San Sebastian College - Recoletos, Manila, Philippines
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. 
 * If not, see http://www.gnu.org/licenses
 * 
 */

package tk.idclxvii.sharpfixandroid;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import tk.idclxvii.sharpfixandroid.databasemodel.*;
import tk.idclxvii.sharpfixandroid.serverutils.MailSender;
import tk.idclxvii.sharpfixandroid.serverutils.ServerCommunication;
import tk.idclxvii.sharpfixandroid.utils.AndroidUtils;
import tk.idclxvii.sharpfixandroid.utils.FileProperties;
import tk.idclxvii.sharpfixandroid.utils.Zip;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This class is a sub class of {@link Service} responsible for 
 * moving files to its designated directory according to 
 * its file type, which has been set by the user.
 * <br />
 * <br />
 * Basically, this service uses the Magic Number detection algorithm
 * in order to accurately define what file type a certain file is. 
 * This means that file extensions does not matter or does not contribute
 * on how this module detects a file type. After file type detection,
 * the service checks the database if a certain rule exists with regards
 * to the detected file type. If there is, then the said file is moved to
 * its designation folder, else, the file is left as is proceeding to the
 * next one.
 * <br />
 * <br />
 * For example, the user set a new File Designation rule regarding {@code *.jpg} files,
 * which is designated to be moved inside {@code /mnt/sdcard/Pictures/JPG/}. This class
 * reads all the files in the file system, reads their file types and handles
 * the {@code *.jpg} files, moving all {@code *.jpg} files outside {@code /mnt/sdcard/Pictures/JPG/}
 * to the said directory. If {@code /mnt/sdcard/SC001.jpg} exists, it is copied inside
 * the designation directory and deletes the original file, thus moving it
 * to {@code /mnt/sdcard/Pictures/JPG/SC001.jpg}
 * <br />
 * <br />
 * 
 * @version 1.1.2 Alpha Release Version
 * @author Magarzo, Randolf Josef V.
 *
 */
public class FileDesignationScanner extends Service{
	
	/**
	 * The TAG to be used by {@link android.util.Log}
	 * when performing {@code Logcat} operations.
	 */
	private final String TAG = this.getClass().getSimpleName();
	
	/**
	 * The {@code SQLiteHelper} instance to be used by this context
	 * when performing {@link SQLiteHelper} operations.
	 */
	private SQLiteHelper db;
	
	/**
	 * The Application Context, containing the recently
	 * loaded user preferences and application settings
	 * on global context.
	 * @see {@link Context#getApplicationContext()}
	 */
	private SharpFixApplicationClass SF;
	
	/**
	 * An Asynchronous Thread that contains all the
	 * tasks to be executed by this service when
	 * the service starts.
	 */
	private Task TASKHANDLER = new Task();
	
	/**
	 * A {@link NotificationManager} that will be used
	 * by this service. This manages the Notifications
	 * that the service is going to use while the service
	 * is running.
	 */
	private NotificationManager mNotifyManager;
	
	/**
	 * A {@link NotificationCompat.Builder} that will be used
	 * by this service. This builds the Notification before 
	 * it is finally shown, while the service is running
	 */
	private NotificationCompat.Builder mBuilder;
	
	
	/**
	 * A {@link PowerManager} that will be used
	 * by this service. This manages the power and
	 * wake lock whenever the phone is locked when
	 * the service initiates.
	 * 
	 */
	private PowerManager powerManager;
	
	/**
	 * A {@link WakeLock} that will be used
	 * by this service. This will be the reference
	 * for acquiring wake locks and other wake locks
	 * activities
	 */
	private WakeLock wakeLock;
	
	
	
	
	private ModelPreferences prefs; 
	
	private Object[] filesQueueTemp;
	private Object[] magicNumbersQueueTemp;
	private Object[] fdRulesTemp;
	
	private List<Object> filesQueue;
	private List<Object> magicNumbersQueue;
	private List<Object> fdRules;
	
	private int scannedFiles = 0;
	private long movedFileSize = 0;
	private int movedFiles = 0;
	
	public FileDesignationScanner(){
		
	}

	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	
	private class Task extends GlobalAsyncTask<File, String, Void>{

		@Override
		protected Void doTask(File... params) throws Exception {
			// TODO Auto-generated method stub
			scannedFiles = SF.filesQueue.size();
			
			
			
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
					":\nInitializing File Designation Scanner");
			/*
			Log.i(TAG, "########################################");
			Log.i(TAG, "Starting File Designation Scanner");
			*/
			long start = System.currentTimeMillis();
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					"Initializing File Designation Scanner" );
			publishProgress(new String[] {"File Designation Scan", "Initializing Scan: " + (FileProperties.formatFileLastMod(start))});
			
			// single query all needed information
			// to avoid redundant database transactions which promotes optimization and speed
			prefs = (ModelPreferences)db.selectAll(Tables.preferences, ModelPreferences.class , null)[0];
			filesQueueTemp = db.selectAll(Tables.files_info, ModelFilesInfo.class, null);
			magicNumbersQueueTemp =  db.selectAll(Tables.magic_number, ModelMagicNumber.class, null);
			fdRulesTemp = db.selectAll(Tables.file_designation_settings, ModelFdSettings.class, null);
			
			filesQueue = new ArrayList<Object>(Arrays.asList(filesQueueTemp));
			magicNumbersQueue =  new ArrayList<Object>(Arrays.asList(magicNumbersQueueTemp));
			fdRules =  new ArrayList<Object>(Arrays.asList(fdRulesTemp));
			
			/*
			 * File Designation Scan Algorithm
			 * @author IDcLxViI
			 * 11/17/2014
			 * 
			
			
			FDDS Algorithm:
				* check fd switch
				* check filter switch
					* filter:
						check for filtered dirs and sub dirs then remove it from dirsQueue
						check for files inside filtered dirs and sub dirs then remove it from filesQueue
						check for files inside filtered files then remove it from filesQueue
					
					* fd scan
						* scan the given file 
						* update the file information (try insert first, then update if it fails)
						* remove the file in the filesQueue
						* for each element in dirsQueue, check if ALL the files in filesQueue containing
						 	the current element's path has been processed. if it was, then update the element's dir information
						 	and then remove it from dirsQUeue (try db insert first, then update if it fails)
						 	
						 	
			*/
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
					":\nChecking File Designation Settings" );
			publishProgress(new String[] {"", "Checking FDD Filter Switch" });
			
			if(prefs.getFd_switch() == 1){
				// fdd scan is turned on
				DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
						":\nFile Designation is turned on!\nChecking if there are rules defined in FD Settings . . .");
				/*
				Log.i(TAG, "########################################");
				Log.i(TAG, "File Designation is turned on!");
				
				Log.i(TAG, "########################################");
				Log.i(TAG, "Checking if there are rules defined in FD Settings . . .");
				*/
				
				if(fdRules.size() > 0){
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							":\nThere are defined File Designation Rules!");
					/*
					Log.i(TAG, "########################################");
					Log.i(TAG, "There are defined FD Rules!");
					*/
					if(prefs.getFd_Filter_switch() == 1){
						// fdd filter is turned on, filter the filesQueue
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFile Designation Filtering is turned on!");
						/*
						Log.i(TAG, "########################################");
						Log.i(TAG, "FD Filtering is turned on!");
						*/
						SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
								":\nFiltering queued files" );
						publishProgress(new String[] {"", "Filtering scanned files"});
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFiltering queued files");
						/*
						Log.i(TAG, "########################################");
						Log.i(TAG, "Filtering queued Files and Directories");
						*/
						filter();
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nStarting File Designation Scan!");
						/*
						Log.i(TAG, "########################################");
						Log.i(TAG, "Initializing FD Scan!");
						*/
						SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
								":\nStarting File Designation Scan" );
						publishProgress(new String[] {"", "Starting scan" });
						scan();
					}else{
						// fdd filter is turned off, scan w/o filtering
						
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFile Designation Filtering is turned off!\nStarting File Designation Scan!");
						/*
						Log.i(TAG, "########################################");
						Log.i(TAG, "FD Filtering is turned off!");
						*/
						SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
								":\nStarting File Designation Scan" );
						publishProgress(new String[] {"", "Starting scan" });
						
						scan();
					}
				}else{
					SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
							":\nThere are no defined File Designation Rules!\n" +
							"File Designation Scanner will not now halt service executione because no rules has been set" );
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							":\nThere are no defined File Designation Rules!\n" +
							"File Designation Scanner will not now halt service executione because no rules has been set");
					/*
					Log.i(TAG, "########################################");
					Log.i(TAG, "There are no defined FD Rules!");
					Log.i(TAG, "FD Scanner will not initiate because no rules has been set");
					*/
				}
				
				
			}else{
				// do nothing, fdd scan is turned off
				DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
						":\nFile Designation is turned off!");
				/*
				Log.i(TAG, "########################################");
				Log.i(TAG, "File Designation is turned off!");
				*/
			}
			
			/*
			for(Object o : filesQueue){
				checkPreferences( new File( ((ModelFilesInfo)o).getPath() ) );
			}
			*/
			long end = System.currentTimeMillis();
			long runTime = end - start;
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
					":\nSharpFix File Designation Scanner finished scanning!\n\nFiles scanned: " + scannedFiles +
					"\nMoved files: " + movedFiles + "\nTotal file size moved: " + FileProperties.formatFileSize(movedFileSize) +
					"\n\nTime elapsed: " + String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
							TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
							TimeUnit.MILLISECONDS.toSeconds(runTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))));
			
			publishProgress(new String[] {"File Designation Scan Finished", "Time elapsed:" + 
					String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
							TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
							TimeUnit.MILLISECONDS.toSeconds(runTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))),
							"" });
			//checkPreferences(params[0]);
			
			
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
					":\nSharpFix File Designation Scanner finished scanning!\n\nFiles scanned: " + scannedFiles +
					"\nMoved files: " + movedFiles + "\nTotal file size moved: " + FileProperties.formatFileSize(movedFileSize) +
					"\n\nTime elapsed: " + String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
							TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
							TimeUnit.MILLISECONDS.toSeconds(runTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))));
			/*
			Log.i(TAG, "########################################");
			Log.i(TAG, "File Designation Scanner Finished!");
			*/
			
			
			try {   
				Log.e("AndroidUtils", "SENDING MAIL");
				AndroidUtils.zipLogs(FileDesignationScanner.this);
				AndroidUtils.emailLogs(FileDesignationScanner.this, SF.getEmail());
			}catch(Exception e){
				e.printStackTrace();
			}
			
			AndroidUtils.logProgressReport(FileDesignationScanner.this, DirectoryScanner.logs.toArray(new String[DirectoryScanner.logs.size()]));
			AndroidUtils.logScanReport(FileDesignationScanner.this, SF.logsQueue.toArray(new String[SF.logsQueue.size()]));
			return null;
			
			
		}

		@Override
		protected void onCancelled(){
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					"File Designationn Scanner's execution has been manually cancelled!");
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
					":\nFile Designation Scanner's execution has been manually cancelled!");
			/*
			Log.i(TAG, "########################################");
			Log.i(TAG, "Directory Scanner's execution has been cancelled!");
			*/
			mBuilder.setProgress(0, 0, true);
			mBuilder.setContentText("FD Scanner has been cancelled.");
			mNotifyManager.notify(3, mBuilder.build());
			try {   
				Log.e("AndroidUtils", "SENDING MAIL");
				AndroidUtils.zipLogs(FileDesignationScanner.this);
				AndroidUtils.emailLogs(FileDesignationScanner.this, SF.getEmail());
			}catch(Exception e){
				e.printStackTrace();
			}
			AndroidUtils.logProgressReport(FileDesignationScanner.this, DirectoryScanner.logs.toArray(new String[DirectoryScanner.logs.size()]));
			AndroidUtils.logScanReport(FileDesignationScanner.this, SF.logsQueue.toArray(new String[ SF.logsQueue.size()]));
			
		}
		
		@Override
		protected void onException(Exception e) {
			// TODO Auto-generated method stub
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
					":\nException on File Designation Scanner!!!");
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					"File Designation Scanner's execution might become unstable and produce unreliable "
					+ "results due to an uncaught exception!\n\nException details:");
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" + 
					errors.toString());
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					errors.toString());	
			try {   
				Log.e("AndroidUtils", "SENDING MAIL");
				AndroidUtils.zipLogs(FileDesignationScanner.this);
				AndroidUtils.emailLogs(FileDesignationScanner.this, SF.getEmail());
			}catch(Exception ee){
				e.printStackTrace();
			}
			AndroidUtils.logProgressReport(FileDesignationScanner.this, DirectoryScanner.logs.toArray(new String[DirectoryScanner.logs.size()]));
			AndroidUtils.logScanReport(FileDesignationScanner.this, SF.logsQueue.toArray(new String[ SF.logsQueue.size()]));
			
			/*
			Log.i(TAG, "########################################");
			Log.i(TAG, "Exception on File Designation Scanner!!!");
			Logcat.logCaughtException("FileDesignationScanner", e.getStackTrace());
			*/
		}
		
		@Override
		protected void onProgressUpdate(String... params){
			if(params.length > 2){
				// ######################## ALPHA RELEASE 1.1.3 ########################
				/*
				 * Fully activated Notifications control on all services
				 * Also, notifications automatically closes once the scan has been finished
				 */
				if(SF.getServiceNoti() == 1){
				// ######################## ALPHA RELEASE 1.1.3 ########################
					
					mBuilder.setProgress(0, 0, true);
					mBuilder.setContentTitle(params[0]);
					mBuilder.setContentText(params[1]);
					mNotifyManager.notify(3, mBuilder.build());
					// ######################## ALPHA RELEASE 1.1.3 ########################
					mNotifyManager.cancel(3);
					// ######################## ALPHA RELEASE 1.1.3 ########################
					//stopForeground(true);
				}
				stopSelf();
				
			}else{
				
				if(params[0].length() > 0){
					// there are changes in notification title
					if(SF.getServiceNoti() == 1){
						mBuilder.setContentTitle(params[0]);
						mBuilder.setProgress(0, 0, true);
						mBuilder.setContentText(params[1]);
						mNotifyManager.notify(3, mBuilder.build());
					}
				}else{
					// there are no changes in notification title
					if(SF.getServiceNoti() == 1){
						mBuilder.setProgress(0, 0, true);
						mBuilder.setContentText(params[1]);
						mNotifyManager.notify(3, mBuilder.build());
					}
					
				}
			}
			
		}
		
		private void filter() throws IllegalArgumentException, InstantiationException,
		IllegalAccessException, NoSuchMethodException, InvocationTargetException{
		
		
			Object[] mdf = db.selectMulti(Tables.dir_filter, ModelDirFilter.class,
					new Object[][] { {"filter", "fd"}} ,null);
			
			
			Object[] mff = db.selectMulti(Tables.file_filter, ModelFileFilter.class,
					new Object[][] { {"filter", "fd"}} ,null);
			
			
					//db.selectAll(Tables.dir_filter, ModelDirFilter.class, null);
			
			// dirs filter
			for(Object filter : mdf){
				
				// filter filesQueue (sub dir instances)
				//for(Object file : filesQueue){
				for(Iterator<Object> it = filesQueue.iterator(); it.hasNext();){
					Object file = it.next();
					if( ((ModelFilesInfo)file).getPath().contains( ((ModelDirFilter)filter).getDir())){
						it.remove();
						
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFile " +
								((ModelFilesInfo)file).getPath() + " was detected to be a inside the directory of "
								+ ((ModelDirFilter)filter).getDir() + ", which is being filtered!\n" +
								"Removing " + ((ModelFilesInfo)file).getPath() + " from files queue.");
						
						/*
						Log.i(TAG, "File Filter Detection");
						Log.i(TAG, ((ModelFilesInfo)file).getPath() + " was detected to be a inside the directory of "
								+ ((ModelDirFilter)filter).getDir() + ", which is being filtered!");
						Log.i(TAG, "Removing " + ((ModelFilesInfo)file).getPath() + " from filesQueue");
						*/
					}
				}
				
				
				
			}
			
			// files filter
			for(Object filter : mff){
				//for(Object file : filesQueue){
				for(Iterator<Object> it = filesQueue.iterator(); it.hasNext();){
					Object file = it.next();
					if( ((ModelFilesInfo)file).getPath().equals( ((ModelFileFilter)filter).getFile())){
						it.remove();
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFile " +
								((ModelFilesInfo)file).getPath() + " is being directly filtered!"
								 + "\nFilter rule: "+ ((ModelFileFilter)filter).getFile() +
								"\nRemoving " + ((ModelFilesInfo)file).getPath() + " from files queue.");
						/*
						Log.i(TAG, "File Filter Detection");
						Log.i(TAG, ((ModelFilesInfo)file).getPath() + " is being filtered!"
								 + "Filter was: "+ ((ModelFileFilter)filter).getFile());
						Log.i(TAG, "Removing " + ((ModelFilesInfo)file).getPath() + " from filesQueue");
						*/
					}
				}
			}
			
		}
	
		
		private void smartFilter(){
			// ######################## ALPHA RELEASE 1.1.3 ########################
			/*
			 * Added a smart filtering that automatically gets the list of all android
			 * applications installed on the phone, and ignore all externalDir and internalDir
			 * owned by the installed applications(Internal Memory, not root dir)
			 * Also, add a default filter rule that that filters all files under Android
			 * folder (both sd0 and sd1 if device has two volumes).
			 * The default filter rule is created upon installation or initial set up of
			 * SharpFix and disabling it and deleting it is allowed, but is discouraged.
			 * 
			 * GITHUB Issues Link: https://github.com/idclxvii/SharpFixAndroid/issues/2
			 * 
			 * */

			PackageManager pm = getPackageManager();
			//get a list of installed apps.
			List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

						
			// smart filter dirsQueue
						
			for (ApplicationInfo packageInfo : packages) {
				for(Iterator<Object> it = filesQueue.iterator(); it.hasNext();){
					Object file = it.next();
					if( !(packageInfo.packageName.contains("com.android") || packageInfo.packageName.equals("android")) &&
							((ModelFilesInfo)file).getPath().contains( packageInfo.packageName ) &&
							! ((ModelFilesInfo)file).getPath().contains(".apk")){
						Log.i(TAG, "Current Application Package: " + packageInfo.packageName );
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFile " + 
								((ModelFilesInfo)file).getPath() + " was detected to be an application-owned directory of "
								+ packageInfo.packageName + ", which is being SMARTLY filtered!\n" +
								"Removing " + ((ModelFilesInfo)file).getPath() + " from folders queue");
						Log.i(TAG, ":\nFolder " + 
								((ModelFilesInfo)file).getPath() + " was detected to be an application-owned directory of "
								+ packageInfo.packageName + ", which is being SMARTLY filtered!\n" +
								"Removing " + ((ModelFilesInfo)file).getPath() + " from folders queue");
						it.remove();
					}
					
				}
			}
						
			
				
			// ######################## ALPHA RELEASE 1.1.3 ########################
						
						
		}
		
		private void scan() throws IllegalArgumentException, InstantiationException,
		IllegalAccessException, NoSuchMethodException, InvocationTargetException, FileNotFoundException, 
		IOException, NoSuchAlgorithmException{
			
			// ######################## ALPHA RELEASE 1.1.3 ########################
						/*
						 * Added a smart filtering that automatically gets the list of all android
						 * applications installed on the phone, and ignore all externalDir and internalDir
						 * owned by the installed applications(Internal Memory, not root dir)
						 * Also, add a default filter rule that that filters all files under Android
						 * folder (both sd0 and sd1 if device has two volumes).
						 * The default filter rule is created upon installation or initial set up of
						 * SharpFix and disabling it and deleting it is allowed, but is discouraged.
						 * 
						 * GITHUB Issues Link: https://github.com/idclxvii/SharpFixAndroid/issues/2
						 * 
						 * */
						
						SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
								":\nHalting File Designation Scan to make way for SharpFix Smart Filtering � Feature"+
								"\nInitializing Smart Filtering . . .");
						
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nHalting File Designation Scan to make way for SharpFix Smart Filtering � Feature"+
								"\nInitializing Smart Filtering . . .");
						smartFilter();
						SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
								":\nSharpFix Smart Filtering � Completed.\nReinitializing File Designation Scan");
						
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nSharpFix Smart Filtering � Completed.\nReinitializing File Designation Scan");
						
						// ######################## ALPHA RELEASE 1.1.3 ########################
						
			
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
					":\nSharpFix File Signature Definitions: " + SQLiteHelper.getDatabaseVersion());
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
					":\nSharpFix File Signature Definitions: " + SQLiteHelper.getDatabaseVersion());
			//android.os.Debug.waitForDebugger();
			//for(Object queuedFile : filesQueue){
			
			for(Iterator<Object> it = filesQueue.iterator(); it.hasNext(); ){
				Object queuedFile = it.next();
				scannedFiles++;
				File f = new File(((ModelFilesInfo)queuedFile).getPath());
				publishProgress(new String[] {"", f.getAbsolutePath()});
				
				try{
					//android.os.Debug.waitForDebugger();
					Object[] result = db.selectConditional(Tables.magic_number, ModelMagicNumber.class, 
						new Object[][] {
								{"signature_4_bytes", Security.getMagicNumber(f, 4), " OR "},
								{"signature_8_bytes", Security.getMagicNumber(f, 8)}
						}
					, null);
					
					if(result.length > 0){
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFile: " +f.getAbsolutePath() + "\n" +
								"Current Queued File Information: \n" +
								"File Type:" + ((ModelMagicNumber)result[0]).getFile_type() + "\n" +
								"MIME:" + ((ModelMagicNumber)result[0]).getMime() + "\n" + 
								"4-bytes Signature:" + ((ModelMagicNumber)result[0]).getSignature_4_bytes() + "\n" + 
								"8-bytes Signature:" + ((ModelMagicNumber)result[0]).getSignature_8_bytes());
						/*
						Log.i(TAG, "########################################");
						Log.i(TAG, "File: " +f.getAbsolutePath());
						Log.i(TAG, "Current Queued File Information: ");
						Log.i(TAG, "File Type:" + ((ModelMagicNumber)result[0]).getFile_type());
						Log.i(TAG, "MIME:" + ((ModelMagicNumber)result[0]).getMime());
						Log.i(TAG, "4-bytes Signature:" + ((ModelMagicNumber)result[0]).getSignature_4_bytes());
						Log.i(TAG, "8-bytes Signature:" + ((ModelMagicNumber)result[0]).getSignature_8_bytes());
						*/
						Object[] rule = db.selectConditional(Tables.file_designation_settings, ModelFdSettings.class,
								new Object[][] {{"file_type", ((ModelMagicNumber)result[0]).getFile_type(), " AND NOT "},
												{"designation_path", f.getParent()}	},
						null);
						if(rule.length > 0){
							// there are defined rules on the current file
							if(AndroidUtils.cutPasteFile(f,
									new File( ((ModelFdSettings)rule[0]).getDesignation_path() + "/" + f.getName() ), 0    )){
								// successfully moved the file
								movedFiles += 1;
								movedFileSize += new File( ((ModelFdSettings)rule[0]).getDesignation_path() + "/" + f.getName() ).length();
								SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
										"\nFile " + f.getAbsolutePath() + " is being designated!" +
										":\nRule to be applied: " + ((ModelFdSettings)rule[0]).getRule_name() + 
										"\nDesignation path: " + ((ModelFdSettings)rule[0]).getDesignation_path() +
										"\nAwaiting designation result . . ." + 
										"\nSuccessfully moved " + f.getAbsolutePath() + " to " + 
										((ModelFdSettings)rule[0]).getDesignation_path());
								
								DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										"\nFile " + f.getAbsolutePath() + " is being designated!" +
										":\nRule to be applied: " + ((ModelFdSettings)rule[0]).getRule_name() + 
										"\nDesignation path: " + ((ModelFdSettings)rule[0]).getDesignation_path() +
										"\nAwaiting designation result . . ." + 
										"\nSuccessfully moved " + f.getAbsolutePath() + " to " + 
										((ModelFdSettings)rule[0]).getDesignation_path());
								
								/*
								Log.i(TAG, "########################################");
								Log.i(TAG, "Successfully moved " + f.getAbsolutePath() + " to " +
										((ModelFdSettings)rule[0]).getDesignation_path());
								*/
								ModelFilesInfo oldParams = ((ModelFilesInfo)queuedFile);
								// public ModelFilesInfo(String path, String dir, Long lastMod, String crc32, String md5, String sha1, String size){
								
								db.update(Tables.files_info, oldParams ,new ModelFilesInfo(
										((ModelFdSettings)rule[0]).getDesignation_path() + "/" +f.getName(),
										((ModelFdSettings)rule[0]).getDesignation_path(), oldParams.getLast_mod(),
										oldParams.getCrc32(), oldParams.getMd5(), oldParams.getSha1(), oldParams.getSize())
								, null);
								
							}else{
								// failed to move the file
									// either unable to copy or unable to delete the original file
								SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
										"\nFile " + f.getAbsolutePath() + " is being designated!" +
										":\nRule to be applied: " + ((ModelFdSettings)rule[0]).getRule_name() + 
										"\nDesignation path: " + ((ModelFdSettings)rule[0]).getDesignation_path() +
										"\nAwaiting designation result . . ." + 
										"\nFailed to  move the file " + f.getAbsolutePath() + " to " + 
										((ModelFdSettings)rule[0]).getDesignation_path());
								
								DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										"\nFile " + f.getAbsolutePath() + " is being designated!" +
										":\nRule to be applied: " + ((ModelFdSettings)rule[0]).getRule_name() + 
										"\nDesignation path: " + ((ModelFdSettings)rule[0]).getDesignation_path() +
										"\nAwaiting designation result . . ." + 
										"\nFailed to move the file " + f.getAbsolutePath() + " to " + 
										((ModelFdSettings)rule[0]).getDesignation_path());
								
								
								DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										": Failed to move " + f.getAbsolutePath() + " to " +
										((ModelFdSettings)rule[0]).getDesignation_path());
								/*
								Log.e(TAG, "########################################");
								Log.e(TAG, "Failed to move " + f.getAbsolutePath() + " to " +
										((ModelFdSettings)rule[0]).getDesignation_path());
								*/
							}
						}else{
							// there are no defined rules on the current file
							
							DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
									"\nFile " + f.getAbsolutePath() + " is NOT being designated!");
							
						}
						/*
						Log.i(TAG, "########################################");
						Log.i(TAG, "Rule: " + ((ModelFdSettings)rule[0]).getRule_name());
						Log.i(TAG, "Designation path: " + ((ModelFdSettings)rule[0]).getDesignation_path());
						*/
					
					}else{
						// undefined file type

						// ######################## ALPHA RELEASE 1.1.3 ########################
						/*
						 * Removed the verbose logging of Unidentified file types in Scan Logs
						
						SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFile: " +f.getAbsolutePath() + " has been detected to be unidentified!" + 
								"\nIt seems that the file's signature is not known yet. See the information below."+
								"\nCurrent Queued File Information:" +
								"\n4-bytes Signature:" +  Security.getMagicNumber(f, 4) + 
								"\n8-bytes Signature:" +  Security.getMagicNumber(f, 8));
						
						 */
						// ######################## ALPHA RELEASE 1.1.3 ########################
						 
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFile: " +f.getAbsolutePath() + " has been detected to be unidentified!" + 
								"\nIt seems that the file's signature is not known yet. See the information below."+
								"\nCurrent Queued File Information:" +
								"\n4-bytes Signature:" + Security.getMagicNumber(f, 4) + 
								"\n8-bytes Signature:" +  Security.getMagicNumber(f, 8));
						
					}
					
					
					
					
				}catch(Exception e){
					/* 
					 * When this code is run, it means one of the following:
					 * 
					 * 1. The current queued file type is unidentified
					 * 2. There are no rules defined for the current queued file type
					 * 3. The current queued file is already inside its designated folder
					 * 
					 */

					SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							":\nDetected File: " +f.getAbsolutePath() + "\nException details:\n");
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							":\nDetected File: " +f.getAbsolutePath() + "\nException details:\n");
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + 
							errors.toString());
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + 
								errors.toString());
					
					/*
					Log.i(TAG, "########################################");
					Log.i(TAG, "Exception details:");
					e.printStackTrace();
					Log.i(TAG, "Detected File: " +f.getAbsolutePath());
					*/
					
				}
				it.remove();
				
			}
			
			// checkDirsQueue();
		}
		
		/*
		private void checkDirsQueue() throws SQLiteConstraintException, IllegalArgumentException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException{
			//for(Object dir : dirsQueue){
			for(Iterator<Object> it = SF.dirsQueueFD.iterator(); it.hasNext();){
				Object dir = it.next();
				boolean dirFinished = true;
				for(Object file : SF.filesQueueFD){
					if(((ModelFilesInfo)file).getPath().contains( ((ModelDirsInfo)dir).getPath())){
						dirFinished = false;
					}
				}
				if(dirFinished){
					if(db.insert(Tables.dirs_info, (ModelDirsInfo) dir, null)){
						Log.i(TAG, "########################################");
						Log.i(TAG, "Inserting directory" + ((ModelDirsInfo) dir).getPath() + " successful!" );
						Log.i(TAG, "########################################");
					}else{
						Log.i(TAG, "########################################");
						Log.i(TAG, "Inserting directory" + ((ModelDirsInfo) dir).getPath() + " not successful!" );
						Log.i(TAG, "Trying update . . ." );
						Log.i(TAG, "########################################");
						
						//(new ModelDirsInfo(((ModelDirsInfo)dir).getSd_id(),((ModelDirsInfo)dir).getPath(), ((ModelDirsInfo)dir).getLast_mod())),
						if(db.update(Tables.dirs_info, 
								db.selectMulti(Tables.dirs_info, ModelDirsInfo.class,
										new Object[][] { {"path" , ((ModelDirsInfo)dir).getPath()}, {"sd_id", ((ModelDirsInfo)dir).getSd_id()}}, null)[0],
								new ModelDirsInfo(((ModelDirsInfo)dir).getSd_id(),((ModelDirsInfo)dir).getPath(), ( new File( ((ModelDirsInfo)dir).getPath()).lastModified())),
								null)){
							Log.i(TAG, "########################################");
							Log.i(TAG, "Updating directory" + ((ModelDirsInfo) dir).getPath() + " successful!" );
							Log.i(TAG, "########################################");
						}else{
							Log.e(TAG, "########################################");
							Log.e(TAG, "Updating directory" + ((ModelDirsInfo) dir).getPath() + " not successful!" );
							Log.e(TAG, "########################################");
						}
					}
					
					
					it.remove();
				}
			}
			
			
		}
	
		*/
	
	}

	
	
	@Override
	public void onCreate() {
		super.onCreate();
		db = getDb(this);
		this.SF = ((SharpFixApplicationClass) getApplication() );
		powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		        "MyWakelockTag");
		wakeLock.acquire();
		
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
				":\nDestroying File Designation Scanner");
		/*
		Log.i(TAG, "########################################");
		Log.i(TAG, "Destroying File Designation Scanner");
		*/
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
		/*
		Log.i(TAG, "########################################");
		Log.i(TAG, "Creating File Designation Scanner");
		*/
		DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
				":\nCreating File Designation Scanner");
		Intent notificationIntent = new Intent(this, SubMenuDirectScanControls.class);
		notificationIntent.putExtra("notification", true);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent dspendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("SharpFix File Designation Scanner");
		mBuilder.setContentIntent(dspendingIntent);
		mBuilder.setContentText("Scanning For Designation");
	    mBuilder.setSmallIcon(R.drawable.fd_icon);
		
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
