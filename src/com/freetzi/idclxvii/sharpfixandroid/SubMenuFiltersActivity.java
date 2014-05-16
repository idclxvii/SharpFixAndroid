package com.freetzi.idclxvii.sharpfixandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.freetzi.idclxvii.sharpfixandroid.databasemodel.*;

public class SubMenuFiltersActivity extends Activity implements OnClickListener, OnCheckedChangeListener{
	
	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
			
	CheckBox fddCh, fdCh;
	TextView title, fddLabel, fdLabel, fddFilterRules, fddFilterRulesLabel,
		fdFilterRules, fdFilterRulesLabel;
	
	private SQLiteHelper db;
	
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filters_sub_menu);
		this.TAG = this.getClass().getName().replace(this.getPackageName(), "");
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onCreate()");
		}
			
			title = (TextView)findViewById(R.id.title);
			title.setText(title.getText().toString().toUpperCase());
			
			fddCh = (CheckBox) findViewById(R.id.filters_selection1);
			fddLabel = (TextView) findViewById(R.id.filters_label1);
			fddLabel.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					fddCh.performClick();
				}
				
			});
			fddFilterRules = (TextView) findViewById(R.id.filters_selection2);
			fddFilterRules.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(SubMenuFiltersActivity.this, SubMenuFilterRulesActivity.class);
					// set intent variables to tell whether fd or fdd
					startActivity(i);
					
					//Toast.makeText(SubMenuFiltersActivity.this, "Calling File Duplication Detection Filtering Rules", Toast.LENGTH_LONG).show();
				}
				
			});
			fddFilterRulesLabel = (TextView) findViewById(R.id.filters_label2);
			fddFilterRulesLabel.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					fddFilterRules.setPressed(true);
					fddFilterRules.performClick();
				}
				
			});
			
			fdCh = (CheckBox) findViewById(R.id.filters_selection3);
			fdLabel = (TextView) findViewById(R.id.filters_label3);
			fdLabel.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					fdCh.performClick();
					
				}
				
			});
			fdFilterRules = (TextView) findViewById(R.id.filters_selection4);
			fdFilterRules.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(SubMenuFiltersActivity.this, SubMenuFilterRulesActivity.class);
					// set intent variables to tell whether fd or fdd
					startActivity(i);
					// Toast.makeText(SubMenuFiltersActivity.this, "Calling File Designation Filtering Rules", Toast.LENGTH_LONG).show();
					
				}
				
			});
			fdFilterRulesLabel = (TextView) findViewById(R.id.filters_label4);
			fdFilterRulesLabel.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					fdFilterRules.setPressed(true);
					fdFilterRules.performClick();
					
				}
				
			});
			
			/*
			ch = (CheckBox) findViewById(R.id.selection1);
			ch.setOnClickListener(this);
			ch.setOnCheckedChangeListener(this);
			chl =  (TextView) findViewById(R.id.label1);
			chl.setOnClickListener(this);
			selection = (TextView) findViewById(R.id.selection2);
			selection.setOnClickListener(this);
			label = (TextView) findViewById(R.id.label2);
			label.setOnClickListener(this);
			*/
			this.db = this.getDb(getApplicationContext());
			
			
			
			checkChanged(0);
		}
		
		
		@Override
		public void onDestroy() {
			super.onDestroy();
			this.db.close();
			
			if(LOGCAT){
				Log.d(TAG, this.getClass().getName() + " OnDestroy()");
			}
			
		}
		
		@Override
		public void onPause() {

	 		super.onPause();
	 		if(this.LOGCAT){
				Log.d(this.TAG, this.TAG +  "onPause()");
			}
	 		
		}
		
		@Override
		public void onResume() {
			super.onResume();
			if(this.LOGCAT){
				Log.d(this.TAG, this.TAG +  "onResume()");
			}
			
		}
		
		@Override
		public void onStart(){
			super.onStart();
			if(this.LOGCAT){
				Log.d(this.TAG, this.TAG +  "onStart()");
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
		  
		@Override
		public void onClick(View src) {
						
		}
		
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean checkedState) {
			
		}

		private void checkChanged(int id){
		}


	
		
}
