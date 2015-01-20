/*
 * BootReceiver.java
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
import android.app.*;
import android.content.*;
import tk.idclxvii.sharpfixandroid.databasemodel.*;
import tk.idclxvii.sharpfixandroid.utils.AndroidUtils;
import tk.idclxvii.sharpfixandroid.utils.FileProperties;


/**
 * This class is a sub class of {@link BroadcastReceiver} receiving broadcasts from
 * the mobile device and waits for the intent {@link android.intent.action.BOOT_COMPLETED}
 * before it starts its tasks
 * <br />
 * <br />
 * Basically, this class sets the Alarm of the Services whenever the mobile device completed
 * booting. All alarms get reset when the device is turned off, so the solution for that is
 * to re-set the user preferred time of Alarm whenever the device has successfully completed
 * booting.
 * 
 * @version 1.1.2 Alpha Release Version
 * @author Magarzo, Randolf Josef V.
 *
 */
public class BootReceiver extends BroadcastReceiver {

	/**
	 * The TAG to be used by {@link android.util.Log}
	 * when performing {@code Logcat} operations.
	 */
	private final String TAG = this.getClass().getSimpleName();
	
	/**
	 * The List of Strings containing the logged operations
	 * @see {@link AndroidUtils#logProgressReport(Context, String[])}
	 */
	private List<String> logs =  new ArrayList<String>();
	
	
	@Override
	public void onReceive(final Context context, Intent intent) {
	
		final SQLiteHelper db = new SQLiteHelper(context);
		
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			logs.add(AndroidUtils.convertMillis(System.currentTimeMillis())+ TAG + 
					": Boot completed at: " + FileProperties.formatFileLastMod(System.currentTimeMillis()));
		
			// Set the alarm here.
	    	new GlobalAsyncTask<Void, Void, Void>(){
	
				@Override
				protected Void doTask(Void... params) throws Exception {
					// TODO Auto-generated method stub
					
					//android.os.Debug.waitForDebugger();
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(System.currentTimeMillis());
					
					ModelPreferences mp = (ModelPreferences) db.selectAll(Tables.preferences, ModelPreferences.class, null)[0];
					c.set(Calendar.HOUR_OF_DAY, mp.getSss_hh());
					c.set(Calendar.MINUTE, mp.getSss_mm());
					AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(context, Alarm.class);
					PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
					
					alarm.setRepeating(AlarmManager.RTC_WAKEUP,  c.getTimeInMillis(),
							/*((mp.getSss_repeat() != 7) ? AlarmManager.INTERVAL_DAY * 7 :*/ AlarmManager.INTERVAL_DAY
							, pendingIntent);
					logs.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + TAG + ": Setting CPU Alarm at " +
							mp.getSss_hh() + " : " + mp.getSss_mm());
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

}
