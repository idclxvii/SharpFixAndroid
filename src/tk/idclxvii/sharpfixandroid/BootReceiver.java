package tk.idclxvii.sharpfixandroid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.*;
import android.content.*;
import android.util.Log;
import tk.idclxvii.sharpfixandroid.databasemodel.*;
import tk.idclxvii.sharpfixandroid.utils.AndroidUtils;
import tk.idclxvii.sharpfixandroid.utils.FileProperties;

public class BootReceiver extends BroadcastReceiver {

	private final String TAG = this.getClass().getName();
	List<String> logs =  new ArrayList<String>();
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		
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
					//Log.i("BootReceiver", "Awaiting debugger . . .");
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(System.currentTimeMillis());
					
					ModelPreferences mp = (ModelPreferences) db.selectAll(Tables.preferences, ModelPreferences.class, null)[0];
					c.set(Calendar.HOUR_OF_DAY, mp.getSss_hh());
					c.set(Calendar.MINUTE, mp.getSss_mm());
					
					/*
					c.set(Calendar.AM_PM, (mp.getSss_ampm()) == 0 ? Calendar.AM : Calendar.PM);
					switch(mp.getSss_repeat()+1 ){
					
					case 1 :
						c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
						break;
					case 2 :
						c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
						break;
					case 3 :
						c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
						break;
					case 4 :
						c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
						break;
					case 5 :
						c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
						break;
					case 6 :
						c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
						break;
					case 7 :
						c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
						break;
					default :
						// do nothing, use system's current day settings
						break;
					}
					*/
					
					/*
					if(mp.getSss_repeat() != 7){
						c.set(Calendar.DAY_OF_WEEK, mp.getSss_repeat()+1);
					}
					*/
					AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(context, Alarm.class);
					
					/*
					long current = System.currentTimeMillis();
					long set = c.getTimeInMillis();
										
					if((current - set) > 900000 || (current - set) < 0){ // if more than 15 minutes has passed, then scan wont initialize immediately
						intent.putExtra("lapse", (current - set) );
						intent.putExtra("start", false);
					}else{
						intent.putExtra("lapse", (current - set) );
						intent.putExtra("start", true);
					}
					*/
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
