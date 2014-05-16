package com.freetzi.idclxvii.sharpfixandroid;

import java.util.Locale;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.*;
import android.widget.*;
import com.freetzi.idclxvii.sharpfixandroid.databasemodel.*;

public class MainMenuActivity extends Activity implements OnClickListener{
	
	
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	
	private TextView title;
	private TextView fdds;
	private TextView fddl;
	private TextView fds;
	private TextView fdl;
	private TextView filters;
	private TextView filtersl;
	private TextView services;
	private TextView servicesl;
	private TextView abouts;
	private TextView aboutl;
	
	private SQLiteHelper db;
	private long backPressed = 0;

	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	  }
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getDb(getApplicationContext());
		setContentView(R.layout.main_menu);
		this.TAG = this.getClass().getName().replace(this.getPackageName(), "");
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onCreate()");
		}
		
		title = (TextView)findViewById(R.id.title);
		title.setText(title.getText().toString().toUpperCase(Locale.getDefault()));
		
		fdds = (TextView) findViewById(R.id.selection1);
		fdds.setOnClickListener(this);
		fddl = (TextView)findViewById(R.id.label1);
		fddl.setOnClickListener(this);
		
		fds = (TextView) findViewById(R.id.selection2);
		fds.setOnClickListener(this);
		fdl = (TextView) findViewById(R.id.label2);
		fdl.setOnClickListener(this);
		
		filters = (TextView) findViewById(R.id.selection3);
		filters.setOnClickListener(this);
		filtersl = (TextView) findViewById(R.id.label3);
		filtersl.setOnClickListener(this);
		
		services = (TextView) findViewById(R.id.selection4);
		services.setOnClickListener(this);
		servicesl = (TextView) findViewById(R.id.label4);
		servicesl.setOnClickListener(this);
		
		abouts = (TextView) findViewById(R.id.selection5);
		abouts.setOnClickListener(this);
		aboutl = (TextView) findViewById(R.id.label5);
		aboutl.setOnClickListener(this);
		
		
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onStart()");
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onResume()");
		}
		
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG + "onPause()");
		}
	}
	
	@Override
	public void onStop(){
		//setResult(((SharpFixApplicationClass)getApplication()).getAutoLogin());
		super.onStop();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG + "onStop()");
		}
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG + "onRestart()");
		}
	}
	
	@Override
	public void onDestroy(){
		//setResult(((SharpFixApplicationClass)getApplication()).getAutoLogin());
		super.onDestroy();
		this.db.close();
		if(LOGCAT){
			Log.d(TAG, this.TAG + " onDestroy()");
		}
		
	}
	
	
	@Override
	public void onBackPressed(){
		if (this.backPressed + 2000 > System.currentTimeMillis()){
			setResult(((SharpFixApplicationClass)getApplication()).getAutoLogin());
			finish();
		}else{
			Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
		}
        this.backPressed = System.currentTimeMillis();
        if(LOGCAT){
			Log.d(TAG, this.TAG + " onBackPressed()");
		}
	}
	
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu){
		  MenuInflater mi = getMenuInflater();
		  mi.inflate(R.menu.main, menu);
		  if(LOGCAT){
				Log.d(TAG, this.TAG + " onCreateOptionsMenu()");
			}
		  return true;
	  }
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item){
		  // handle item selection:
		  switch (item.getItemId()){
		  
		  
		  case R.id.MenuLogout:
			// call method / do task.
			  ModelPreferences oldParams = new ModelPreferences( ((SharpFixApplicationClass) getApplication()).getAccountId(),
					  ((SharpFixApplicationClass) getApplication()).getFddSwitch(),
					  ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
					  ((SharpFixApplicationClass) getApplication()).getFilterSwitch(),
					  ((SharpFixApplicationClass) getApplication()).getFddPref(),
					  ((SharpFixApplicationClass) getApplication()).getAutoLogin());
			  ModelPreferences newParams = new ModelPreferences( ((SharpFixApplicationClass) getApplication()).getAccountId(),
					  ((SharpFixApplicationClass) getApplication()).getFddSwitch(),
					  ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
					  ((SharpFixApplicationClass) getApplication()).getFilterSwitch(),
					  ((SharpFixApplicationClass) getApplication()).getFddPref(),
					  ((SharpFixApplicationClass) getApplication()).getAutoLogin());
			  newParams.setAuto_login(0);
			  try{
				  this.db.update(Tables.preferences, oldParams, newParams, null);
				  ((SharpFixApplicationClass) getApplication()).updatePreferences(this.db);
				  
			  }catch(Exception e){}
			  Intent i = new Intent(this,MainActivity.class);
			  i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			  startActivity(i);
			  finish();
			  return true;
			  
		  default:
			  return super.onOptionsItemSelected(item);
				  
		  }
	  }
	
	public void onClick(View src) {
		switch (src.getId()) {
	    
		
		// fdd
		case R.id.selection1:
			startActivity(new Intent(this, SubMenuFddActivity.class));
			break;
			
		case R.id.label1:
			fdds.setPressed(true);
			fdds.performClick();
			break;
			
		// fd
		case R.id.selection2:
			startActivity(new Intent(this, SubMenuFdActivity.class));
			break;
		
		case R.id.label2:
			fds.setPressed(true);
			fds.performClick();
			break;
		// filters
		case R.id.selection3:
			startActivity(new Intent(this, SubMenuFiltersActivity.class));
			break;
		
			
			
		case R.id.label3:
			filters.setPressed(true);
			filters.performClick();
			break;
			
		// services
		case R.id.selection4:
			startActivity(new Intent(this, SubMenuServicesActivity.class));
			break;
			
		case R.id.label4:
			services.setPressed(true);
			services.performClick();
			break;



		// about
		case R.id.selection5:
			startActivity(new Intent(this, SubMenuAboutActivity.class));
			break;
			
		case R.id.label5:
			abouts.setPressed(true);
			abouts.performClick();
			break;
				
				
		}
	}
}
