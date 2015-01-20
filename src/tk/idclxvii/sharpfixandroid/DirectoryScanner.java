/*
 * DirectoryScanner.java
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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import tk.idclxvii.sharpfixandroid.databasemodel.*;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import tk.idclxvii.sharpfixandroid.utils.*;

/**
 * This class is a sub class of {@link Service} responsible for 
 * scanning the filesystem before a File Duplication Detection and
 * File Designation is initiated. 
 * <br />
 * <br />
 * Basically, this service uses two methods in checking the file
 * records. First, it uses the Reverse Scanning Algorithm, which
 * scans all the files and folders recorded in the SQLite database.
 * This will ensure that all the records in the database are correct
 * and is existing. This algorithm handles file exceptions as part 
 * of its tasks, so that if a file has been deleted, it automatically
 * deletes the said file record on the database.
 * <br />
 * <br />
 * Secondly, this service uses the Recursive Scanning Algorithm, which
 * checks all the actual files contained and cross-examines the database
 * contents from the actual files being scanned.
 * <br />
 * <br />
 * This service has been created to ensure that no redundant scans will
 * be done which also saves the time of scanning the files. This will 
 * also make sure that File Designation and File Duplication Detection
 * will only scan the files detected to be modified since the last scan,
 * instead of redundantly scanning the whole file system
 * 
 * @version 1.1.2 Alpha Release Version
 * @author Magarzo, Randolf Josef V.
 *
 */
public class DirectoryScanner extends Service{
	
	/**
	 * The List of Strings containing the logged operations to be 
	 * logged by {@code Progress Logs}
	 * @see {@link AndroidUtil.logProgressReport}
	 */
	public static List<String> logs =  new ArrayList<String>();
	
	
	/**
	 * The TAG to be used by {@link android.util.Log}
	 * when performing {@code Logcat} operations.
	 */
	private final String TAG =  this.getClass().getSimpleName();
	
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
	
	/**
	 * Returns a new SQLiteHelper instance 
	 * 
	 * @param context - the context that will be using this database access
	 * @return the {@link SQLiteHelper} instance that was instantiated using the 
	 * context given as a parameter
	 */
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	/**
	 * An Asynchronous Thread that contains all the
	 * tasks to be executed by this service when
	 * the service starts.
	 */
	private class Task extends GlobalAsyncTask<File, String, Void>{

