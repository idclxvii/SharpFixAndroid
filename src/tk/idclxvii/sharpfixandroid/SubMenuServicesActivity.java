package tk.idclxvii.sharpfixandroid;

import tk.idclxvii.sharpfixandroid.utils.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;

public class SubMenuServicesActivity extends Activity {

	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	SQLiteHelper db;
	
	// layout fields
	CheckBox  chSchedScan, chAutoUpd;
	TextView schedScanLabel, schedScanSettings, schedScanSettingsLabel, autoUpdLabel;
	
	
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
		
		setContentView(R.layout.services_sub_menu);
		
		chSchedScan = (CheckBox) findViewById(R.id.services_selection1);
		schedScanLabel = (TextView) findViewById(R.id.services_label1);
		schedScanSettings = (TextView) findViewById(R.id.services_selection2);
		schedScanSettingsLabel = (TextView) findViewById(R.id.services_label2);
		chAutoUpd = (CheckBox) findViewById(R.id.services_selection3);
		autoUpdLabel = (TextView) findViewById(R.id.services_label3);
		
		
		
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
			
		}else{
			chSchedScan = AndroidLayoutUtils.fixCheckBoxPaddingLeft(this, chSchedScan,30.0f);
			chAutoUpd = AndroidLayoutUtils.fixCheckBoxPaddingLeft(this, chAutoUpd,18.0f);
		}
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
