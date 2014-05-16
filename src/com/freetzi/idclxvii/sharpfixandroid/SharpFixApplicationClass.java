package com.freetzi.idclxvii.sharpfixandroid;

import com.freetzi.idclxvii.sharpfixandroid.databasemodel.*;

import android.app.Application;
import android.util.Log;

public class SharpFixApplicationClass extends Application{
	
	private final boolean LOGCAT = true;
	private final String TAG = "." +this.getClass().getName();
	
	private Integer accountId;
	private Integer fddSwitch;
	private Integer fdSwitch;
	private Integer filterSwitch;
	private Integer fddPref; // preference if older or newer file will be deleted in case of file duplication : 1 = newer, 0  = older
	private Integer autoLogin  = 0;
	
	
	@Override
	public void onCreate(){
		super.onCreate();
		if(LOGCAT){
			Log.d(TAG, this.getClass().getName() + " onCreate()");
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
		this.filterSwitch = null;
		this.fddPref = null;
		this.autoLogin = null;
		
	}
	
	// getters:
	
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
	
	public Integer getFilterSwitch(){
		return this.filterSwitch;
	}
	
	public Integer getFddPref(){
		return this.fddPref;
	}
	
	public Integer getAutoLogin(){
		return this.autoLogin;
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
	
	public void setFilterSwitch(Integer sw){
		this.filterSwitch = sw;
	}
	
	public void setFddPref(Integer pref){
		this.fddPref = pref;
	}
	
	public void setAutoLogin(Integer sw){
		this.autoLogin = sw;
	}
	
		
	// call this method explicitly everytime user_pref is being updated in the database
	public void updatePreferences(SQLiteHelper db){
		try{
			ModelPreferences newPreferences = (ModelPreferences)db.select(Tables.preferences, ModelPreferences.class, new Object[][]{{"account",this.getAccountId()}}, null);
			this.setAccountId(newPreferences.getAccount());
			this.setAutoLogin(newPreferences.getAuto_login());
			this.setFddPref(newPreferences.getFdd_pref());
			this.setFddSwitch(newPreferences.getFdd_switch());
			this.setFdSwitch(newPreferences.getFd_switch());
			this.setFilterSwitch(newPreferences.getFilter_switch());
			
		}catch(Exception e){
			
		}
	}
	
}