		private int dirCount  =0 , fileCount = 0;
		Object [] sd;
		Object[] dirs;
		Object[] files;
		ModelPreferences prefs;
		
		
		@Override
		protected Void doTask(File... params) throws Exception {
		
			SF.initScanQueue();
			logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\nStarting Directory Scanner:");
			long start = System.currentTimeMillis();
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					"Initializing Directory Scanner");
			publishProgress(new String[] {"Initializing Directory Scanner", "Time start:" + (FileProperties.formatFileLastMod(start)) });
			sd = db.selectAll(Tables.sd_info, ModelSD.class, null);
			dirs = db.selectAll(Tables.dirs_info, ModelDirsInfo.class, null);
			files = db.selectAll(Tables.files_info, ModelFilesInfo.class, null);
			try{
				prefs = (ModelPreferences)db.selectAll(Tables.preferences, ModelPreferences.class , null)[0];
				
			}catch(Exception e){
				prefs = new ModelPreferences(0, 1,0,1,0,0,0,0,0,0,0,0,7,1,1);
			}
			logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +  ":\nStarting Reverse Scan");
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					"SharpFix Reverse Directory Scan - Starting Reverse Scan Algorithm to Folders");
			publishProgress(new String[] {"SharpFix Reverse Directory Scan", "Starting Reverse Scan Algorithm to Folders"});
			
			/*
			 * reverse scan is the scan that looks up on the database and checks if the 
			 * database values actually exists, if not then it is deleted. If however the 
			 * said value was modified, the reverse scan updates the values in the database
			 */
			
			// directories reverse scan
			logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\nStarting Reverse Directory Scan");
			for(Object o : dirs){
				File f = new File(((ModelDirsInfo) o).getPath());
				publishProgress(new String[] {"", f.getAbsolutePath()});
				if( f.isDirectory() && f.canRead() && f.canWrite() && /*!f.isHidden() && */ f.exists()){
					// the recorded data is a dir, readable, writeable, not hidden and exists
					
					// check if the current file instance has greater lastMod than the recorded data on the database (recency check)
					if(Long.valueOf(f.lastModified()).compareTo(((ModelDirsInfo) o).getLast_mod()) > 0){
						// file was modified
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
								":\nFolder " +f.getAbsolutePath() + " still exists in the file system!\n" + 
								"Checking if it has been modified since the last scan . . .\n" + 
								"\nFolder " +f.getAbsolutePath() + " has been detected to be modified!");
						int sdCard = 0;
						boolean notFound = true;
						File parent = f.getParentFile();
						while(notFound){
							ModelSD sdTemp = (ModelSD) db.select(Tables.sd_info, ModelSD.class, 
									new Object[][] { {"path", parent.getAbsolutePath()} }, null);
							if( sdTemp.getId() != null){
								sdCard = sdTemp.getId();
								notFound = false;
							}else{
								parent = parent.getParentFile();
							}
						}
						// add to directories to be scanned later by duplication detection and file designation
						SF.dirsQueue.add(new ModelDirsInfo(sdCard, f.getAbsolutePath(), f.lastModified()));
						
					}else{
						// file was unmodified
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
								":\nFolder " +f.getAbsolutePath() + " still exists in the file system!\n" + 
								"Checking if it has been modified since the last scan . . .\n" + 
								"\nFolder " +f.getAbsolutePath() + " has been detected to be unmodified!");
					}
					
				}else{
					// invalid file or unreadable, delete this record on the databse
					logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							":\nFolder " +f.getAbsolutePath() + 
							" has been detected to be unreadable!It either means the folder is unreadable or does not exist anymore."+
							"\nDeleting from database records . . .");
					  db.delete(Tables.dirs_info, new ModelDirsInfo(((ModelDirsInfo) o).getSd_id(), ((ModelDirsInfo) o).getPath(),  
							((ModelDirsInfo) o).getLast_mod()), null);
					
				}
			}
			
			logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\nStarting Reverse File Scan");
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					"SharpFix Reverse File Scan - Starting Reverse Scan Algorithm to Files");
			publishProgress(new String[] {"SharpFix Reverse File Scan", "Starting Reverse Scan Algorithm to Files"});
			
			// files reverse scan
			for(Object o : files){
				File f = new File(((ModelFilesInfo) o).getPath());
				publishProgress(new String[] {"", f.getAbsolutePath()});
				if( !f.isDirectory() && f.canRead() && f.canWrite() && !f.isHidden() && f.exists()){
		
					// the recorded data is a dir, readable, writeable, not hidden and exists
					if(Long.valueOf(f.lastModified()).compareTo(((ModelFilesInfo) o).getLast_mod()) > 0){
						// file was modified
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
								":\nFile " +f.getAbsolutePath() + " still exists in the file system!\n" + 
								"Checking if it has been modified since the last scan . . .\n" + 
								"\nFile " +f.getAbsolutePath() + " has been detected to be modified!");
						
						SF.filesQueue.add(new ModelFilesInfo(f.getAbsolutePath(),f.getParent()));
					}else{
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
								":\nFile " +f.getAbsolutePath() + " still exists in the file system!\n" + 
								"Checking if it has been modified since the last scan . . .\n" + 
								"\nFile " +f.getAbsolutePath() + " has been detected to be unmodified!");
					}
				}else{
		
					// invalid file or unreadable, delete this record on the databse
					logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							":\nFile " +f.getAbsolutePath() + 
							" has been detected to be unreadable!It either means the folder is unreadable or does not exist anymore."+
							"\nDeleting from database records . . .");
					db.delete(Tables.files_info, new ModelFilesInfo(((ModelFilesInfo) o).getPath(), ((ModelFilesInfo) o).getDir(),
							((ModelFilesInfo) o).getLast_mod(), ((ModelFilesInfo) o).getCrc32(), ((ModelFilesInfo) o).getMd5(),
							((ModelFilesInfo) o).getSha1(), ((ModelFilesInfo) o).getSize()), null);
				}
			}
			logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\nStarting Recursive Scan");
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					"SharpFix Recursive Scan - Starting Recursive Scan Algorithm to Folders and Files");
		
			publishProgress(new String[] {"SharpFix Recursive Scan", "Starting Recursive Scan Algorithm to Folders and Files"});
			
			// recursive scan
			for(Object f : sd){
				publishProgress(new String[] {"",((ModelSD)f).getPath()});
				checkDir(new File( ((ModelSD)f).getPath() ), ((ModelSD)f).getId() );
			}
			long end = System.currentTimeMillis(); 
			long runTime = end - start;
			
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					"SharpFix Directory Scanner finished scanning!\n\nFiles scanned :" + fileCount + "\nFolders scanned: " + dirCount + 
					"\nModified files since last scan: " + SF.filesQueue.size() +
					"\nModofied folders since last scan: " + SF.dirsQueue.size() +
					"\n\nTime elapsed: " + String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
							TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
							TimeUnit.MILLISECONDS.toSeconds(runTime) - 
							TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))));
			
			publishProgress(new String[] {"SD-Card Scan Finished", "Time elapsed:" + 
					String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
							TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
							TimeUnit.MILLISECONDS.toSeconds(runTime) - 
							TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))),
							"" });
			
			logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\nDirectory Scanner finished successfully!\n\n" + 
					 "Scanned folders: " +dirCount + "\nScanned files: " +fileCount + 
					 "\nTime elapsed: " + String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
								TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
								TimeUnit.MILLISECONDS.toSeconds(runTime) - 
								TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))));
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
					// ######################## ALPHA RELEASE 1.1.3 ########################
					/*
					 * Fully activated Notifications control on all services
					 * Also, notifications automatically closes once the scan has been finished
					 */
					mNotifyManager.cancel(1);
					// ######################## ALPHA RELEASE 1.1.3 ########################
					
				}
				DirectoryScanner.this.wakeLock.release();
				
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
					
				}else{
					if( prefs.getFdd_switch() != 1){
						
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFile Duplication Detection is turned off!");
		
						// check if fd switch is turned on
						// this will call fd scanner when fd is turned on even if fdd did not run 
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nChecking for File Designation switch since File Duplication Detection switch is turned off . . .");
						if(prefs.getFd_switch() == 1){
							// file designation is turned on
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\nFile Designation is turned on!");
							
							Intent i = new Intent(DirectoryScanner.this, FileDesignationScanner.class);
							DirectoryScanner.this.startService(i);
							
						}else{
							
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\nFile Designation is turned off!");
							AndroidUtils.logProgressReport(DirectoryScanner.this, logs.toArray(new String[logs.size()]));
							AndroidUtils.logScanReport(DirectoryScanner.this, SF.logsQueue.toArray(new String[ SF.logsQueue.size()]));
						}
					}else{
						
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
								":\nFile Duplication Detection is turned on! Checking for file and directory modifications . . .");
						if(!(SF.dirsQueue.size() > 0)){
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
									":\nIt seems that no directory under Sharpfix's scope was modified since the last scan\n"+
									"Checking report consistency . . .");
							
							if(SF.filesQueue.size() > 0){
								logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										":\nWARNING! It looks like the scan report was inconsistent because the system detected "+
										SF.filesQueue.size() + (SF.filesQueue.size() > 1 ? " files" : " file") + " has " +
										"been modified since the last scan!");
							}else{
								logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										":\nThere are no conflicts in both directory and file scan reports!");
							}
						}
						
					}
					
					
				}
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
			
			logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
					":\nException on Directory Scanner!!!");
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					"Directory Scanner's execution might become unstable and produce unreliable results due to an uncaught exception!\n\nException details:");
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" + 
						errors.toString());
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					errors.toString());	
			AndroidUtils.logProgressReport(DirectoryScanner.this, logs.toArray(new String[logs.size()]));
			AndroidUtils.logScanReport(DirectoryScanner.this, SF.logsQueue.toArray(new String[ SF.logsQueue.size()]));
			
		}
		
		@Override
		protected void onCancelled(){
			SF.logsQueue.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ":\n" +
					"Directory Scanner's execution has been manually cancelled!");
			logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
					":\nDirectory Scanner's execution has been manually cancelled!");
			mBuilder.setProgress(0, 0, true);
			mBuilder.setContentText("Directory Scanner has been cancelled.");
			mNotifyManager.notify(1, mBuilder.build());
			AndroidUtils.logProgressReport(DirectoryScanner.this, logs.toArray(new String[logs.size()]));
			AndroidUtils.logScanReport(DirectoryScanner.this, SF.logsQueue.toArray(new String[ SF.logsQueue.size()]));
		}
		
		
		/**
		 * A recursive algorithm that scans all the files and folders inside
		 * the given directory and sdcard index. Some devices has 2 sdcard
		 * given in the format: /mnt/storge/sdcard0 and /mnt/storge/sdcard1.
		 * 
		 * In these instances, the scanner automatically creates an index
		 * in order to identify the difference between the two. After creating
		 * an index, this index will be referred by future scans and is needed
		 * in order to separate the scan activities from sdcard0 to sdcard1
		 * 
		 * @param f - a {@link File} instance 
		 * @param sdCard - SD-Card index in database records
		 * @throws InterruptedException
		 * @throws Exception
		 */
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
							
							// check if directory last mod was changed
							if(Long.valueOf(ff.lastModified()).compareTo(mdi.getLast_mod()) > 0){
								logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										":\nFolder "+ mdi.getPath() + " already exists in the database!\n" + 
										"Checking if directory has been modified since the last scan . . .\n" +
										 mdi.getPath() + " has been detected to be modified!\n"+
										"Traversing this directory and adding to folders queue . . .");
								// dir was modified
								
								SF.dirsQueue.add(mdi);
							}else{
								logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										":\nFolder "+ mdi.getPath() + " already exists in the database!\n" + 
										"Checking if directory has been modified since the last scan . . .\n" +
										 mdi.getPath() + " has been detected to be unmodified!\n"+
										"Traversing this folder and checking sub folders . . .");
							}
							// traverse the current directory, even if modified or not!
							/* DEVELOPER NOTE:
							 * 	
							 *  A directory's last_modified attribute will only change when a file
							 *  residing directly inside it, was explicitly modified. This means that 
							 *  when a file inside a sub directory, which is inside this directory 
							 *  has been modified, it will not change the uppermost directories.
							 *  
							 *  Example:
							 *  /mnt/sdcard/Android/Test/ has a last_mod attribute of Nov. 12, 2014
							 *  
							 *  /mnt/sdcard/Android/Test/A1/ has a last_mod attribute of Nov 20, 2014
							 *  
							 *  whenever a file inside /mnt/sdcard/Android/Test/A1/ is modified, 
							 *  the directory A1's last_mod attribute will change, but not the directory
							 *  Test's last_mod attribute. 
							 *  
							 *  With the given circumstances, github commit 8.1.0
							 *  (https://github.com/idclxvii/SharpFixAndroid/commit/2badff5e0fe9f09a934ef77fdc87070e42c5f1ae#diff-14)
							 *  produced a bug that sub directories are not being scanned because Sharpfix detects
							 *  that the parent directory was not modified, even though the inner most sub directories has been
							 *  modified
							 *  
							 *  I do not know yet if this is a bug in the Android File System, but the only 
							 *  solution for now is to revert back to the previous algorithm. Traverse all directories,
							 *  which may again, lead to slower SD-card scanning.
							 *  
							 *  
							 * */
							checkDir(ff, mdi.getSd_id());
						}else{
							
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
									":\nFolder "+ mdi.getPath() + " does not exist in the database!\n" + 
									"Traversing this folder, adding to folders queue and checking sub folders . . .");
							// add this dir to dirQueue
							SF.dirsQueue.add(new ModelDirsInfo(sdCard, ff.getAbsolutePath(), ff.lastModified()));
							// disable adding to database because scans can be cancelled
							// add it later to database when the current item was successfully scanned
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
									}
								, null);
						
						// Thread.sleep(100);
						if(mfi.getPath() != null){
							
							if(Long.valueOf(ff.lastModified()).compareTo(mfi.getLast_mod()) > 0){
								// file was modified
								logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										":\nFile " + mfi.getPath() + " already exists in the database!\n"+
										"Checking if file has been modified since the last scan . . .\n"+ 
										"File " + mfi.getPath() + " has been detected to be modified!\n"+
										"Adding to files queue . . .");
								SF.filesQueue.add(mfi);
								
							}else{
								logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										":\nFile " + mfi.getPath() + " already exists in the database!\n"+
										"Checking if file has been modified since the last scan . . .\n"+ 
										"File " + mfi.getPath() + " has been detected to be unmodified!\n");
							}
							
							
						}else{
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
									":\nFile " + ff.getPath() + " does not exist in the database!\n"+
									"Adding to files queue . . .");
							
							// add this file to fileQueue
							SF.filesQueue.add(new ModelFilesInfo(ff.getAbsolutePath(),ff.getParent()));
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
								}
							, null);
					Thread.sleep(10);
					if(mfi.getPath() != null){
						
						if(Long.valueOf(f.lastModified()).compareTo(mfi.getLast_mod()) > 0){
							// file was modified
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
									":\nFile " + mfi.getPath() + " already exists in the database!\n"+
									"Checking if file has been modified since the last scan . . .\n"+ 
									"File " + mfi.getPath() + " has been detected to be modified!\n"+
									"Adding to files queue . . .");
							SF.	filesQueue.add(mfi);
							
						}
						
					}else{
						
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								":\nFile " + f.getPath() + " does not exist in the database!\n"+
								"Adding to files queue . . .");
						// add this file to fileQueue
						SF.filesQueue.add(new ModelFilesInfo(f.getAbsolutePath(),f.getParent()));
					}
					Thread.sleep(10);
					fileCount++;
				}
			}
		}
	}
	
	
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		logs =  new ArrayList<String>();
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
		TASKHANDLER.cancel(true);
		logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + 
				":\nDestroying Directory Scanner");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
				":\nCreating Directory Scanner");
		Intent notificationIntent = new Intent(this, SubMenuDirectScanControls.class);
		notificationIntent.putExtra("notification", true);
		
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
		
		
		return START_STICKY;
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
