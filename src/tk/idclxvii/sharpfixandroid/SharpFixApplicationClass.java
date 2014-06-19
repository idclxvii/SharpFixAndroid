package tk.idclxvii.sharpfixandroid;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.*;

import tk.idclxvii.sharpfixandroid.databasemodel.*;
import tk.idclxvii.sharpfixandroid.utils.AndroidUtils;
import tk.idclxvii.sharpfixandroid.utils.ExecuteAsRootBase;
import tk.idclxvii.sharpfixandroid.utils.Logcat;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

public class SharpFixApplicationClass extends Application{
	
	/*
	 * Developer Fields - Fields that are manually edited by the developer based on mood swings.
	 */
	
	private final boolean LOGCAT = true;
	private final String TAG = this.getClass().getName();
	private final boolean DEVELOPER_MODE = true;
	
	public boolean getDevMode(){
		return this.DEVELOPER_MODE;
	}
	
	/*
	 * Handset Fields - Fields that are based on the phone's data
	 */
	
	
	private File extFileDir;
	private File intFileDir;
	private File dbFileDir;
	private HashMap<String, File> mountedVolumeDirs;
	private HashMap<String, String> mountedVolumeState = new HashMap<String, String>();
	
	public File getDbFileDirFile(){
		return this.dbFileDir;
	}
	
	public String getDbFileDirPath(){
		return this.dbFileDir.toString();
	}

	public File getIntFileDirFile(){
		return this.intFileDir;
	}
	
	public String getIntFileDirPath(){
		return this.intFileDir.toString();
	}
	
	
	public File getExtFileDirFile(){
		return this.extFileDir;
	}
	
	public String getExtFileDirPath(){
		return this.extFileDir.toString();
	}
	
