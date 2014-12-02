package tk.idclxvii.sharpfixandroid;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import tk.idclxvii.sharpfixandroid.databasemodel.ModelPreferences;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import tk.idclxvii.sharpfixandroid.utils.AndroidLayoutUtils;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	private int hour, minute, ampm;
	private final String days[] = new String[] {"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ,"Everyday"};
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
		ampm = c.get(Calendar.AM_PM);
		
		timeSelection = (TextView) findViewById(R.id.sssUpdateSelection1);
		timeSelection.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new TimePickerDialog(SubMenuSchedScanSettingsActivity.this,
						new TimePickerDialog.OnTimeSetListener(){
							@Override
							public void onTimeSet(TimePicker view, int selectedHour,int selectedMinute) {
								// throws 24 hours counting
								hour = selectedHour;
								//ampm = 0; // am = 0, pm = 1
								
								if(selectedHour == 0){
									// 12:00 AM
									//hour = 12;
									ampm = 0;
								}else{
									ampm = ((selectedHour > 11) ?  1 : 0);
									//hour = ((selectedHour > 12) ?  selectedHour - 12 : selectedHour);
									
								}
								minute = selectedMinute;
					 
								// set current time into textview
								
								// set current time into timepicker
								
								
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
								newParams.setSss_hh(hour);
								newParams.setSss_mm(minute);
								newParams.setSss_ampm(ampm);
								
								// ##################################################################################
								// temporary everyday alarm
								//newParams.setSss_repeat(7);
								// ##################################################################################
								
								
								try {
									db.update(Tables.preferences,oldParams, newParams,null);
									SF.updatePreferences(db);
									Calendar c = Calendar.getInstance();
									c.setTimeInMillis(System.currentTimeMillis());
									c.set(Calendar.HOUR_OF_DAY, hour);
									c.set(Calendar.MINUTE,minute);
									//c.set(Calendar.AM_PM, (ampm == 0 ? Calendar.AM : Calendar.PM));
									
									AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
									Intent intent = new Intent(SubMenuSchedScanSettingsActivity.this, Alarm.class);
									PendingIntent pendingIntent = PendingIntent.getBroadcast(SubMenuSchedScanSettingsActivity.this, 0, intent, 0);
									alarm.setRepeating(AlarmManager.RTC_WAKEUP,  c.getTimeInMillis(),
											
											//{"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ,"Everyday"};
											
											/*((newParams.getSss_repeat() != 7) ? AlarmManager.INTERVAL_DAY * 7 : */AlarmManager.INTERVAL_DAY
											, pendingIntent);
									
									
								} catch(Exception e){
									
								}
								
								
								
								timeLabel.setText((hour == 0 ? "12" : (hour > 12 ? hour - 12: hour)) + " : " + minute + (ampm == 0 ? " AM": " PM"));
								
								Toast.makeText(SubMenuSchedScanSettingsActivity.this, "Time was set to " + hour + " : " + minute
										, Toast.LENGTH_LONG).show();
								
							}}
				
				
					, hour, minute,false).show();
				 
			}
			
			
			
		});
		
		timeLabel = (TextView) findViewById(R.id.sssUpdateLabel1);
		timeLabel.setText( (hour == 0 ? "12" : (hour > 12 ? hour - 12: hour)) + " : " + minute + (SF.getServiceAMPM() == 0 ? " AM": " PM"));
		
		timeLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timeSelection.performClick();
			}
			
		});
		
		
		repeatSelection = (TextView) findViewById(R.id.sssUpdateSelection2);
		repeatSelection.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(SubMenuSchedScanSettingsActivity.this)
				.setTitle("Repeat").setSingleChoiceItems(days,SF.getServiceRepeat(),
				           new DialogInterface.OnClickListener() {
								
			        			public void onClick(DialogInterface dialog, int item) {
			        				Log.i(TAG, "Selected Alarm repetition day: " + days[item]);
			        				
			        				if(SF.getServiceRepeat() != item){
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
										newParams.setSss_repeat(item);
										// ##################################################################################
										// temporary everyday alarm
										//newParams.setSss_repeat(7);
										// ##################################################################################
										
										
										try {
											db.update(Tables.preferences,oldParams, newParams,null);
											SF.updatePreferences(db);
											Calendar c = Calendar.getInstance();
											c.setTimeInMillis(System.currentTimeMillis());
											c.set(Calendar.HOUR_OF_DAY, newParams.getSss_hh());
											c.set(Calendar.MINUTE,newParams.getSss_mm());
											//c.set(Calendar.AM_PM, (newParams.getSss_ampm() == 0 ? Calendar.AM : Calendar.PM));
											
											
											AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
											Intent intent = new Intent(SubMenuSchedScanSettingsActivity.this, Alarm.class);
											PendingIntent pendingIntent = PendingIntent.getBroadcast(SubMenuSchedScanSettingsActivity.this, 0, intent, 0);
											alarm.setRepeating(AlarmManager.RTC_WAKEUP,  c.getTimeInMillis(),
													/*((newParams.getSss_repeat() != 7) ? AlarmManager.INTERVAL_DAY * 7 : */AlarmManager.INTERVAL_DAY
													, pendingIntent);
											
										} catch(Exception e){
											
										}
										
			        				}else{
			        					// do nothing, user did not change any setting
			        				}
			        				
			        				repeatLabel.setText(days[SF.getServiceRepeat()]);
			        				dialog.cancel();
			        			}

			    				}).create().show();
				
			}
			
			
			
		});
		
		
		
		repeatLabel =  (TextView) findViewById(R.id.sssUpdateLabel2);
		repeatLabel.setText(days[SF.getServiceRepeat()]);
		repeatLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				repeatSelection.performClick();
			}
			
		});
		
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
		
		timeLabel.setText(( SF.getServiceHour() == 0 ? "12" : ( SF.getServiceHour() > 12 ?  SF.getServiceHour() - 12: SF.getServiceHour()))
				+ " : " + SF.getServiceMin() + (SF.getServiceAMPM() == 0 ? " AM": " PM"));
		repeatLabel.setText(days[SF.getServiceRepeat()]);
		//  CheckBox  chUpd, chAlert;
		try{
			if( SF.getServiceUpdateSwitch() == 0){
				chUpd.setChecked(false);
				
			}else{
				chUpd.setChecked(true);
			}
			if( SF.getServiceNoti() == 0){
				chAlert.setChecked(false);
				
			}else{
				chAlert.setChecked(true);
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
		timeLabel.setText(( SF.getServiceHour() == 0 ? "12" : ( SF.getServiceHour() > 12 ?  SF.getServiceHour() - 12: SF.getServiceHour()))
				+ " : " + SF.getServiceMin() + (SF.getServiceAMPM() == 0 ? " AM": " PM"));
		repeatLabel.setText(days[SF.getServiceRepeat()]);

		// CheckBox  chUpd, chAlert;
		// ###############################################################################################################
		 		if((chUpd.isChecked() && SF.getServiceUpdateSwitch() == 0 ) ||
						(!chUpd.isChecked() && SF.getServiceUpdateSwitch() == 1)){
					// update database 
					if(chUpd.isChecked()){
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
							newParams.setSss_update(1);
							
							this.db.update(Tables.preferences, oldParams, newParams, null);
							SF.updatePreferences(db);
							
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
							newParams.setSss_update(0);
							this.db.update(Tables.preferences, oldParams, newParams, null);
							SF.updatePreferences(db);
						}catch(Exception e){
								
						}
							
					}
					
				}else{
					// Toast.makeText(this, "File Duplication Detection Filtering Settings was not changed" ,Toast.LENGTH_LONG).show();
				}
		 		
		 		if((chAlert.isChecked() && SF.getServiceNoti() == 0 ) ||
						(!chAlert.isChecked() && SF.getServiceNoti() == 1)){
					// update database 
					if(chAlert.isChecked()){
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
							newParams.setSss_noti(1);
							/*
							Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Designation Settings have been updated!" :
									"File Designation Settings failed to update!" ,Toast.LENGTH_LONG).show();
								*/
							this.db.update(Tables.preferences, oldParams, newParams, null);
							SF.updatePreferences(db);
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
							newParams.setSss_noti(0);
							/*
							Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Designation Settings have been updated!" :
									"File Designation Settings failed to update!" ,Toast.LENGTH_LONG).show();
							*/
							this.db.update(Tables.preferences, oldParams, newParams, null);
							SF.updatePreferences(db);
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
