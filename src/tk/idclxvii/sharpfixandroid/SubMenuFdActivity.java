package tk.idclxvii.sharpfixandroid;

import tk.idclxvii.sharpfixandroid.databasemodel.*;
import tk.idclxvii.sharpfixandroid.utils.AndroidLayoutUtils;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.CompoundButton.*;

public class SubMenuFdActivity extends Activity implements OnClickListener{

	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
		
	TextView title;
	CheckBox ch;
	TextView chl;
	TextView label;
	TextView selection;
	private SQLiteHelper db;

	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fd_sub_menu);
		this.TAG = this.getClass().getName().replace(this.getPackageName(), "");
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onCreate()");
		}
		
		title = (TextView)findViewById(R.id.title);
		title.setText(title.getText().toString().toUpperCase());
		
		ch = (CheckBox) findViewById(R.id.selection1);
		ch.setOnClickListener(this);
		chl =  (TextView) findViewById(R.id.label1);
		chl.setOnClickListener(this);
		selection = (TextView) findViewById(R.id.selection2);
		selection.setOnClickListener(this);
		label = (TextView) findViewById(R.id.label2);
		label.setOnClickListener(this);
		
		this.db = this.getDb(getApplicationContext());
		
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
			
		}else{
			ch = AndroidLayoutUtils.fixCheckBoxPaddingLeft(this, ch,50.0f);
		}
		
		
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.db.close();
		
		if(LOGCAT){
			Log.d(TAG, this.getClass().getName() + " OnDestroy()");
		}
		
	}
	
	@Override
	public void onPause() {

 		super.onPause();
		if((ch.isChecked() && ((SharpFixApplicationClass) getApplication()).getFdSwitch() == 0 ) ||
				(!ch.isChecked() && ((SharpFixApplicationClass) getApplication()).getFdSwitch() == 1)){
			// update database 
			if(ch.isChecked()){
				//	File Designation Feature is turned on
				try{
					ModelPreferences newParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							((SharpFixApplicationClass) getApplication()).getServiceSwitch(),
							((SharpFixApplicationClass) getApplication()).getServiceHour(),
							((SharpFixApplicationClass) getApplication()).getServiceMin(),
							((SharpFixApplicationClass) getApplication()).getServiceAMPM(),
							((SharpFixApplicationClass) getApplication()).getServiceUpdateSwitch(),
							((SharpFixApplicationClass) getApplication()).getServiceRepeat(),
							((SharpFixApplicationClass) getApplication()).getServiceNoti(),
							((SharpFixApplicationClass) getApplication()).getAuSwitch());
					ModelPreferences oldParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							((SharpFixApplicationClass) getApplication()).getServiceSwitch(),
							((SharpFixApplicationClass) getApplication()).getServiceHour(),
							((SharpFixApplicationClass) getApplication()).getServiceMin(),
							((SharpFixApplicationClass) getApplication()).getServiceAMPM(),
							((SharpFixApplicationClass) getApplication()).getServiceUpdateSwitch(),
							((SharpFixApplicationClass) getApplication()).getServiceRepeat(),
							((SharpFixApplicationClass) getApplication()).getServiceNoti(),
							((SharpFixApplicationClass) getApplication()).getAuSwitch());
					newParams.setFd_switch(1);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Designation Settings have been updated!" :
							"File Designation Settings failed to update!" ,Toast.LENGTH_LONG).show();
						*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					((SharpFixApplicationClass) getApplication()).setFdSwitch(1);
				}catch(Exception e){
					
				}
			}else{
				//	File Designation Feature is turned off
				try{
					ModelPreferences newParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							((SharpFixApplicationClass) getApplication()).getServiceSwitch(),
							((SharpFixApplicationClass) getApplication()).getServiceHour(),
							((SharpFixApplicationClass) getApplication()).getServiceMin(),
							((SharpFixApplicationClass) getApplication()).getServiceAMPM(),
							((SharpFixApplicationClass) getApplication()).getServiceUpdateSwitch(),
							((SharpFixApplicationClass) getApplication()).getServiceRepeat(),
							((SharpFixApplicationClass) getApplication()).getServiceNoti(),
							((SharpFixApplicationClass) getApplication()).getAuSwitch());
					ModelPreferences oldParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch(),
							
							// new fields
							// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
							((SharpFixApplicationClass) getApplication()).getServiceSwitch(),
							((SharpFixApplicationClass) getApplication()).getServiceHour(),
							((SharpFixApplicationClass) getApplication()).getServiceMin(),
							((SharpFixApplicationClass) getApplication()).getServiceAMPM(),
							((SharpFixApplicationClass) getApplication()).getServiceUpdateSwitch(),
							((SharpFixApplicationClass) getApplication()).getServiceRepeat(),
							((SharpFixApplicationClass) getApplication()).getServiceNoti(),
							((SharpFixApplicationClass) getApplication()).getAuSwitch());
					newParams.setFd_switch(0);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Designation Settings have been updated!" :
							"File Designation Settings failed to update!" ,Toast.LENGTH_LONG).show();
					*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					((SharpFixApplicationClass) getApplication()).setFdSwitch(0);
				}catch(Exception e){
						
				}
					
			}

		}else{
			// Toast.makeText(this, "File Designation Settings was not changed" ,Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		try{
			if( ((SharpFixApplicationClass) getApplication()).getFdSwitch() == 0){
				ch.setChecked(false);
				
			}else{
				ch.setChecked(true);
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
			  ModelPreferences oldParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
						((SharpFixApplicationClass) getApplication()).getFddSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdSwitch(),
						((SharpFixApplicationClass) getApplication()).getFddPref(),
						((SharpFixApplicationClass) getApplication()).getAutoLogin(),
						((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdFilterSwitch(),
						
						// new fields
						// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
						((SharpFixApplicationClass) getApplication()).getServiceSwitch(),
						((SharpFixApplicationClass) getApplication()).getServiceHour(),
						((SharpFixApplicationClass) getApplication()).getServiceMin(),
						((SharpFixApplicationClass) getApplication()).getServiceAMPM(),
						((SharpFixApplicationClass) getApplication()).getServiceUpdateSwitch(),
						((SharpFixApplicationClass) getApplication()).getServiceRepeat(),
						((SharpFixApplicationClass) getApplication()).getServiceNoti(),
						((SharpFixApplicationClass) getApplication()).getAuSwitch());
			  ModelPreferences newParams = new ModelPreferences( ((SharpFixApplicationClass) getApplication()).getAccountId(),
						((SharpFixApplicationClass) getApplication()).getFddSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdSwitch(),
						((SharpFixApplicationClass) getApplication()).getFddPref(),
						((SharpFixApplicationClass) getApplication()).getAutoLogin(),
						((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdFilterSwitch(),
						
						// new fields
						// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
						((SharpFixApplicationClass) getApplication()).getServiceSwitch(),
						((SharpFixApplicationClass) getApplication()).getServiceHour(),
						((SharpFixApplicationClass) getApplication()).getServiceMin(),
						((SharpFixApplicationClass) getApplication()).getServiceAMPM(),
						((SharpFixApplicationClass) getApplication()).getServiceUpdateSwitch(),
						((SharpFixApplicationClass) getApplication()).getServiceRepeat(),
						((SharpFixApplicationClass) getApplication()).getServiceNoti(),
						((SharpFixApplicationClass) getApplication()).getAuSwitch());
			  newParams.setAuto_login(0);
			  try{
				  this.db.update(Tables.preferences, oldParams, newParams, null);
				  ((SharpFixApplicationClass) getApplication()).updatePreferences(this.db);
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
	public void onClick(View src) {
		
	switch (src.getId()) {
	    
		case R.id.label1:
			ch.performClick();
			
			break;
		
		case R.id.selection2:
			startActivity(new Intent(this, SubMenuFdRulesActivity.class));
			
			
			break;
			
		case R.id.label2:
			selection.setPressed(true);
			selection.performClick();
			break;
			
		}
		
		
		
	}
	
}// end of class