	public HashMap<String,File> getMountedVolumeDirs(){
		
		// mount types: primary, mounted, usbdisk
		
		String state = Environment.getExternalStorageState();
		if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {  // we can read the External Storage...           
		    //Retrieve the primary External Storage:
		    final File primaryExternalStorage = Environment.getExternalStorageDirectory();

		    //Retrieve the External Storages root directory:
		    String externalStorageRootDir = null;
		    if ( (externalStorageRootDir = primaryExternalStorage.getParent()) == null ) {  // no parent...
		    	this.mountedVolumeState.put("primary", (Environment.isExternalStorageRemovable() ? " Removable SD-CARD" : "Internal Memory"));
		    	return (
		    			new HashMap<String, File>() { 
		    					{
		    						put("primary", primaryExternalStorage); 
		    						
		    				}
		    			}
		    	);
		    }
		    else {
		        File externalStorageRoot = new File( externalStorageRootDir );
		        File[] files = externalStorageRoot.listFiles(new FilenameFilter(){

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
		        HashMap <String,File> data = new HashMap< String, File>();
		        if(files.length > 1){
		        	//data.add("MULTIPLE VOLUMES HAS BEEN DETECTED!");
			        //data.add("Enumerating detected volumes . . .");
			    }else{
		        	//data.add("ONLY A SINGLE VOLUME HAS BEEN DETECTED!");
			    }
		        
		        for ( File file : files ) {
		            if ( file.isDirectory() && file.canRead() && file.canWrite() 
		            		&& !file.isHidden() && (files.length > 0) ) {  // it is a real directory (not a USB drive)...
		            	if(file.toString().equals(primaryExternalStorage.toString())){
		            		this.mountedVolumeState.put("primary", (Environment.isExternalStorageRemovable() ? "Removable SD-CARD" : "Internal Memory"));
		            		data.put("primary", file);
		            				/*
		            				(Environment.isExternalStorageRemovable() ? "(REMOVABLE SD-CARD)" : "(INTERNAL Memory)")
					    			+ " PRIMARY STORAGE: " + file.getAbsolutePath()
					    			*/
					    		
		            	}else{
		            		this.mountedVolumeState.put("mounted",  ((file.toString().contains("usb") || 
		            				file.toString().contains("USB")) ? "Removable USB Disk" : "Internal Memory"));
		            		data.put("mounted", file);
		            		
		            	}
		            }
		        }
		        return data;
		    }
		}else{
			// we cannot read the External Storage..., return null
			return null;
		}
		
	}
	
	
	
	
	private Boolean rootAccess; // this is just to know whether the device is rooted or not, do not base on this when calling root commands
	private Boolean root; // current permission given to a root command, this is the dynamic variable to watch when calling root commands
	
	
	/*
	 * SQLite-based Fields - Fields that are saved and retrieved in the SQLite Database 
	 */
	
	private Integer accountId;
	private Integer fddSwitch;
	private Integer fdSwitch;
	//private Integer filterSwitch;
	private Integer fddPref; // preference if older or newer file will be deleted in case of file duplication : 1 = newer, 0  = older
	private Integer autoLogin  = 0;
	private Integer fdFilterSwitch;
	private Integer fddFilterSwitch;
	
	
	/*
	 * 
	 * Id, Account, Fdd_switch, Fd_switch,  Fdd_pref, Auto_login, Fdd_Filter_switch, Fd_Filter_switch,
		ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
	*/
	
	private Integer serviceSwitch; // 1 = on, 0 = off
	private Integer serviceHour; // 0-24 from hours
	private Integer serviceMin; // 0 - 59 from min
	private Integer serviceAMPM; // 1 = AM, 0 = PM
	private Integer serviceUpdateSwitch;  // 1 = on, 0 = off
	private Integer serviceRepeat; // 0 - 7, Monday = 0, Tue = 1 and so on ... 7 = everyday
	private Integer serviceNoti; // 1 = on, 0 = off
	private Integer auSwitch;  // 1 = on, 0 = off
	
	public void bootMessage(){
		if(LOGCAT){
			Log.d(TAG, this.getClass().getName() + " onCreate()");
			Log.i(TAG, "########################################");
			Log.i(TAG, "SharpFix Android File Management Utility");
			Log.i(TAG, "Application Information: ");	
			try{

				Log.i(TAG, "* Release Version: " + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode);
				Log.i(TAG, "* Version Name: "+ this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
			}catch(Exception e){
				Log.e(TAG, "WARNING!\nERROR ON EXECUTING COMMAND : \'this.getPackageManager().getPackageInfo(this.getPackageName(), 0)\'");
			}
			Log.i(TAG, "* Package Name: " + this.getApplicationInfo().packageName);	
			Log.i(TAG, "* Process Name: " + this.getApplicationInfo().processName);	
			Log.i(TAG, "* Target SDK: " + this.getApplicationInfo().targetSdkVersion);	
			Log.i(TAG, "* Application External Directory: " +extFileDir.toString());
			Log.i(TAG, "* Application Root Directory: " +intFileDir.toString());
			Log.i(TAG, "* Application Database Directory: " +dbFileDir.toString());
			Log.i(TAG, "* Database: " +SQLiteHelper.getDatabaseVersion());
			Log.i(TAG, "* Developer: {ERROR c0d3s1x: \"The handle is invalid\"}");
		}
	}
	
	public void bootTasks(){
		Log.i(TAG, "########################################");
		Log.i(TAG, " Starting SharpFix initial tasks . . .\n");
		Log.i(TAG, "{Android Version} Detection Initializing . . .");
		Logcat.i(this, AndroidUtils.getCurrentAndroidVersionInfo());
		Log.i(TAG, "{Root Access} Detection Initializing . . .\n");
		Log.w(TAG, "{Root Access} Detection awaiting permission . . .\n");
		this.setRootAccess(ExecuteAsRootBase.canRunRootCommands());
		Log.e(TAG, (this.getRootAccess()) ? "{Root Access} available!" : "{Root Access} unavailable!");
		Log.i(TAG, "{Active and Mounted Volumes} Detection Initializing . . .");
		Logcat.i(this,  AndroidUtils.getMountedVolumes());
		
	}
	
	@Override
	public void onCreate(){
		super.onCreate();

		extFileDir = this.getExternalFilesDir(null).getParentFile();
		intFileDir = this.getFilesDir().getParentFile();
		dbFileDir = this.getDatabasePath("sharpfix_database.db").getParentFile();
		bootMessage();
		bootTasks();
	}
	
	@Override
	public void onTerminate(){
		// experimental according to Google
	}

	public void resetAll(){
		this.accountId = null;
		this.fddSwitch = null;
		this.fdSwitch = null;
		this.fdFilterSwitch = null;
		this.fddFilterSwitch = null;
		this.fddPref = null;
		this.autoLogin = null;
		this.rootAccess = null;
		this.serviceSwitch = null; 
		this.serviceHour = null; 
		this.serviceMin = null; 
		this.serviceAMPM = null;
		this.serviceUpdateSwitch = null;  
		this.serviceRepeat = null;
		this.serviceNoti = null;
		this.auSwitch = null; 
		
		this.root = null;
	}
	
	// getters:
	public Boolean getRootAccess(){
		return this.rootAccess;
	}
	
	public Boolean getRootPermission(){
		return this.root;
	}
	
	public boolean getLogCatSwitch(){
		return this.LOGCAT;
	}
	
	public Integer getAccountId(){
		return this.accountId;
	}
	
	public Integer getFddSwitch(){
		return this.fddSwitch;
	}
	
	public Integer getFdSwitch(){
		return this.fdSwitch;
	}
	
	public Integer getFdFilterSwitch(){
		return this.fdFilterSwitch;
	}
	
	public Integer getFddFilterSwitch(){
		return this.fddFilterSwitch;
	}
	
	
	public Integer getFddPref(){
		return this.fddPref;
	}
	
	public Integer getAutoLogin(){
		return this.autoLogin;
	}
	
	
	
	public Integer getServiceSwitch(){
		return this.serviceSwitch;
	}
	
	public Integer getServiceHour(){
		return this.serviceHour;
	}
	
	public Integer getServiceMin(){
		return this.serviceMin;
	}
	
	public Integer getServiceAMPM(){
		return this.serviceAMPM;
	}
	
	public Integer getServiceUpdateSwitch(){
		return this.serviceUpdateSwitch;
	}
	
	public Integer getServiceRepeat(){
		return this.serviceRepeat;
	}
	
	public Integer getServiceNoti(){
		return this.serviceNoti;
	}
	
	public Integer getAuSwitch(){
		return this.auSwitch;
	}
	
	
	public void setRootAccess(Boolean ra){
		this.rootAccess = ra;
	}
	
	public void setRootPermission(Boolean p){
		this.root = p;
	}
	
	public void setAccountId(Integer id){
		this.accountId = id;
	}
	
	public void setFddSwitch(Integer sw){
		this.fddSwitch = sw;
	}
	
	public void setFdSwitch(Integer sw){
		this.fdSwitch = sw;
	}
	
	public void setFdFilterSwitch(Integer sw){
		this.fdFilterSwitch = sw;
	}
	
	public void setFddFilterSwitch(Integer sw){
		this.fddFilterSwitch = sw;
	}
	
	
	public void setFddPref(Integer pref){
		this.fddPref = pref;
	}
	
	public void setAutoLogin(Integer sw){
		this.autoLogin = sw;
	}
	
	public void setServiceSwitch(Integer sw){
		this.serviceSwitch = sw;
	}
	
	public void setServiceHour(Integer hh){
		this.serviceHour = hh;
	}
	
	public void setServiceMin(Integer mm){
		this.serviceMin = mm;
	}
	
	public void setServiceAMPM(Integer ampm){
		this.serviceAMPM = ampm;
	}
	
	public void setServiceUpdateSwitch(Integer sw){
		this.serviceUpdateSwitch = sw;
	}
	
	public void setServiceRepeat(Integer repeat){
		this.serviceRepeat = repeat;
	}
	
	public void setServiceNoti(Integer noti){
		this.serviceNoti = noti;
	}
	
	public void setAuSwitch(Integer sw){
		this.auSwitch = sw;
	}
		
	// call this method explicitly everytime user_pref is being updated in the database
	public void updatePreferences(SQLiteHelper db){
		if(LOGCAT){
			Log.w(TAG, this.getClass().getName() + " USER PREFERENCES HAS BEEN CHANGED!");
		}
		try{
			ModelPreferences newPreferences = (ModelPreferences)db.select(Tables.preferences, ModelPreferences.class, new Object[][]{{"account",this.getAccountId()}}, null);
			this.setAccountId(newPreferences.getAccount());
			this.setAutoLogin(newPreferences.getAuto_login());
			this.setFddPref(newPreferences.getFdd_pref());
			this.setFddSwitch(newPreferences.getFdd_switch());
			this.setFdSwitch(newPreferences.getFd_switch());
			this.setFdFilterSwitch(newPreferences.getFd_Filter_switch());
			this.setFdFilterSwitch(newPreferences.getFdd_Filter_switch());
			// new fields
			// Sss_switch, Sss_hh, Sss_mm, Sss_ampm, Sss_update, Sss_repeat, Au_switch
			
			this.setServiceSwitch(newPreferences.getSss_switch());
			this.setServiceHour(newPreferences.getSss_hh());
			this.setServiceMin(newPreferences.getSss_mm());
			this.setServiceAMPM(newPreferences.getSss_ampm());
			this.setServiceUpdateSwitch(newPreferences.getSss_update());
			this.setServiceRepeat(newPreferences.getSss_repeat());
			this.setServiceNoti(newPreferences.getSss_noti());
			this.setAuSwitch(newPreferences.getAu_switch());
			
		}catch(Exception e){
			
		}
	}
	
}
