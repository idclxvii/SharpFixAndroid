package tk.idclxvii.sharpfixandroid;

import tk.idclxvii.sharpfixandroid.databasemodel.ModelPreferences;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import tk.idclxvii.sharpfixandroid.utils.AndroidLayoutUtils;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.View.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;

public class SubMenuFddActivity extends GlobalExceptionHandlerActivity implements OnClickListener, OnCheckedChangeListener{

	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private final String TAG = this.getClass().getSimpleName();
	private boolean LOGCAT;
	
	TextView title;
	CheckBox ch;
	TextView chl;
	RadioGroup rg;
	RadioButton older;
	RadioButton newer;
	TextView label;
	TextView selection;
	SQLiteHelper db;
	
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onCreate()");
		}
		
		setContentView(R.layout.fdd_sub_menu);
		title = (TextView)findViewById(R.id.title);
		title.setText(title.getText().toString().toUpperCase());
		
		ch = (CheckBox) findViewById(R.id.selection1);
		ch.setOnClickListener(this);
		ch.setOnCheckedChangeListener(this);
		chl =  (TextView) findViewById(R.id.label1);
		chl.setOnClickListener(this);
		rg =  (RadioGroup) findViewById(R.id.deletionPriority);
		older = (RadioButton) findViewById(R.id.radioOlder);
		newer = (RadioButton) findViewById(R.id.radioNewer);
		selection = (TextView) findViewById(R.id.selection2);
		selection.setOnClickListener(this);
		label = (TextView) findViewById(R.id.label2);
		label.setOnClickListener(this);
		
		this.db = this.getDb(getApplicationContext());
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
			
		}else{
			ch = AndroidLayoutUtils.fixCheckBoxPaddingLeft(this, ch,50.0f);
			older = AndroidLayoutUtils.fixRadioButtonPaddingLeft(this, older, 50.f);
			newer = AndroidLayoutUtils.fixRadioButtonPaddingLeft(this, newer, 50.f);
			
			
		}
		checkChanged(0);
	}

	@Override
	public void onPause(){
		super.onPause();
		if((ch.isChecked() && SF.getFddSwitch() == 0 ) ||
				(!ch.isChecked() && SF.getFddSwitch() == 1)){
			// update database 
			if(ch.isChecked()){
				//	File Duplication Detection Features is turned on
				try{
					ModelPreferences newParams = new ModelPreferences(SF.getAccountId(),
							SF.getFddSwitch(),
							SF.getFdSwitch(),
							SF.getFddPref(),
							SF.getAutoLogin(),
							SF.getFddFilterSwitch(),
							SF.getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							SF.getServiceSwitch(),
							SF.getServiceHour(),
							SF.getServiceMin(),
							SF.getServiceAMPM(),
							SF.getServiceUpdateSwitch(),
							SF.getServiceRepeat(),
							SF.getServiceNoti(),
							SF.getAuSwitch());
					ModelPreferences oldParams = new ModelPreferences(SF.getAccountId(),
							SF.getFddSwitch(),
							SF.getFdSwitch(),
							SF.getFddPref(),
							SF.getAutoLogin(),
							SF.getFddFilterSwitch(),
							SF.getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							SF.getServiceSwitch(),
							SF.getServiceHour(),
							SF.getServiceMin(),
							SF.getServiceAMPM(),
							SF.getServiceUpdateSwitch(),
							SF.getServiceRepeat(),
							SF.getServiceNoti(),
							SF.getAuSwitch());
					newParams.setFdd_switch(1);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Duplication Detection Settings have been updated!" :
							"File Duplication Detection Settings failed to update!" ,Toast.LENGTH_LONG).show();
					*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					SF.setFddSwitch(1);
				}catch(Exception e){
						
				}
			}else{
				// File Duplication Detection Features is turned off
				try{
					ModelPreferences newParams = new ModelPreferences(SF.getAccountId(),
							SF.getFddSwitch(),
							SF.getFdSwitch(),
							SF.getFddPref(),
							SF.getAutoLogin(),
							SF.getFddFilterSwitch(),
							SF.getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							SF.getServiceSwitch(),
							SF.getServiceHour(),
							SF.getServiceMin(),
							SF.getServiceAMPM(),
							SF.getServiceUpdateSwitch(),
							SF.getServiceRepeat(),
							SF.getServiceNoti(),
							SF.getAuSwitch());
					ModelPreferences oldParams = new ModelPreferences(SF.getAccountId(),
							SF.getFddSwitch(),
							SF.getFdSwitch(),
							SF.getFddPref(),
							SF.getAutoLogin(),
							SF.getFddFilterSwitch(),
							SF.getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							SF.getServiceSwitch(),
							SF.getServiceHour(),
							SF.getServiceMin(),
							SF.getServiceAMPM(),
							SF.getServiceUpdateSwitch(),
							SF.getServiceRepeat(),
							SF.getServiceNoti(),
							SF.getAuSwitch());
					newParams.setFdd_switch(0);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Duplication Detection Settings have been updated!" :
							"File Duplication Detection Settings failed to update!" ,Toast.LENGTH_LONG).show();
					*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					SF.setFddSwitch(0);
				}catch(Exception e){
						
				}
					
			}

		}else{
			// Toast.makeText(this, "File Duplication Detection Settings was not changed" ,Toast.LENGTH_LONG).show();
		}
		
		if((newer.isChecked() && SF.getFddPref() == 0 ) ||
				(!newer.isChecked() && SF.getFddPref() == 1)){
			// update database 
			if(newer.isChecked()){
				//	Duplicate Files Deletion Priority deletes newer files
				try{
					ModelPreferences newParams = new ModelPreferences(SF.getAccountId(),
							SF.getFddSwitch(),
							SF.getFdSwitch(),
							SF.getFddPref(),
							SF.getAutoLogin(),
							SF.getFddFilterSwitch(),
							SF.getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							SF.getServiceSwitch(),
							SF.getServiceHour(),
							SF.getServiceMin(),
							SF.getServiceAMPM(),
							SF.getServiceUpdateSwitch(),
							SF.getServiceRepeat(),
							SF.getServiceNoti(),
							SF.getAuSwitch());
					ModelPreferences oldParams = new ModelPreferences(SF.getAccountId(),
							SF.getFddSwitch(),
							SF.getFdSwitch(),
							SF.getFddPref(),
							SF.getAutoLogin(),
							SF.getFddFilterSwitch(),
							SF.getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							SF.getServiceSwitch(),
							SF.getServiceHour(),
							SF.getServiceMin(),
							SF.getServiceAMPM(),
							SF.getServiceUpdateSwitch(),
							SF.getServiceRepeat(),
							SF.getServiceNoti(),
							SF.getAuSwitch());
					newParams.setFdd_pref(1);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Duplication Detection Settings have been updated!" :
							"File Duplication Detection Settings failed to update!" ,Toast.LENGTH_LONG).show();
					*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					SF.setFddPref(1);
				}catch(Exception e){
						
				}
			}else{
				//	Duplicate Files Deletion Priority deletes older files
				try{
					ModelPreferences newParams = new ModelPreferences(SF.getAccountId(),
							SF.getFddSwitch(),
							SF.getFdSwitch(),
							SF.getFddPref(),
							SF.getAutoLogin(),
							SF.getFddFilterSwitch(),
							SF.getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							SF.getServiceSwitch(),
							SF.getServiceHour(),
							SF.getServiceMin(),
							SF.getServiceAMPM(),
							SF.getServiceUpdateSwitch(),
							SF.getServiceRepeat(),
							SF.getServiceNoti(),
							SF.getAuSwitch());
					ModelPreferences oldParams = new ModelPreferences(SF.getAccountId(),
							SF.getFddSwitch(),
							SF.getFdSwitch(),
							SF.getFddPref(),
							SF.getAutoLogin(),
							SF.getFddFilterSwitch(),
							SF.getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							SF.getServiceSwitch(),
							SF.getServiceHour(),
							SF.getServiceMin(),
							SF.getServiceAMPM(),
							SF.getServiceUpdateSwitch(),
							SF.getServiceRepeat(),
							SF.getServiceNoti(),
							SF.getAuSwitch());
					newParams.setFdd_pref(0);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Duplication Detection Priority have been updated!" :
							"File Duplication Detection Priority failed to update!" ,Toast.LENGTH_LONG).show();
					*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					SF.setFddPref(0);
				}catch(Exception e){
						
				}
					
			}

		}else{
			// Toast.makeText(this, "File Duplication Detection Settings was not changed" ,Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		this.db.close();
		
		if(this.LOGCAT){
			Log.d(this.TAG, this.db.toString());
			Log.d(this.TAG, this.TAG +  " onDestroy()");
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		try{
			if( SF.getFddSwitch() == 0){
				ch.setChecked(false);
				
			}else{
				ch.setChecked(true);
			}
		}catch(Exception e){
			
		}
		try{
			if( SF.getFddPref() == 0){
				newer.setChecked(false);
				
			}else{
				newer.setChecked(true);
			}
		}catch(Exception e){
			
		}
	}
		
	@Override
	  public boolean onCreateOptionsMenu(Menu menu){
		  MenuInflater mi = getMenuInflater();
		  mi.inflate(R.menu.main, menu);
		  return true;
	  }
	  
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item){
		  // handle item selection:
		  switch (item.getItemId()){
		  
		  
		  case R.id.MenuLogout:
			// call method / do task.
			  ModelPreferences oldParams = new ModelPreferences(SF.getAccountId(),
						SF.getFddSwitch(),
						SF.getFdSwitch(),
						SF.getFddPref(),
						SF.getAutoLogin(),
						SF.getFddFilterSwitch(),
						SF.getFdFilterSwitch(),
						
						// new fields
						// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
						SF.getServiceSwitch(),
						SF.getServiceHour(),
						SF.getServiceMin(),
						SF.getServiceAMPM(),
						SF.getServiceUpdateSwitch(),
						SF.getServiceRepeat(),
						SF.getServiceNoti(),
						SF.getAuSwitch());
			  ModelPreferences newParams = new ModelPreferences( SF.getAccountId(),
						SF.getFddSwitch(),
						SF.getFdSwitch(),
						SF.getFddPref(),
						SF.getAutoLogin(),
						SF.getFddFilterSwitch(),
						SF.getFdFilterSwitch(),
						
						// new fields
						// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
						SF.getServiceSwitch(),
						SF.getServiceHour(),
						SF.getServiceMin(),
						SF.getServiceAMPM(),
						SF.getServiceUpdateSwitch(),
						SF.getServiceRepeat(),
						SF.getServiceNoti(),
						SF.getAuSwitch());
			  newParams.setAuto_login(0);
			  try{
				  this.db.update(Tables.preferences, oldParams, newParams, null);
				  SF.updatePreferences(this.db);
			  }catch(Exception e){}
			
			  Intent i = new Intent(this,MainActivity.class);
			  i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			  startActivity(i);
			  finish();
			  return true;
			  
		  default:
			  return super.onOptionsItemSelected(item);
				  
		  }
	  }
	
	@Override
	public void onClick(View src){
		
	
		switch (src.getId()) {
	    
		case R.id.label1:
			ch.performClick();
			break;
		
		case R.id.selection2:
			checkChanged(R.id.selection2);
			break;
			
		case R.id.label2:
			
			selection.setPressed(true);
			selection.performClick();
			break;
			
		case R.id.radioNewer:
			
			newer.setPressed(true);
			break;
		
		case R.id.radioOlder:
			
			older.setPressed(true);
			break;

		case R.id.deletionPriority:
		
			selection.setPressed(true);
			selection.performClick();
			
			
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean checkedState) {

		if(ch.isChecked()){
			
		}else{
		
		}

		

		checkChanged(0);
		
		
	}
	
	private void checkChanged(int id){
		if(!ch.isChecked()){
			// rg.setEnabled(false);
			//rg.setVisibility(View.INVISIBLE);
			older.setEnabled(false);
			//older.setVisibility(View.INVISIBLE);
			newer.setEnabled(false);
			//newer.setVisibility(View.INVISIBLE);
			if(id == R.id.selection2 ){
			Toast.makeText(this,
				 	   "Please Enable Automatic Deletion first to change this preference!", Toast.LENGTH_LONG).show();
			}
		}else{
	//		rg.setEnabled(true);
			//rg.setVisibility(View.VISIBLE);
			older.setEnabled(true);
			//older.setVisibility(View.VISIBLE);
			newer.setEnabled(true);
			//newer.setVisibility(View.VISIBLE);
			
		}
	}
	
	
} // end class
