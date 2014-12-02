package tk.idclxvii.sharpfixandroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import tk.idclxvii.sharpfixandroid.databasemodel.*;
import tk.idclxvii.sharpfixandroid.utils.AndroidUtils;
import tk.idclxvii.sharpfixandroid.utils.FileProperties;
import tk.idclxvii.sharpfixandroid.utils.Logcat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class FileDuplicationDetectionScanner extends Service{

	public FileDuplicationDetectionScanner(){
		
	}
	
	
	
	private SQLiteHelper db;
	private Context c;
	
	private PowerManager powerManager;
	private WakeLock wakeLock;
	private Task TASKHANDLER = new Task();
	private NotificationManager mNotifyManager;
	private NotificationCompat.Builder mBuilder;
	
	private final String TAG = this.getClass().getName();
	private SharpFixApplicationClass SF;
	
	private long deletedFileSize;
	/*
	private Object[] tempFilesQueue;
	private Object[] tempDirsQueue;
	private List<Object> filesQueue;
	private List<Object> dirsQueue;
	 */
	
	
	private ModelPreferences prefs; 
	
	
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	
	private class Task extends GlobalAsyncTask<File, String, Void>{

		@Override
		protected Void doTask(File... params) throws Exception {
			// TODO Auto-generated method stub
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Starting File Duplication Detection Scanner");
			/*
			Log.i(TAG, "########################################");
			Log.i(TAG, "Starting File Duplication Detection Scanner");
			*/
			long start = System.currentTimeMillis();
			publishProgress(new String[] {"File Duplication Detection Scan", "Initializing Scan: " + (FileProperties.formatFileLastMod(start))});
			
			try{
				prefs = (ModelPreferences)db.selectAll(Tables.preferences, ModelPreferences.class , null)[0];
				
			}catch(Exception e){
				prefs = new ModelPreferences(0, 1,0,0,0,0,0,0,0,0,0,0,0,0,0);
			}
			/*
			 * File Duplication Detection Scan Algorithm
			 * @author IDcLxViI
			 * 11/3/2014
			 * 
			
			
			FDDS Algorithm:
				* check fdd switch
				* check filter switch
					* filter:
						check for filtered dirs and sub dirs then remove it from dirsQueue
						check for files inside filtered dirs and sub dirs then remove it from filesQueue
						check for files inside filtered files then remove it from filesQueue
					
					* fdd scan
						* scan the given file 
						* update the file information (try insert first, then update if it fails)
						* remove the file in the filesQueue
						* for each element in dirsQueue, check if ALL the files in filesQueue containing
						 	the current element's path has been processed. if it was, then update the element's dir information
						 	and then remove it from dirsQUeue (try db insert first, then update if it fails)
						 	
						 	
			*/
			publishProgress(new String[] {"", "Checking FDD Filter Switch" });
			
			if(prefs.getFdd_switch() == 1){
				// fdd scan is turned on
				DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
						": File Duplication Detection is turned on!");
				/*
				Log.i(TAG, "########################################");
				Log.i(TAG, "File Duplication Detection is turned on!");
				*/
				if(prefs.getFdd_Filter_switch() == 1){
					// fdd filter is turned on, filter the filesQueue
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							": FDD Filtering is turned on!");
					/*
					Log.i(TAG, "########################################");
					Log.i(TAG, "FDD Filtering is turned on!");
					*/
					publishProgress(new String[] {"", "Filtering scanned files and directories"});
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							": Filtering queued Files and Directories");
					/*
					Log.i(TAG, "########################################");
					Log.i(TAG, "Filtering queued Files and Directories");
					*/
					filter();
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							": Initializing FDD Scan!");
					/*
					Log.i(TAG, "########################################");
					Log.i(TAG, "Initializing FDD Scan!");
					*/
					publishProgress(new String[] {"", "Starting scan" });
					scan();
				}else{
					// fdd filter is turned off, scan w/o filtering
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							": FDD Filtering is turned off!");
					/*
					Log.i(TAG, "########################################");
					Log.i(TAG, "FDD Filtering is turned off!");
					*/
					publishProgress(new String[] {"", "Starting scan" });
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							": Initializing FDD Scan!");
					scan();
				}
			}else{
				// do nothing, fdd scan is turned off
				DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
						": File Duplication Detection is turned off!");
				/*
				Log.i(TAG, "########################################");
				Log.i(TAG, "File Duplication Detection is turned off!");
				*/
			}
			
			/*
			for(Object o : filesQueue){
				checkPreferences( new File( ((ModelFilesInfo)o).getPath() ) );
			}
			*/
			long end = System.currentTimeMillis();
			long runTime = end - start;
			
			publishProgress(new String[] {"File Duplication Detection Scan Finished", "Time elapsed: " + 
					String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
							TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
							TimeUnit.MILLISECONDS.toSeconds(runTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))),
							"" });
			//checkPreferences(params[0]);
			
			
			
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
					": File Duplication Detection Scanner Finished!");
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
					": Time elapsed: " + 
					String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(runTime),
							TimeUnit.MILLISECONDS.toMinutes(runTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runTime)),
							TimeUnit.MILLISECONDS.toSeconds(runTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))));
			/*
			Log.i(TAG, "########################################");
			Log.i(TAG, "File Duplication Detection Scanner Finished!");
			*/
			
			return null;
		}

		@Override
		protected void onException(Exception e) {
			// TODO Auto-generated method stub
			
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
					": Exception on File Duplication Detection Scanner!!!");
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + 
						errors.toString());
			AndroidUtils.logProgressReport(FileDuplicationDetectionScanner.this, DirectoryScanner.logs.toArray(new String[DirectoryScanner.logs.size()]));
			/*
			Log.i(TAG, "########################################");
			Log.i(TAG, "Exception on File Duplication Detection Scanner!!!");
			
			Logcat.logCaughtException("FileDuplicationDetectionScanner", e.getStackTrace());
			*/
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
				
				if(prefs.getFd_switch() == 1){
					// file designation is turned on
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							": FDD Scan finished, Ready to initiate File Designation!");
					/*
					Log.w(TAG, "########################################");
					Log.w(TAG, "FDD Scan finished, Ready to initiate File Designation!");
					*/
					Intent i = new Intent(FileDuplicationDetectionScanner.this, FileDesignationScanner.class);
					FileDuplicationDetectionScanner.this.startService(i);
					/*	
						Log.i(TAG, "########################################");
						Log.i(TAG, "Exception Caught: No Files to Queue!");
						Log.i(TAG, "########################################");
						Logcat.logCaughtException("DirectoryScanner", e.getStackTrace());
					*/
				}else{
					if( prefs.getFd_switch() != 1){
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								": FDD Scan finished. File Designation is turned off. No service are set to run anymore!");
						/*
						Log.w(TAG, "########################################");
						Log.w(TAG, "FDD Scan finished. File Designation is turned off. No service are set to run!");
						*/
						AndroidUtils.logProgressReport(FileDuplicationDetectionScanner.this, DirectoryScanner.logs.toArray(new String[DirectoryScanner.logs.size()]));
						
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
						mNotifyManager.notify(2, mBuilder.build());
					}
				}else{
					// there are no changes in notification title
					if(SF.getServiceNoti() == 1){
						mBuilder.setProgress(0, 0, true);
						mBuilder.setContentText(params[1]);
						mNotifyManager.notify(2, mBuilder.build());
					}
					
				}
			}
			
		}
		
		@Override
		protected void onPostExecute(Void returnedResult){
			
		}
		
		
		
		private void filter() throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException{
			
			
			Object[] mdf = db.selectMulti(Tables.dir_filter, ModelDirFilter.class,
					new Object[][] { {"filter", "fdd"}} ,null);
			
			
			Object[] mff = db.selectMulti(Tables.file_filter, ModelFileFilter.class,
					new Object[][] { {"filter", "fdd"}} ,null);
			
			
					//db.selectAll(Tables.dir_filter, ModelDirFilter.class, null);
			
			// dirs filter
			for(Object filter : mdf){
				// filter dirsQuue (dirs & subdirs instances)
				//for(Object dir : dirsQueue){
				for(Iterator<Object> it = SF.dirsQueue.iterator(); it.hasNext();){
					Object dir = it.next();
					if( ((ModelDirsInfo)dir).getPath().contains( ((ModelDirFilter)filter).getDir())){
						it.remove();
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								": Directory and Subdirectory Filter Detection\n" + 
								((ModelDirsInfo)dir).getPath() + " was detected to be a sub directory or the directory itself of "
								+ ((ModelDirFilter)filter).getDir() + ", which is being filtered!\n" +
								 "Removing " + ((ModelDirsInfo)dir).getPath() + " from dirsQueue");
						/*
						Log.i(TAG, "Directory and Subdirectory Filter Detection");
						Log.i(TAG, ((ModelDirsInfo)dir).getPath() + " was detected to be a sub directory or the directory itself of "
								+ ((ModelDirFilter)filter).getDir() + ", which is being filtered!");
						Log.i(TAG, "Removing " + ((ModelDirsInfo)dir).getPath() + " from dirsQueue");
						*/
					}
				}
				
				// filter filesQueue (sub dir instances)
				//for(Object file : filesQueue){
				for(Iterator<Object> it = SF.filesQueue.iterator(); it.hasNext();){
					Object file = it.next();
					if( ((ModelFilesInfo)file).getPath().contains( ((ModelDirFilter)filter).getDir())){
						it.remove();
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								": File Filter Detection\n" + 
								((ModelFilesInfo)file).getPath() + " was detected to be a inside the directory of "
								+ ((ModelDirFilter)filter).getDir() + ", which is being filtered!\n" +
								 "Removing " +  ((ModelFilesInfo)file).getPath() + " from filesQueue");
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
				for(Iterator<Object> it = SF.filesQueue.iterator(); it.hasNext();){
					Object file = it.next();
					if( ((ModelFilesInfo)file).getPath().equals( ((ModelFileFilter)filter).getFile())){
						it.remove();
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								": File Filter Detection\n" + 
								((ModelFilesInfo)file).getPath() + " was detected to be a inside the directory of "
								+ ((ModelDirFilter)filter).getDir() + ", which is being filtered!\n" +
								 "Removing " +  ((ModelFilesInfo)file).getPath() + " from filesQueue");
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
		
	
		private void scan() throws IllegalArgumentException, InstantiationException,
		IllegalAccessException, NoSuchMethodException, InvocationTargetException, FileNotFoundException, 
		IOException, NoSuchAlgorithmException{
			//android.os.Debug.waitForDebugger();
			//for(Object queuedFile : filesQueue){
			for(Iterator<Object> it = SF.filesQueue.iterator(); it.hasNext(); ){
				Object queuedFile = it.next();
				File ff = new File(((ModelFilesInfo)queuedFile).getPath());
				publishProgress(new String[] {"", ff.getAbsolutePath()});
				try{
					
					
					Object[] result = db.selectConditional(Tables.files_info, ModelFilesInfo.class, 
						new Object[][] {
								{"crc32", Security.getCRC32Checksum(((ModelFilesInfo)queuedFile).getPath()), "AND NOT "},
								{"path",  ((ModelFilesInfo)queuedFile).getPath() }
							
						}
							
					/*
							("crc32 = '" + Security.getCRC32Checksum(((ModelFilesInfo)queuedFile).getPath()) + "' AND " +
							"NOT path = '" + ((ModelFilesInfo)queuedFile).getPath()+ "'" )
							*/
							
					, null);
					if(result.length > 0){
						// the current file in queue has duplicate!
						if(result.length > 1){
							// there are 2 or more occurrence duplicate files (3 or more duplicate files)
							DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
									": 2 or more files detected as duplicates of " + ((ModelFilesInfo)queuedFile).getPath());
							/*
							Log.i(TAG, "########################################");
							Log.i(TAG, "2 or more files detected as duplicates of " + ((ModelFilesInfo)queuedFile).getPath());
							Log.i(TAG, "########################################");
							*/
							
							
							
						}else{
							// there's only 1 occurrence of duplicate file (2 duplicate files)
							
							DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
									": 1 file is detected as duplicate of " + ((ModelFilesInfo)queuedFile).getPath());
							/*
							Log.i(TAG, "########################################");
							Log.i(TAG, "1 file is detected as duplicate of " + ((ModelFilesInfo)queuedFile).getPath());
							Log.i(TAG, "########################################");
							*/
							/*
							for(Object o : result){
								Log.i(TAG, ((ModelFilesInfo)o).getPath() + " " +((ModelFilesInfo)o).getCrc32()  );
							}
							*/
						}
						for(Object storedFile : result){
							Log.i(TAG, ((ModelFilesInfo)storedFile).getPath() + " " + ((ModelFilesInfo)storedFile).getCrc32()  );
							if(prefs.getFdd_pref() == 0){
								// user prefers to delete the older file
								if( Long.valueOf((new File(((ModelFilesInfo)queuedFile).getPath())).lastModified()).compareTo(
										((ModelFilesInfo)storedFile).getLast_mod()) > 0 ){
									if((new File(((ModelFilesInfo)storedFile).getPath())).delete()){
										// file successfully deleted
										DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
												": Deleting actual file " + ((ModelFilesInfo)storedFile).getPath() + " successful!");
										/*
										Log.i(TAG, "########################################");
										Log.i(TAG, "Deleting actual file " + ((ModelFilesInfo)storedFile).getPath() + " successful!" );
										Log.i(TAG, "########################################");
										*/
									}else{
										// file was not successfully deleted
										DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
												": Deleting actual file " + ((ModelFilesInfo)storedFile).getPath() + "  was not successful!");
										/*
										Log.e(TAG, "########################################");
										Log.e(TAG, "Deleting actual file " + ((ModelFilesInfo)storedFile).getPath() + " was not successful!" );
										Log.e(TAG, "########################################");
										*/
									}
									
									db.delete(Tables.files_info, storedFile , null);
										
									if(db.insert(Tables.files_info,
											new ModelFilesInfo(ff.getAbsolutePath(), ff.getParent(), ff.lastModified(), 
													Security.getCRC32Checksum(ff.getAbsolutePath()), 
													Security.getMD5Checksum(ff.getAbsolutePath()), 
													Security.getSHA1Checksum(ff.getAbsolutePath()),
													Long.toString(ff.length())),
										null)){
										DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
												": Inserting file" + ff.getAbsolutePath() + " to database recrods successful!");
										/*
										Log.i(TAG, "########################################");
										Log.i(TAG, "Inserting file" + ff.getAbsolutePath() + " successful!" );
										Log.i(TAG, "########################################");
										*/
									}else{
										if(db.update(Tables.files_info,
												db.selectMulti(Tables.files_info, ModelFilesInfo.class,
														new Object[][] { {"path" , ff.getAbsolutePath()}, {"dir", ff.getParent()}}, null)[0]
												
												, 
												new ModelFilesInfo(ff.getAbsolutePath(), ff.getParent(), ff.lastModified(), 
														Security.getCRC32Checksum(ff.getAbsolutePath()), 
														Security.getMD5Checksum(ff.getAbsolutePath()), 
														Security.getSHA1Checksum(ff.getAbsolutePath()),
														Long.toString(ff.length()))
											, null)){
											
											DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
													": Inserting file" + ff.getAbsolutePath() + " to database recrods was not successful! "+
													"Trying udpate . . .");
											/*
											Log.i(TAG, "########################################");
											Log.i(TAG, "Inserting file" + ff.getAbsolutePath()  + " not successful!" );
											Log.i(TAG, "Trying update . . ." );
											Log.i(TAG, "########################################");
											*/
										}else{
											DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
													": Updating file" + ff.getAbsolutePath()  + " to database records was not successful!");
											/*
											Log.e(TAG, "########################################");
											Log.e(TAG, "Updating file" + ff.getAbsolutePath()  + " not successful!" );
											Log.e(TAG, "########################################");
											*/
										}
									}
								}else{
									if((new File(((ModelFilesInfo)queuedFile).getPath())).delete()){
										// file successfully deleted
										DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
												": Deleting actual file " + ((ModelFilesInfo)queuedFile).getPath() + " successful!");
										/*
										Log.i(TAG, "########################################");
										Log.i(TAG, "Deleting actual file " + ((ModelFilesInfo)queuedFile).getPath() + " successful!" );
										Log.i(TAG, "########################################");
										*/
									}else{
										// file was not successfully deleted
										DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
												": Deleting actual file " + ((ModelFilesInfo)queuedFile).getPath() + " was not successful!");
										/*
										Log.e(TAG, "########################################");
										Log.e(TAG, "Deleting actual file " + ((ModelFilesInfo)queuedFile).getPath() + " was not successful!" );
										Log.e(TAG, "########################################");
										*/
										
									}
									
									db.delete(Tables.files_info, queuedFile , null);
									
								}
								
								
								
								
								
							}else{
								// user prefers to delete the new file
								if((new File(((ModelFilesInfo)queuedFile).getPath())).delete()){
									// file successfully deleted
									DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
											": Deleting actual file " + ((ModelFilesInfo)queuedFile).getPath() + " successful!");
									/*
									Log.i(TAG, "########################################");
									Log.i(TAG, "Deleting actual file " + ((ModelFilesInfo)storedFile).getPath() + " successful!" );
									Log.i(TAG, "########################################");
									*/
								}else{
									// file was not successfully deleted
									DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
											": Deleting actual file " + ((ModelFilesInfo)queuedFile).getPath() + " was not successful!");
									/*
									Log.e(TAG, "########################################");
									Log.e(TAG, "Deleting actual file " + ((ModelFilesInfo)storedFile).getPath() + " was not successful!" );
									Log.e(TAG, "########################################");
									*/
								}
								db.delete(Tables.files_info, queuedFile , null);
								
							}
						}
						
						
						
					}else{
					
					// file has no duplicate
					// update the file information
					
					// try insert first
					
						
						if(db.insert(Tables.files_info, new ModelFilesInfo(ff.getAbsolutePath(), ff.getParent(), ff.lastModified(), 
								Security.getCRC32Checksum(ff.getAbsolutePath()), Security.getMD5Checksum(ff.getAbsolutePath()),
								Security.getSHA1Checksum(ff.getAbsolutePath()), Long.toString(ff.length())), null)){
							
							DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
									": Inserting file" + ff.getAbsolutePath() + " to database records successful!");
							/*
							Log.i(TAG, "########################################");
							Log.i(TAG, "Inserting file" + ff.getAbsolutePath() + " successful!" );
							Log.i(TAG, "########################################");
							*/
						}else{
							// update
							DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
									": Inserting file" + ff.getAbsolutePath()  + " to database records was not successful! " +
									"Trying update . . .");
							/*
							Log.i(TAG, "########################################");
							Log.i(TAG, "Inserting file" + ff.getAbsolutePath()  + " not successful!" );
							Log.i(TAG, "Trying update . . ." );
							Log.i(TAG, "########################################");
							*/
							if(db.update(Tables.files_info, 
									db.selectMulti(Tables.files_info, ModelFilesInfo.class,
											new Object[][] { {"path" , ff.getAbsolutePath()}, {"dir", ff.getParent()}}, null)[0]
									, 
									new ModelFilesInfo(ff.getAbsolutePath(), ff.getParent(), ff.lastModified(), 
											Security.getCRC32Checksum(ff.getAbsolutePath()), 
											Security.getMD5Checksum(ff.getAbsolutePath()), 
											Security.getSHA1Checksum(ff.getAbsolutePath()),
											Long.toString(ff.length()))
								, null)){
								DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										": Updating file" + ff.getAbsolutePath()  + " to database records was successful!");
								/*
								Log.i(TAG, "########################################");
								Log.i(TAG, "Updating file" + ff.getAbsolutePath() + " successful!" );
								
								*/
							}else{
								DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
										": Updating file" + ff.getAbsolutePath()  + " to database records was not successful!");
								/*
								Log.e(TAG, "########################################");
								Log.e(TAG, "Updating file" + ff.getAbsolutePath()  + " not successful!" );
								Log.e(TAG, "########################################");
								*/
							}
						}
						
						/*
						try {
							db.insert(Tables.files_info, new ModelFilesInfo(ff.getAbsolutePath(), ff.getParent(), ff.lastModified(), 
								Security.getCRC32Checksum(ff.getAbsolutePath()), Security.getMD5Checksum(ff.getAbsolutePath()),
								Security.getSHA1Checksum(ff.getAbsolutePath()), Long.toString(ff.length())), null);
						} catch (SQLiteConstraintException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						*/
					}
				}catch(Exception e){
					/* when this code is run, it means that this record is previously existing in the database
					 * but the actual file has been deleted due to duplication detection from this instance of 
					 * fdd, and therefore, crc32 throws an Exception because the file cannot be found.
					 * 
					 * Current solution: delete this record in the database since the actual file is not existing
					 * anymore
					 * 
					 */
					
					DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
							": An exception has been caught! Please check the file information:\n" + 
							"Bug trigger:" + ((ModelFilesInfo)queuedFile).getPath());
					/*
					Log.e(TAG,"CRC32 BUG EXCEPTION OCCURED! PLEASE CHECK IMMEDIATE DATABASE DELETION RESULT:");
					Log.e(TAG, "Bug trigger:" + ((ModelFilesInfo)queuedFile).getPath() );
					*/
					//android.os.Debug.waitForDebugger();
					
					if(db.delete(Tables.files_info, new ModelFilesInfo(((ModelFilesInfo) queuedFile).getPath(), ((ModelFilesInfo) queuedFile).getDir(),
							((ModelFilesInfo) queuedFile).getLast_mod(), ((ModelFilesInfo) queuedFile).getCrc32(), ((ModelFilesInfo) queuedFile).getMd5(),
							((ModelFilesInfo) queuedFile).getSha1(), ((ModelFilesInfo) queuedFile).getSize()), null) ){
						
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								": File" + ((ModelFilesInfo)queuedFile).getPath() + " was successfully deleted from database records!");
						
						//Log.e(TAG,"File" + ((ModelFilesInfo)queuedFile).getPath() + " was successfully deleted from database records!");
					}else{
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								": File" + ((ModelFilesInfo)queuedFile).getPath() + " was not successfully deleted from database records!");
						// Log.e(TAG,"File" + ((ModelFilesInfo)queuedFile).getPath() + " WAS NOT SUCCESSFULLY DELETED FROM DATABASE RECORDS!");
					}
					
				}
				it.remove();
				
			}
			checkDirsQueue();
		}
		
		
		private void checkDirsQueue() throws SQLiteConstraintException, IllegalArgumentException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException{
			//for(Object dir : dirsQueue){
			for(Iterator<Object> it = SF.dirsQueue.iterator(); it.hasNext();){
				Object dir = it.next();
				boolean dirFinished = true;
				for(Object file : SF.filesQueue){
					if(((ModelFilesInfo)file).getPath().contains( ((ModelDirsInfo)dir).getPath())){
						dirFinished = false;
					}
				}
				if(dirFinished){
					if(db.insert(Tables.dirs_info, (ModelDirsInfo) dir, null)){
						
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								": Inserting directory" + ((ModelDirsInfo) dir).getPath() + " to database records successful!");
						/*
						Log.i(TAG, "########################################");
						Log.i(TAG, "Inserting directory" + ((ModelDirsInfo) dir).getPath() + " successful!" );
						Log.i(TAG, "########################################");
						*/
						
					}else{
						
						DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
								": Inserting directory" + ((ModelDirsInfo) dir).getPath() + " to database records was not successful! " + 
								"Trying update . . ." );
						/*
						Log.i(TAG, "########################################");
						Log.i(TAG, "Inserting directory" + ((ModelDirsInfo) dir).getPath() + " not successful!" );
						Log.i(TAG, "Trying update . . ." );
						Log.i(TAG, "########################################");
						*/
						//(new ModelDirsInfo(((ModelDirsInfo)dir).getSd_id(),((ModelDirsInfo)dir).getPath(), ((ModelDirsInfo)dir).getLast_mod())),
						if(db.update(Tables.dirs_info, 
								db.selectMulti(Tables.dirs_info, ModelDirsInfo.class,
										new Object[][] { {"path" , ((ModelDirsInfo)dir).getPath()}, {"sd_id", ((ModelDirsInfo)dir).getSd_id()}}, null)[0],
								new ModelDirsInfo(((ModelDirsInfo)dir).getSd_id(),((ModelDirsInfo)dir).getPath(), ( new File( ((ModelDirsInfo)dir).getPath()).lastModified())),
								null)){
							DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
									": Updating directory" + ((ModelDirsInfo) dir).getPath() + " to database records successful!");
							/*
							Log.i(TAG, "########################################");
							Log.i(TAG, "Updating directory" + ((ModelDirsInfo) dir).getPath() + " successful!" );
							Log.i(TAG, "########################################");
							*/
						}else{
							DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
									": Updating directory" + ((ModelDirsInfo) dir).getPath() + " to database records was not successful!");
							/*
							Log.e(TAG, "########################################");
							Log.e(TAG, "Updating directory" + ((ModelDirsInfo) dir).getPath() + " not successful!" );
							Log.e(TAG, "########################################");
							*/
							
						}
					}
					
					
					it.remove();
				}
			}
			
			
		}
	
		
	
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
				": Destroying File Duplication Detection Scanner");
		/*
		Log.i(TAG, "########################################");
		Log.i(TAG, "Destroying File Duplication Detection Scanner");
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
		DirectoryScanner.logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +
				": Creating File Duplication Detection Scanner");
		/*
		Log.i(TAG, "########################################");
		Log.i(TAG, "Creating File Duplication Detection Scanner");
		*/
		Intent notificationIntent = new Intent(this, SubMenuDirectScanControls.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent dspendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("SharpFix File Duplication Detection Scanner");
		mBuilder.setContentIntent(dspendingIntent);
		mBuilder.setContentText("Scanning For Duplicate Files");
	    mBuilder.setSmallIcon(R.drawable.fdd_icon);//setSmallIcon(R.drawable.ic_launcher);
		
	   /*
	    tempFilesQueue = (Object[]) intent.getExtras().getSerializable("files");
	    tempDirsQueue = (Object[]) intent.getExtras().getSerializable("dirs");
	   
	    filesQueue = new ArrayList<Object>(Arrays.asList(tempFilesQueue));
	    dirsQueue = new ArrayList<Object>(Arrays.asList(tempDirsQueue));
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
