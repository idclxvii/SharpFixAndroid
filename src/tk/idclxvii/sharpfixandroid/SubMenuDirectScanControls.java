package tk.idclxvii.sharpfixandroid;

import android.app.Activity;

import tk.idclxvii.sharpfixandroid.databasemodel.ModelPreferences;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import tk.idclxvii.sharpfixandroid.utils.*;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SubMenuDirectScanControls extends Activity {

	
	
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	SQLiteHelper db;
		
	// layout fields
	TextView title;
	Button start, stop;
		
	// service Intents
	private Intent dsIntent, fdsIntent, fddsIntent;
		
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
			setContentView(R.layout.direct_scan_controls);
			
			createServiceIntents();
			
			title = (TextView)findViewById(R.id.title);
			title.setText(title.getText().toString().toUpperCase());
			
			
			
			
			
			start = (Button) findViewById(R.id.scan);
			start.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SubMenuDirectScanControls.this.startService(dsIntent);
				}
				
			});
			stop = (Button) findViewById(R.id.stop);
			stop.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SubMenuDirectScanControls.this.stopService(dsIntent);
				}
			
			});
			
			
			
			
			
		
			
			
		}

		
		
		private void createServiceIntents(){
			this.dsIntent = new Intent(this, DirectoryScanner.class);
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
			
		}

		/* (non-Javadoc)
		 * @see android.app.Activity#onPause()
		 */
		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			if(LOGCAT){
				Log.d(TAG, this.getClass().getName() + " onPause()");
			}
			
		}

		/* (non-Javadoc)
		 * @see android.app.Activity#onStop()
		 */
		@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			super.onStop();
			this.db.close();
			if(LOGCAT){
				Log.d(TAG, this.getClass().getName() + " onStop()");
			}
		}

		/* (non-Javadoc)
		 * @see android.app.Activity#onDestroy()
		 */
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			this.db.close();
			if(LOGCAT){
				Log.d(TAG, this.getClass().getName() + " onDestroy()");
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
				  ((SharpFixApplicationClass) getApplication()).resetAll();
				  Intent i = new Intent(this,MainActivity.class);
				  i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				  startActivity(i);
				  finish();
				  return true;
				
				  
			  default:
				  return super.onOptionsItemSelected(item);
					  
			  }
		  }


			
}
