package tk.idclxvii.sharpfixandroid;

import tk.idclxvii.sharpfixandroid.utils.AndroidLayoutUtils;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

public class SubMenuSchedScanSettingsActivity extends Activity {

	
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	SQLiteHelper db;
	
	// layout fields
	CheckBox  chUpd, chAlert;
	TextView title, timeSelection, timeLabel, repeatSelection, repeatLabel, updLabel, alertLabel;
	
	
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
		
		 
		timeSelection = (TextView) findViewById(R.id.sssUpdateSelection1);
		
		timeLabel = (TextView) findViewById(R.id.sssUpdateLabel1);
			
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
