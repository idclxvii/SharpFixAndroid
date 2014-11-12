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
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		final Intent i = intent;
		Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Received Alarm Trigger! " 
		 + (Calendar.getInstance()).get(Calendar.HOUR) + " : " +
		 (Calendar.getInstance()).get(Calendar.MINUTE));

		final SQLiteHelper db = new SQLiteHelper(context);
		
		new GlobalAsyncTask<Void, Void, Void>(){

			
			@Override
			protected Void doTask(Void... params) throws Exception {
				// TODO Auto-generated method stub
				Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
				Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Alarm Set at " +
						+ (((SharpFixApplicationClass)context.getApplicationContext()).getServiceHour() > 12 ? 
								((SharpFixApplicationClass)context.getApplicationContext()).getServiceHour() -12
								: ((SharpFixApplicationClass)context.getApplicationContext()).getServiceHour()) + " : " +
						((SharpFixApplicationClass)context.getApplicationContext()).getServiceMin() + " " 
						+ (((SharpFixApplicationClass)context.getApplicationContext()).getServiceAMPM() == 0 ? "AM" : "PM"));
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
					Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
					Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking intent . . .");
					if(i.hasExtra("start") && i.hasExtra("lapse")){
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Intent received from BootReceiver");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
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
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Intent received from SYSTEM");
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(System.currentTimeMillis());
						c.set(Calendar.HOUR, ((SharpFixApplicationClass)context.getApplicationContext()).getServiceHour());
						c.set(Calendar.MINUTE, ((SharpFixApplicationClass)context.getApplicationContext()).getServiceMin());
						
						Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
						Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking Service repetition . . .");
						if(((SharpFixApplicationClass)context.getApplicationContext()).getServiceRepeat() != 7){
							// not everyday
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Service repetition: Once a week, every " 
									+ days[((SharpFixApplicationClass)context.getApplicationContext()).getServiceRepeat()] );
							
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking current day . .. ");
							if(c.get(Calendar.DAY_OF_WEEK) == 
									((SharpFixApplicationClass)context.getApplicationContext()).getServiceRepeat()+1){
								// today is the day!
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Today is " 
										+ days[((SharpFixApplicationClass)context.getApplicationContext()).getServiceRepeat() ] + "!" );
								long current = System.currentTimeMillis();
								long set = c.getTimeInMillis();
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking Time Lapse according to Application Context . . .");
								
								
								if((current - set) > 900000){
									Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse more than 15 minutes!");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + (current - set) +" ms");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (current - set)/1000 +" seconds");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm",  ((current - set)/1000)/60 +" minutes");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "DirectoryScanner Service execution has been prevented!");
								}else{
									Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse was not more than 15 minutes!");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + (current - set) +" ms");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (current - set)/1000 +" seconds");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm",  ((current - set)/1000)/60 +" minutes");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
									Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Starting DirectoryScanner Service!!");
									context.startService(new Intent(context,DirectoryScanner.class));
								}
							}else{
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Today is not " 
										+ days[((SharpFixApplicationClass)context.getApplicationContext()).getServiceRepeat()] + "!" );
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "DirectoryScanner Service execution has been prevented!");
							}
						}else{
							// everyday
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Service repetition: Everyday");
							long current = System.currentTimeMillis();
							long set = c.getTimeInMillis();
							Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
							Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Checking Time Lapse according to Application Context . . .");
							
							
							if((current - set) > 900000){
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse more than 15 minutes!");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + (current - set) +" ms");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (current - set)/1000 +" seconds");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  ((current - set)/1000)/60 +" minutes");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "DirectoryScanner Service execution has been prevented!");
							}else{
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse was not more than 15 minutes!");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Time lapse detected: " + (current - set) +" ms");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  (current - set)/1000 +" seconds");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm",  ((current - set)/1000)/60 +" minutes");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm","#######################################################");
								Log.i("tk.idclxvii.sharpfixandroid.Alarm", "Starting DirectoryScanner Service!!");
								context.startService(new Intent(context,DirectoryScanner.class));
							}
						}
						
						//context.startService(new Intent(context,DirectoryScanner.class));
					}
					
					
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
