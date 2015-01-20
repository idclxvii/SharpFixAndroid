/*
 * Alarm.java
 * 1.1.2 Alpha Release Version
* 
 * Magarzo, Randolf Josef V.

 * Copyright (c) 2013 Magarzo, Randolf Josef V.
 * Project SharpFix Android
 * 
 * SHARPFIX ANDROID FILE MANAGEMENT UTILITY 2014 - 2015 
 * Area of Computer Science College of Accountancy, 
 * Business Administration and Computer Studies
 * San Sebastian College - Recoletos, Manila, Philippines
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. 
 * If not, see http://www.gnu.org/licenses
 * 
 */

package tk.idclxvii.sharpfixandroid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import tk.idclxvii.sharpfixandroid.databasemodel.ModelPreferences;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import tk.idclxvii.sharpfixandroid.utils.AndroidUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


 /**
  * This class is a sub class of {@link BroadcastReceiver} receiving requests
  * from several activities, which acts as the starting point of Scan services.
  * This class is called whenever the time or trigger has been met. However,
  * this class only handles the service initialization process, avoiding unnecessary
  * scans.
  * 
  * @version 1.1.2 Alpha Release Version
  * @author Magarzo, Randolf Josef V.
  *
  */

public class Alarm extends BroadcastReceiver {

	/**
	 * The TAG to be used by {@link android.util.Log}
	 * when performing {@code Logcat} operations.
	 */
	private final String TAG = this.getClass().getSimpleName();
	
	/**
	 * An array of String that specifies the days of the week,
	 * adding "Everyday", serving as options in Repeat Scan Settings
	 */
	private final String days[] = new String[] {"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" ,"Everyday"};
	
	/**
	 * The List of Strings containing the logged operations
	 * @see {@link AndroidUtils#logProgressReport(Context, String[])}
	 */
	private List<String> logs =  new ArrayList<String>();
	
	/**
	 * The Application Context, containing the recently
	 * loaded user preferences and application settings
	 * on global context.
	 * @see {@link Context#getApplicationContext()}
	 */
	private SharpFixApplicationClass SF;
	
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		
		final SQLiteHelper db = new SQLiteHelper(context);
		
		SF = (SharpFixApplicationClass) context.getApplicationContext();
		logs.add(AndroidUtils.convertMillis(System.currentTimeMillis())+ TAG + ": Alarm invoked!");
		
		new GlobalAsyncTask<Void, Void, Void>(){
			@Override
			protected Void doTask(Void... params) throws Exception {
				logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Alarm Set at " +
						( SF.getServiceHour() == 0 ? "12" : ( SF.getServiceHour() > 12 ?  SF.getServiceHour() - 12: SF.getServiceHour()))
						+ " : " + SF.getServiceMin() + (SF.getServiceAMPM() == 0 ? " AM": " PM"));
				// check if scheduled scan is enabled
				ModelPreferences mp = (ModelPreferences) db.selectAll(Tables.preferences, ModelPreferences.class, null)[0];
				if(mp.getSss_switch() == 1){
					// service switch is turned on
					logs.add(AndroidUtils.convertMillis(System.currentTimeMillis())+ TAG +  ": Service switch is turned on!");
					if(mp.getAu_switch() == 1){
						// auto update switch is turned on
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Auto update switch is turned on!");
					}else{
						// auto update switch is turned off
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Auto update switch is turned off!");
					}
					if(mp.getSss_update() == 1){
						// latest file definition update switch is turned on
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Latest file definition update switch is turned on!");
					}else{
						// latest file definition update switch is turned off
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Latest file definition update switch is turned off!");
					}
					
					// Manually handle Alarm
					Calendar current = Calendar.getInstance();
					current.setTimeInMillis(System.currentTimeMillis());
					Calendar set = Calendar.getInstance();
					set.setTimeInMillis(System.currentTimeMillis());
					set.set(Calendar.HOUR_OF_DAY, SF.getServiceHour());
					set.set(Calendar.MINUTE, SF.getServiceMin());
				
					logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Checking Service repetition . . .!");
					
					if(SF.getServiceRepeat() != 7){
						// not everyday
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Service repetition: Once a week, every "
						+ days[SF.getServiceRepeat()]);
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Checking current day . . . ");
						if(set.get(Calendar.DAY_OF_WEEK) == 
								SF.getServiceRepeat()+1){
							// today is the day!
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Today is " 
									+ days[SF.getServiceRepeat() ] + "!");
							long currentMillis = current.getTimeInMillis();//.currentTimeMillis();
							long setMillis = set.getTimeInMillis();
							
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +": Checking Time Lapse according to Application Context . . .");
							if((currentMillis - setMillis) > 900000){
								logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +  ": Time lapse is more than 15 minutes!\n"+
										"Time lapse detected: " + (currentMillis - setMillis) +" ms\n" + 
										 (currentMillis - setMillis)/1000 +" seconds\n" +
										 ((currentMillis - setMillis)/1000)/60 +" minutes\n" + 
										 "DirectoryScanner Service execution has been prevented!");
							}else{
					
								logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) +  TAG + ": Time lapse is not more than 15 minutes!\n" +
										"Time lapse detected: " + (currentMillis - setMillis) +" ms\n" + 
										(currentMillis - setMillis)/1000 +" seconds\n" + 
										((currentMillis - setMillis)/1000)/60 +" minutes\n" +
										"Starting DirectoryScanner Service!!");
								context.startService(new Intent(context,DirectoryScanner.class));
							}
						}else{
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +  ": Today is not " 
									+ days[SF.getServiceRepeat()] + "!");
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": DirectoryScanner Service execution has been prevented!");
						}
					}else{
						// everyday
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Service repetition: Everyday");
						long currentMillis = current.getTimeInMillis(); //.currentTimeMillis();
						long setMillis = set.getTimeInMillis();
						logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Checking Time Lapse according to Application Context . . .");
						
						if((currentMillis - setMillis) > 900000){
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG +  ": Time lapse is more than 15 minutes!\n"+
									"Time lapse detected: " + (currentMillis - setMillis) +" ms\n" + 
									 (currentMillis - setMillis)/1000 +" seconds\n" +
									 ((currentMillis - setMillis)/1000)/60 +" minutes\n" + 
									 "DirectoryScanner Service execution has been prevented!");
						}else{
							logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) +  TAG + ": Time lapse is not more than 15 minutes!\n" +
									"Time lapse detected: " + (currentMillis - setMillis) +" ms\n" + 
									(currentMillis - setMillis)/1000 +" seconds\n" + 
									((currentMillis - setMillis)/1000)/60 +" minutes\n" +
									"Starting DirectoryScanner Service!!");
							context.startService(new Intent(context,DirectoryScanner.class));
						}
					}
					
					
					
					
				}else{
					// service switch is turned on
					logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Service switch is turned off!");
				}
				
				AndroidUtils.logProgressReport(context, logs.toArray(new String[logs.size()]));
				return null;
			}

			@Override
			protected void onException(Exception e) {
				// TODO Auto-generated method stub
				
			}
		}.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
