package tk.idclxvii.sharpfixandroid;

import tk.idclxvii.sharpfixandroid.databasemodel.ModelPreferences;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import tk.idclxvii.sharpfixandroid.utils.*;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SubMenuServicesActivity extends GlobalExceptionHandlerActivity implements OnCheckedChangeListener{

	private SharpFixApplicationClass SF;
	private final String TAG = this.getClass().getSimpleName();
	private boolean LOGCAT;
	SQLiteHelper db;
	
	// layout fields
	CheckBox  chSchedScan, chAutoUpd;
	TextView title, schedScanLabel, schedScanSettings, schedScanSettingsLabel, autoUpdLabel, directScanSettings, directScanSettingsLabel,
	 logs, logsLabel;
	
	// service Intents
	private Intent dsIntent, fdsIntent, fddsIntent;
	
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	  }
	  
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  " onCreate()");
		}
		// initialize database connection
		db = this.getDb(getApplicationContext());
		setContentView(R.layout.services_sub_menu);
		
		createServiceIntents();
		
		title = (TextView)findViewById(R.id.title);
		title.setText(title.getText().toString().toUpperCase());
		
		chSchedScan = (CheckBox) findViewById(R.id.services_selection1);
		
		schedScanLabel = (TextView) findViewById(R.id.services_label1);
		schedScanLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chSchedScan.performClick();
			}
			
		});
		schedScanSettings = (TextView) findViewById(R.id.services_selection2);
		schedScanSettings.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// call scheduled scan settings Activity
				startActivity(new Intent(SubMenuServicesActivity.this, SubMenuSchedScanSettingsActivity.class));
				
			}
			
		});
		
		schedScanSettingsLabel = (TextView) findViewById(R.id.services_label2);
		schedScanSettingsLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				schedScanSettings.setPressed(true);
				schedScanSettings.performClick();
			}
			
		});
		
		chAutoUpd = (CheckBox) findViewById(R.id.services_selection3);
		
		autoUpdLabel = (TextView) findViewById(R.id.services_label3);
		autoUpdLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chAutoUpd.performClick();
				
			}
			
		});
		
		
		// ###########################################################
		
		directScanSettingsLabel = (TextView) findViewById(R.id.services_label4);
		directScanSettingsLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				directScanSettings.setPressed(true);
				directScanSettings.performClick();
			}
			
		});
		directScanSettings = (TextView) findViewById(R.id.services_selection4);
		directScanSettings.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// call scheduled scan settings Activity
				startActivity(new Intent(SubMenuServicesActivity.this, SubMenuDirectScanControls.class));
				
			}
			
		});
		
		logsLabel = (TextView) findViewById(R.id.services_label5);
		logsLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logs.setPressed(true);
				logs.performClick();
			}
			
		});
		logs = (TextView) findViewById(R.id.services_selection5);
		logs.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// call scheduled scan settings Activity
				startActivity(new Intent(SubMenuServicesActivity.this, SubMenuLogs.class));
				
			}
			
		});
	
		
		
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
			
		}else{
			chSchedScan = AndroidLayoutUtils.fixCheckBoxPaddingLeft(this, chSchedScan,50.0f);
			chAutoUpd = AndroidLayoutUtils.fixCheckBoxPaddingLeft(this, chAutoUpd,50.0f);
		}
		
		
		
		
		
		
	}

	
	
	private void createServiceIntents(){
		this.dsIntent = new Intent(this, DirectoryScanner.class);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		if(LOGCAT){
			Log.d(TAG, this.getClass().getName() + " onStart()");
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if(LOGCAT){
			Log.d(TAG, this.getClass().getName() + " onRestart()");
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(LOGCAT){
			Log.d(TAG, this.getClass().getName() + " onResume()");
		}
		try{
			if( SF.getServiceSwitch() == 0){
				chSchedScan.setChecked(false);
				
			}else{
				chSchedScan.setChecked(true);
			}
			if( SF.getAuSwitch() == 0){
				chAutoUpd.setChecked(false);
				
			}else{
				chAutoUpd.setChecked(true);
			}
		}catch(Exception e){
			
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(LOGCAT){
			Log.d(TAG, this.getClass().getName() + " onPause()");
		}
		
		// ###############################################################################################################
 		if((chSchedScan.isChecked() && SF.getServiceSwitch() == 0 ) ||
				(!chSchedScan.isChecked() && SF.getServiceSwitch() == 1)){
			// update database 
			if(chSchedScan.isChecked()){
				//	turned on
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
					newParams.setSss_switch(1);
					
					this.db.update(Tables.preferences, oldParams, newParams, null);
					SF.setServiceSwitch(1);
				}catch(Exception e){
					
				}
			}else{
				// turned off
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
					newParams.setSss_switch(0);
					this.db.update(Tables.preferences, oldParams, newParams, null);
					SF.setServiceSwitch(0);
				}catch(Exception e){
						
				}
					
			}
			SF.updatePreferences(db);
		}else{
			// Toast.makeText(this, "File Duplication Detection Filtering Settings was not changed" ,Toast.LENGTH_LONG).show();
		}
 		
 		if((chAutoUpd.isChecked() && SF.getAuSwitch() == 0 ) ||
				(!chAutoUpd.isChecked() && SF.getAuSwitch() == 1)){
			// update database 
			if(chAutoUpd.isChecked()){
				// turned on
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
					newParams.setAu_switch(1);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Designation Settings have been updated!" :
							"File Designation Settings failed to update!" ,Toast.LENGTH_LONG).show();
						*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					SF.setAuSwitch(1);
				}catch(Exception e){
					
				}
			}else{
				// turned off
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
					newParams.setAu_switch(0);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Designation Settings have been updated!" :
							"File Designation Settings failed to update!" ,Toast.LENGTH_LONG).show();
					*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					SF.setAuSwitch(0);
				}catch(Exception e){
						
				}
					
			}

		}else{
			// Toast.makeText(this, "File Designation Settings was not changed" ,Toast.LENGTH_LONG).show();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		this.db.close();
		if(LOGCAT){
			Log.d(TAG, this.getClass().getName() + " onStop()");
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.db.close();
		if(LOGCAT){
			Log.d(TAG, this.getClass().getName() + " onDestroy()");
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
			  SF.resetAll();
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
		
	}


	
	
}
