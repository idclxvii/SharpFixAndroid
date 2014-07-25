package tk.idclxvii.sharpfixandroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm extends BroadcastReceiver {

	// Service alarm, service is started here
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.i("ALARM", "Received Alarm Trigger!");
		Log.i("ALARM", "Creating Directory Scanner");
		
		new GlobalAsyncTask<Void, Void, Void>(){

			
			@Override
			protected Void doTask(Void... params) throws Exception {
				// TODO Auto-generated method stub
				context.startService(new Intent(context,DirectoryScanner.class));
				return null;
			}

			@Override
			protected void onException(Exception e) {
				// TODO Auto-generated method stub
				
			}
		}.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
		
		
		
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
		
		
	}
		
		
		
		


}
