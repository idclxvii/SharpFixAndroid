package tk.idclxvii.sharpfixandroid;

import tk.idclxvii.sharpfixandroid.databasemodel.ModelPreferences;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.View.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.*;

public class SubMenuFddActivity extends Activity implements OnClickListener, OnCheckedChangeListener{

	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	
	TextView title;
	CheckBox ch;
	TextView chl;
	RadioGroup rg;
	RadioButton older;
	RadioButton newer;
	TextView label;
	TextView selection;
	SQLiteHelper db;
	
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.TAG = this.getClass().getName().replace(this.getPackageName(), "");
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onCreate()");
		}
		
		setContentView(R.layout.fdd_sub_menu);
		title = (TextView)findViewById(R.id.title);
		title.setText(title.getText().toString().toUpperCase());
		
		ch = (CheckBox) findViewById(R.id.selection1);
		ch.setOnClickListener(this);
		ch.setOnCheckedChangeListener(this);
		chl =  (TextView) findViewById(R.id.label1);
		chl.setOnClickListener(this);
		rg =  (RadioGroup) findViewById(R.id.deletionPriority);
		older = (RadioButton) findViewById(R.id.radioOlder);
		newer = (RadioButton) findViewById(R.id.radioNewer);
		selection = (TextView) findViewById(R.id.selection2);
		selection.setOnClickListener(this);
		label = (TextView) findViewById(R.id.label2);
		label.setOnClickListener(this);
		
		this.db = this.getDb(getApplicationContext());
		checkChanged(0);
	}

	@Override
	public void onPause(){
		super.onPause();
		if((ch.isChecked() && ((SharpFixApplicationClass) getApplication()).getFddSwitch() == 0 ) ||
				(!ch.isChecked() && ((SharpFixApplicationClass) getApplication()).getFddSwitch() == 1)){
			// update database 
			if(ch.isChecked()){
				//	File Duplication Detection Features is turned on
				try{
					ModelPreferences newParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
					ModelPreferences oldParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
					newParams.setFdd_switch(1);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Duplication Detection Settings have been updated!" :
							"File Duplication Detection Settings failed to update!" ,Toast.LENGTH_LONG).show();
					*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					((SharpFixApplicationClass) getApplication()).setFddSwitch(1);
				}catch(Exception e){
						
				}
			}else{
				// File Duplication Detection Features is turned off
				try{
					ModelPreferences newParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
					ModelPreferences oldParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
					newParams.setFdd_switch(0);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Duplication Detection Settings have been updated!" :
							"File Duplication Detection Settings failed to update!" ,Toast.LENGTH_LONG).show();
					*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					((SharpFixApplicationClass) getApplication()).setFddSwitch(0);
				}catch(Exception e){
						
				}
					
			}

		}else{
			// Toast.makeText(this, "File Duplication Detection Settings was not changed" ,Toast.LENGTH_LONG).show();
		}
		
		if((newer.isChecked() && ((SharpFixApplicationClass) getApplication()).getFddPref() == 0 ) ||
				(!newer.isChecked() && ((SharpFixApplicationClass) getApplication()).getFddPref() == 1)){
			// update database 
			if(newer.isChecked()){
				//	Duplicate Files Deletion Priority deletes newer files
				try{
					ModelPreferences newParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
					ModelPreferences oldParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
					newParams.setFdd_pref(1);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Duplication Detection Settings have been updated!" :
							"File Duplication Detection Settings failed to update!" ,Toast.LENGTH_LONG).show();
					*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					((SharpFixApplicationClass) getApplication()).setFddPref(1);
				}catch(Exception e){
						
				}
			}else{
				//	Duplicate Files Deletion Priority deletes older files
				try{
					ModelPreferences newParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
					ModelPreferences oldParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
							((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
							((SharpFixApplicationClass) getApplication()).getFddPref(),
							((SharpFixApplicationClass) getApplication()).getAutoLogin(),
							((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
							((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
					newParams.setFdd_pref(0);
					/*
					Toast.makeText(this, this.db.update(Tables.preferences, oldParams, newParams, null) ? "File Duplication Detection Priority have been updated!" :
							"File Duplication Detection Priority failed to update!" ,Toast.LENGTH_LONG).show();
					*/
					this.db.update(Tables.preferences, oldParams, newParams, null);
					((SharpFixApplicationClass) getApplication()).setFddPref(0);
				}catch(Exception e){
						
				}
					
			}

		}else{
			// Toast.makeText(this, "File Duplication Detection Settings was not changed" ,Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		this.db.close();
		
		if(this.LOGCAT){
			Log.d(this.TAG, this.db.toString());
			Log.d(this.TAG, this.TAG +  " onDestroy()");
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		try{
			if( ((SharpFixApplicationClass) getApplication()).getFddSwitch() == 0){
				ch.setChecked(false);
				
			}else{
				ch.setChecked(true);
			}
		}catch(Exception e){
			
		}
		try{
			if( ((SharpFixApplicationClass) getApplication()).getFddPref() == 0){
				newer.setChecked(false);
				
			}else{
				newer.setChecked(true);
			}
		}catch(Exception e){
			
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
			  ModelPreferences oldParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
						((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
						((SharpFixApplicationClass) getApplication()).getFddPref(),
						((SharpFixApplicationClass) getApplication()).getAutoLogin(),
						((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
			  ModelPreferences newParams = new ModelPreferences( ((SharpFixApplicationClass) getApplication()).getAccountId(),
						((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
						((SharpFixApplicationClass) getApplication()).getFddPref(),
						((SharpFixApplicationClass) getApplication()).getAutoLogin(),
						((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
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
	
	@Override
	public void onClick(View src){
		
	
		switch (src.getId()) {
	    
		case R.id.label1:
			ch.performClick();
			break;
		
		case R.id.selection2:
			checkChanged(R.id.selection2);
			break;
			
		case R.id.label2:
			
			selection.setPressed(true);
			selection.performClick();
			break;
			
		case R.id.radioNewer:
			
			newer.setPressed(true);
			break;
		
		case R.id.radioOlder:
			
			older.setPressed(true);
			break;

		case R.id.deletionPriority:
		
			selection.setPressed(true);
			selection.performClick();
			
			
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean checkedState) {

		if(ch.isChecked()){
			
		}else{
		
		}

		

		checkChanged(0);
		
		
	}
	
	private void checkChanged(int id){
		if(!ch.isChecked()){
			// rg.setEnabled(false);
			//rg.setVisibility(View.INVISIBLE);
			older.setEnabled(false);
			//older.setVisibility(View.INVISIBLE);
			newer.setEnabled(false);
			//newer.setVisibility(View.INVISIBLE);
			if(id == R.id.selection2 ){
			Toast.makeText(this,
				 	   "Please Enable Automatic Deletion first to change this preference!", Toast.LENGTH_LONG).show();
			}
		}else{
	//		rg.setEnabled(true);
			//rg.setVisibility(View.VISIBLE);
			older.setEnabled(true);
			//older.setVisibility(View.VISIBLE);
			newer.setEnabled(true);
			//newer.setVisibility(View.VISIBLE);
			
		}
	}
	
	
} // end class
