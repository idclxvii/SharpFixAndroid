package tk.idclxvii.sharpfixandroid;

import java.io.File;

import tk.idclxvii.sharpfixandroid.databasemodel.*;

import android.app.Application;
import android.util.Log;

public class SharpFixApplicationClass extends Application{
	
	private final boolean LOGCAT = true;
	private final String TAG = "." +this.getClass().getName();
	
	private File extFileDir;
	private File intFileDir;
	private File dbFileDir;
	
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
	
	
	
	private Boolean rootAccess; // this is just to know whether the device is rooted or not, do not base on this when calling root commands
	private Boolean root; // current permission given to a root command, this is the dynamic variable to watch when calling root commands
	
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
	
	
	@Override
	public void onCreate(){
		super.onCreate();

		extFileDir = this.getExternalFilesDir(null).getParentFile();
		intFileDir = this.getFilesDir().getParentFile();
		dbFileDir = this.getDatabasePath("sharpfix_database.db").getParentFile();
		
		if(LOGCAT){
			Log.d(TAG, this.getClass().getName() + " onCreate()");
			Log.i(TAG, "Application Information: ");	
			Log.i(TAG, "Package Name: " + this.getApplicationInfo().packageName);	
			Log.i(TAG, "Process Name: " + this.getApplicationInfo().processName);	
			Log.i(TAG, "Target SDK: " + this.getApplicationInfo().targetSdkVersion);	
			Log.i(TAG, "Application External Dir: " +extFileDir.toString());
			Log.i(TAG, "Application Internal Dir: " +intFileDir.toString());
			Log.i(TAG, "Application Database Dir: " +dbFileDir.toString());
		}
		
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
