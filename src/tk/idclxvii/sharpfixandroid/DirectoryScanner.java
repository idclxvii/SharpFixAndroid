package tk.idclxvii.sharpfixandroid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

import tk.idclxvii.sharpfixandroid.databasemodel.*;
import tk.idclxvii.sharpfixandroid.utils.Logcat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class DirectoryScanner extends Service{

	private SQLiteHelper db;
	
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	private class Task extends GlobalAsyncTask<File, String, Void>{

		private int dirCount  =0 , fileCount = 0;
		Object [] sd;
		Object[] dirs;
		Object[] files;
		
		
		
		@Override
		protected Void doTask(File... params) throws Exception {
			// TODO Auto-generated method stub
			Log.i(TAG, "########################################");
			Log.i(TAG, "Starting Directory Scanner");
			publishProgress(new String[] {"Initializing Directory Scanner"});
			sd = db.selectAll(Tables.sd_info, ModelSD.class, null);
			dirs = db.selectAll(Tables.dirs_info, ModelDirsInfo.class, null);
			files = db.selectAll(Tables.files_info, ModelFilesInfo.class, null);
			
			publishProgress(new String[] {"Starting Reverse Scanning"});
			/*
			 * reverse scan is the scan that looks up on the database and checks if the 
			 * database values actually exists, if not then it is deleted. If however the 
			 * said value was modified, the reverse scan updates the values in the database
			 */
			// directories reverse scan
			for(Object o : dirs){
				File f = new File(((ModelDirsInfo) o).getPath());
				publishProgress(new String[] {"Reverse Scanning:\n" + f.getAbsolutePath()});
				if( f.isDirectory() && f.canRead() && f.canWrite() && !f.isHidden() && f.exists()){
					// the recorded data is a dir, readable, writeable, not hidden and exists
					
					// check if the current file instance has greater lastMod than the recorded data on the database (recency check)
					if(Long.valueOf(f.lastModified()).compareTo(((ModelDirsInfo) o).getLast_mod()) > 0){
						// file was modified
						/*
						// start fdd scanner using this file
						new FileDuplicationDetectionScanner(DirectoryScanner.this);
						// start fd scanner using this file
						new FileDesignationScanner(DirectoryScanner.this);
						*/
						Log.i(TAG, "Old directory was modified: " +f.getAbsolutePath());
						
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
						
						// rewrite lastmod, crc32, md5, sha1, pathname to database
						db.update(Tables.dirs_info, ((ModelDirsInfo)o),  new ModelDirsInfo(sdCard, f.getAbsolutePath(), f.lastModified()), null);
					}else{
						// file was unmodified
						
					}
					
				}else{
					// invalid file or unreadable, delete this record on the databse
					db.delete(Tables.dirs_info, new ModelDirsInfo(((ModelDirsInfo) o).getSd_id(), ((ModelDirsInfo) o).getPath(),  
							((ModelDirsInfo) o).getLast_mod()), null);
				}
			}
			
			// files reverse scan
			for(Object o : files){
				File f = new File(((ModelFilesInfo) o).getPath());
				publishProgress(new String[] {"Reverse Scanning:\n" + f.getAbsolutePath()});
				if( !f.isDirectory() && f.canRead() && f.canWrite() && !f.isHidden() && f.exists()){
					// the recorded data is a dir, readable, writeable, not hidden and exists
					if(Long.valueOf(f.lastModified()).compareTo(((ModelFilesInfo) o).getLast_mod()) > 0){
						// file was modified
						// start fdd scanner using this file
						
						// start fd scanner using this file
						
						
					}else{
						
					}
					
				}else{
					// invalid file or unreadable, delete this record on the databse
					
					//public ModelFilesInfo(String path, String dir, Long lastMod, String crc32, String md5, String sha1, String size){
					db.delete(Tables.files_info, new ModelFilesInfo(((ModelFilesInfo) o).getPath(), ((ModelFilesInfo) o).getDir(),
							((ModelFilesInfo) o).getLast_mod(), ((ModelFilesInfo) o).getCrc32(), ((ModelFilesInfo) o).getMd5(),
							((ModelFilesInfo) o).getSha1(), ((ModelFilesInfo) o).getSize()), null);
				}
			}
			
			
			for(Object f : sd){
				publishProgress(new String[] {"Scanning SD-Card: " + ((ModelSD)f).getPath()});
				checkDir(new File( ((ModelSD)f).getPath() ), ((ModelSD)f).getId() );
			}
			publishProgress(new String[] {"Directory Scanner finished successfully", "f"});
			return null;
		}

		@Override
		protected void onProgressUpdate(String... params){
			if(params.length > 1){
				mBuilder.setProgress(0, 0, true);
				mBuilder.setContentText(params[0]);
				mNotifyManager.notify(1, mBuilder.build());
				mNotifyManager.cancel(1);
				//stopForeground(true);
				//stopSelf();
			}else{
				mBuilder.setProgress(0, 0, true);
				mBuilder.setContentText(params[0]);
				mNotifyManager.notify(1, mBuilder.build());
			}
			
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
			if (f.isDirectory() && f.canRead() && f.canWrite() && !f.isHidden() && f.exists()) {
				String s[] = f.list();
				for (int i=0; i < s.length; i++) {
					File ff = new File(parentDir,s[i]);
					if (ff.isDirectory()) {
						publishProgress(new String[] {ff.getAbsolutePath()});
						dirCount++;
						ModelDirsInfo mdi = 
								(ModelDirsInfo) db.select(Tables.dirs_info, ModelDirsInfo.class, new Object[][] {{"sd_id", sdCard}, {"path", ff.getAbsolutePath()}}, null);
						if(mdi.getPath() != null){
							Thread.sleep(100);
							Log.i("DirectoryScanner", mdi.getPath() + " already exists in the database!");
							checkDir(ff, mdi.getSd_id());
						}else{
							Log.i("DirectoryScanner", ff.getPath() + " does not exist in the database, inserting new record . . .");
							db.insert(Tables.dirs_info, new ModelDirsInfo(sdCard, ff.getAbsolutePath(), ff.lastModified()), null);
							Thread.sleep(100);
							checkDir(ff, sdCard);
						}
						
					}else{
						publishProgress(new String[] { ff.getAbsolutePath()});
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
							Log.i("DirectoryScanner", mfi.getPath() + " already exists in the database!");
						}else{
							Log.i("DirectoryScanner", ff.getPath() + " does not exist in the database, inserting new record . . .");
							db.insert(Tables.files_info, new ModelFilesInfo(ff.getAbsolutePath(), ff.getParent(), ff.lastModified(), 
								Security.getCRC32Checksum(ff.getAbsolutePath()), Security.getMD5Checksum(ff.getAbsolutePath()),
								Security.getSHA1Checksum(ff.getAbsolutePath()), Long.toString(ff.length())), null);
						}
						Thread.sleep(100);
						fileCount++;
						
					}
				}
			}else{
				if(f.canRead() && f.canWrite() && !f.isHidden() && f.exists()){
					publishProgress(new String[] {f.getAbsolutePath()});
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
						Log.i("DirectoryScanner", mfi.getPath() + " already exists in the database!");
					}else{
						Log.i("DirectoryScanner", f.getPath() + " does not exist in the database, inserting new record . . .");
						db.insert(Tables.files_info, new ModelFilesInfo(f.getAbsolutePath(), f.getParent(), f.lastModified(), 
							Security.getCRC32Checksum(f.getAbsolutePath()), Security.getMD5Checksum(f.getAbsolutePath()),
							Security.getSHA1Checksum(f.getAbsolutePath()), Long.toString(f.length())), null);
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
	
	@Override
	public void onCreate() {
		super.onCreate();
		db = getDb(this);
		
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
		
		Intent notificationIntent = new Intent(this, SubMenuServicesActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent dspendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		mNotifyManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
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
