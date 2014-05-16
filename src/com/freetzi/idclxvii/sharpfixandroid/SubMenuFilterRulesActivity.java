package com.freetzi.idclxvii.sharpfixandroid;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class SubMenuFilterRulesActivity extends Activity {

	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	private SQLiteHelper db;
		
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.TAG = this.getClass().getName().replace(this.getPackageName(), "");
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onCreate()");
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onStart()");
		}
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onRestart()");
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onResume()");
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onPause()");
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onStop()");
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onDestroy()");
		}
	}
	
	

}
