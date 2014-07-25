package tk.idclxvii.sharpfixandroid;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import tk.idclxvii.sharpfixandroid.databasemodel.ModelPreferences;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import tk.idclxvii.sharpfixandroid.utils.AndroidLayoutUtils;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SubMenuSchedScanSettingsActivity extends Activity {

	
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	SQLiteHelper db;
	
	// layout fields
	private CheckBox  chUpd, chAlert;
	private TextView title, timeSelection, timeLabel, repeatSelection, repeatLabel, updLabel, alertLabel;
	private int hour, minute;
	
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	  }
	  
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.TAG = this.getClass().getName().replace(this.getPackageName(), "");
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  " onCreate()");
		}
		// initialize database connection
		db = this.getDb(getApplicationContext());
		
		setContentView(R.layout.sched_scan_settings_sub_menu);
		
		title = (TextView)findViewById(R.id.title);
		title.setText(title.getText().toString().toUpperCase());
		
		 
		final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		
		timeSelection = (TextView) findViewById(R.id.sssUpdateSelection1);
		timeSelection.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new TimePickerDialog(SubMenuSchedScanSettingsActivity.this,
						new TimePickerDialog.OnTimeSetListener(){
							@Override
							public void onTimeSet(TimePicker view, int selectedHour,int selectedMinute) {
								hour = selectedHour;
								minute = selectedMinute;
					 
								// set current time into textview
								
								// set current time into timepicker
								
								
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
								newParams.setSss_hh(hour);
								newParams.setSss_mm(minute);
								
								
								// ##################################################################################
								// temporary everyday alarm
								newParams.setSss_repeat(7);
								// ##################################################################################
								
								
								try {
									db.update(Tables.preferences,oldParams, newParams,null);
									Calendar c = Calendar.getInstance();
									c.setTimeInMillis(System.currentTimeMillis());
									c.set(Calendar.HOUR_OF_DAY, hour);
									c.set(Calendar.MINUTE,minute);
									
									AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
									Intent intent = new Intent(SubMenuSchedScanSettingsActivity.this, Alarm.class);
									PendingIntent pendingIntent = PendingIntent.getBroadcast(SubMenuSchedScanSettingsActivity.this, 0, intent, 0);
									alarm.setRepeating(AlarmManager.RTC_WAKEUP,  c.getTimeInMillis(),
											((newParams.getSss_repeat() != 7) ? AlarmManager.INTERVAL_DAY * 7 : AlarmManager.INTERVAL_DAY)
											, pendingIntent);
									
									
								} catch(Exception e){
									
								}
								
								
								
								timeLabel.setText( (hour > 12) ? ((hour-12) + " : " + minute + " PM") : (hour + " : " + minute + " AM"));
								
								Toast.makeText(SubMenuSchedScanSettingsActivity.this, "Time was set to " + hour + " : " + minute
										, Toast.LENGTH_LONG).show();
								
							}}
				
				
					, hour, minute,false).show();
				 
			}
			
			
			
		});
		
		timeLabel = (TextView) findViewById(R.id.sssUpdateLabel1);
		timeLabel.setText( ( ((SharpFixApplicationClass) getApplication()).getServiceHour() > 12) ? 
				((((SharpFixApplicationClass) getApplication()).getServiceHour()-12) + " : " + ((SharpFixApplicationClass) getApplication()).getServiceMin() + " PM" )
				: (((SharpFixApplicationClass) getApplication()).getServiceHour() + " : " + ((SharpFixApplicationClass) getApplication()).getServiceMin() + " AM"));
		
		timeLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timeSelection.performClick();
			}
			
		});
		
		
		repeatSelection = (TextView) findViewById(R.id.sssUpdateSelection2);
		
		repeatLabel =  (TextView) findViewById(R.id.sssUpdateLabel2);
		
		
		chUpd = (CheckBox) findViewById(R.id.sssUpdateSelection3);
		updLabel =  (TextView) findViewById(R.id.sssUpdateLabel3);
		
		chAlert = (CheckBox) findViewById(R.id.sssUpdateSelection4);
		alertLabel = (TextView) findViewById(R.id.sssUpdateLabel4);
		 
		 
		 
		 
		
		
if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
			
		}else{
			chUpd = AndroidLayoutUtils.fixCheckBoxPaddingLeft(this, chUpd,50.0f);
			chAlert = AndroidLayoutUtils.fixCheckBoxPaddingLeft(this, chAlert,50.0f);
		}
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
		/*
		try{
			if( ((SharpFixApplicationClass) getApplication()).getServiceSwitch() == 0){
				chSchedScan.setChecked(false);
				
			}else{
				chSchedScan.setChecked(true);
			}
			if( ((SharpFixApplicationClass) getApplication()).getAuSwitch() == 0){
				chAutoUpd.setChecked(false);
				
			}else{
				chAutoUpd.setChecked(true);
			}
		}catch(Exception e){
			
		}
		*/
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
	
}
