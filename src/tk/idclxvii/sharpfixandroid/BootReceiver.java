package tk.idclxvii.sharpfixandroid;

import java.util.Calendar;

import android.app.*;
import android.content.*;
import android.util.Log;
import tk.idclxvii.sharpfixandroid.databasemodel.*;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		
		final SQLiteHelper db = new SQLiteHelper(context);
		
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
	    
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
					c.set(Calendar.HOUR, mp.getSss_hh());
					c.set(Calendar.MINUTE, mp.getSss_mm());
					
					AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(context, Alarm.class);
					
					long current = System.currentTimeMillis();
					long set = c.getTimeInMillis();
										
					if((current - set) > 900000 || (current - set) < 0){ // if more than 15 minutes has passed, then scan wont initialize immediately
						intent.putExtra("lapse", (current - set) );
						intent.putExtra("start", false);
					}else{
						intent.putExtra("lapse", (current - set) );
						intent.putExtra("start", true);
					}
					
					
					PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
					
					alarm.setRepeating(AlarmManager.RTC_WAKEUP,  c.getTimeInMillis(),
							/*((mp.getSss_repeat() != 7) ? AlarmManager.INTERVAL_DAY * 7 :*/ AlarmManager.INTERVAL_DAY
							, pendingIntent);
					
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
