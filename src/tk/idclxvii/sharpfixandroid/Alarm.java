package tk.idclxvii.sharpfixandroid;

import java.util.Calendar;

import tk.idclxvii.sharpfixandroid.databasemodel.ModelPreferences;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm extends BroadcastReceiver {

	// Service alarm, service is started here
	
	private final String days[] = new String[] {"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ,"Everyday"};
	private SharpFixApplicationClass SF;
	
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		
		// TODO Auto-generated method stub
		final Intent i = intent;
		SF = (SharpFixApplicationClass) context.getApplicationContext();
		Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Received Alarm Trigger! " 
		 + (
				 ((Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 0) ? "12" :
			 	(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 12) ? (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) - 12): Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) +" : " +
		 (Calendar.getInstance()).get(Calendar.MINUTE) + 
		 	((Calendar.getInstance()).get(Calendar.AM_PM) == 0 ? " AM": " PM")));

		final SQLiteHelper db = new SQLiteHelper(context);
		
		new GlobalAsyncTask<Void, Void, Void>(){

			
			@Override
			protected Void doTask(Void... params) throws Exception {
				// TODO Auto-generated method stub
				Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
				Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Alarm Set at " +
						( SF.getServiceHour() == 0 ? "12" : ( SF.getServiceHour() > 12 ?  SF.getServiceHour() - 12: SF.getServiceHour()))
						+ " : " + SF.getServiceMin() + (SF.getServiceAMPM() == 0 ? " AM": " PM"));
				// check if scheduled scan is enabled
				ModelPreferences mp = (ModelPreferences) db.selectAll(Tables.preferences, ModelPreferences.class, null)[0];
				if(mp.getSss_switch() == 1){
					// service switch is turned on
					Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
					Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Service switch is turned on!");
					if(mp.getAu_switch() == 1){
						// auto update switch is turned on
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Auto update switch is turned on!");
						
					}else{
						// auto update switch is turned off
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Auto update switch is turned off!");
					}
					if(mp.getSss_update() == 1){
						// latest file definition update switch is turned on
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Latest file definition update switch is turned on!");
						
					}else{
						// latest file definition update switch is turned off
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Latest file definition update switch is turned off!");
						
					}
					
					
					
					// Manually handle Alarm
					
					Calendar current = Calendar.getInstance();
					current.setTimeInMillis(System.currentTimeMillis());
					
					Calendar set = Calendar.getInstance();
					set.setTimeInMillis(System.currentTimeMillis());
					set.set(Calendar.HOUR_OF_DAY, SF.getServiceHour());
					set.set(Calendar.MINUTE, SF.getServiceMin());
					/*
					set.set(Calendar.AM_PM,
							(SF.getServiceAMPM() == 0 ? 
									Calendar.AM : Calendar.PM));
					switch(mp.getSss_repeat()+1 ){
					
					case 1 :
						set.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
						break;
					case 2 :
						set.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
						break;
					case 3 :
						set.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
						break;
					case 4 :
						set.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
						break;
					case 5 :
						set.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
						break;
					case 6 :
						set.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
						break;
					case 7 :
						set.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
						break;
					default :
						// do nothing, use system's current day settings
						break;
					}
					*/
					
					Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
					Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking Service repetition . . .");
					if(SF.getServiceRepeat() != 7){
						// not everyday
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Service repetition: Once a week, every " 
								+ days[SF.getServiceRepeat()] );
						
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking current day . .. ");
						if(set.get(Calendar.DAY_OF_WEEK) == 
								SF.getServiceRepeat()+1){
							// today is the day!
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Today is " 
									+ days[SF.getServiceRepeat() ] + "!" );
							long currentMillis = current.getTimeInMillis();//.currentTimeMillis();
							long setMillis = set.getTimeInMillis();
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking Time Lapse according to Application Context . . .");
							
							
							if((currentMillis - setMillis) > 900000){
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse more than 15 minutes!");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + (currentMillis - setMillis) +" ms");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (currentMillis - setMillis)/1000 +" seconds");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  ((currentMillis - setMillis)/1000)/60 +" minutes");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "DirectoryScanner Service execution has been prevented!");
							}else{
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse was not more than 15 minutes!");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + (currentMillis - setMillis) +" ms");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (currentMillis - setMillis)/1000 +" seconds");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  ((currentMillis - setMillis)/1000)/60 +" minutes");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Starting DirectoryScanner Service!!");
								context.startService(new Intent(context,DirectoryScanner.class));
							}
						}else{
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Today is not " 
									+ days[SF.getServiceRepeat()] + "!" );
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "DirectoryScanner Service execution has been prevented!");
						}
					}else{
						// everyday
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Service repetition: Everyday");
						long currentMillis = current.getTimeInMillis(); //.currentTimeMillis();
						long setMillis = set.getTimeInMillis();
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking Time Lapse according to Application Context . . .");
						
						
						if((currentMillis - setMillis) > 900000){
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse more than 15 minutes!");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + (currentMillis - setMillis) +" ms");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (currentMillis - setMillis)/1000 +" seconds");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm",  ((currentMillis - setMillis)/1000)/60 +" minutes");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "DirectoryScanner Service execution has been prevented!");
						}else{
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse was not more than 15 minutes!");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + (setMillis -currentMillis) +" ms");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (setMillis -currentMillis)/1000 +" seconds");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm",  ((setMillis -currentMillis)/1000)/60 +" minutes");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Starting DirectoryScanner Service!!");
							context.startService(new Intent(context,DirectoryScanner.class));
						}
					}
					
					
					/*
					
					Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
					Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking intent . . .");
				
					if(i.hasExtra("start") && i.hasExtra("lapse")){
						
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Intent received from BootReceiver");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(System.currentTimeMillis());
						c.set(Calendar.HOUR, SF.getServiceHour());
						c.set(Calendar.MINUTE, SF.getServiceMin());
						c.set(Calendar.AM_PM, SF.getServiceAMPM());
						
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking Service repetition . . .");
						if(SF.getServiceRepeat() != 7){
							// not everyday
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Service repetition: Once a week, every " 
									+ days[SF.getServiceRepeat()] );
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking current day . .. ");
							if(c.get(Calendar.DAY_OF_WEEK) == 
									SF.getServiceRepeat()+1){
								// today is the day!
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Today is " 
										+ days[SF.getServiceRepeat() ] + "!" );
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking Time Lapse according to BootReceiver INTENT . . .");
								if(i.getExtras().getBoolean("start")){
									Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse was not more than 15 minutes!");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + i.getExtras().getLong("lapse") +" ms");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm",  i.getExtras().getLong("lapse") +" ms");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (i.getExtras().getLong("lapse")/1000)/60 +" minutes");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Starting DirectoryScanner Service!!");
									context.startService(new Intent(context,DirectoryScanner.class));
								}else{
									Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse was more than 15 minutes!");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + i.getExtras().getLong("lapse") +" ms");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm",  i.getExtras().getLong("lapse") +" ms");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (i.getExtras().getLong("lapse")/1000)/60 +" minutes");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "DirectoryScanner Service execution has been prevented!");
									
								}
							}else{
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Today is not " 
										+ days[SF.getServiceRepeat()] + "!" );
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "DirectoryScanner Service execution has been prevented!");
							}
						}else{
							// everyday
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Service repetition: Everyday");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking Time Lapse according to BootReceiver INTENT . . .");
							if(i.getExtras().getBoolean("start")){
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse was not more than 15 minutes!");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + i.getExtras().getLong("lapse") +" ms");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  i.getExtras().getLong("lapse") +" ms");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (i.getExtras().getLong("lapse")/1000)/60 +" minutes");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Starting DirectoryScanner Service!!");
								context.startService(new Intent(context,DirectoryScanner.class));
							}else{
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse was more than 15 minutes!");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + i.getExtras().getLong("lapse") +" ms");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  i.getExtras().getLong("lapse") +" ms");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (i.getExtras().getLong("lapse")/1000)/60 +" minutes");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "DirectoryScanner Service execution has been prevented!");
								
							}
						
						/*
						
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking Time Lapse . . .");
						if(i.getExtras().getBoolean("start")){
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse was not more than 15 minutes!");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + i.getExtras().getLong("lapse") +" ms");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm",  i.getExtras().getLong("lapse") +" ms");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (i.getExtras().getLong("lapse")/1000)/60 +" minutes");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Starting DirectoryScanner Service!!");
							context.startService(new Intent(context,DirectoryScanner.class));
						}else{
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse was more than 15 minutes!");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + i.getExtras().getLong("lapse") +" ms");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm",  i.getExtras().getLong("lapse") +" ms");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (i.getExtras().getLong("lapse")/1000)/60 +" minutes");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "DirectoryScanner Service execution has been prevented!");
							
							
							
							
						}
					}else{
						
						
						//context.startService(new Intent(context,DirectoryScanner.class));
					}
					*/
					
				}else{
					// service switch is turned on
					Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
					Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Service switch is turned off!");
				}
				
				return null;
			}

			@Override
			protected void onException(Exception e) {
				// TODO Auto-generated method stub
				
			}
		}.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
		
		
		/*
		new GlobalAsyncTask<Void, Void, Void>(){
	
			
			@Override
			protected Void doTask(Void... params) throws Exception {
				// TODO Auto-generated method stub
				
				return null;
			}

			@Override
			protected void onException(Exception e) {
				// TODO Auto-generated method stub
				
			}
		}.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
		*/
		
	}
		
		
		
		


}
