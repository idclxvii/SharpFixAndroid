package tk.idclxvii.sharpfixandroid;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.*;
import android.widget.*;



public class SubMenuAboutActivity extends GlobalExceptionHandlerActivity implements OnClickListener{
	
	private final String TAG = this.getClass().getSimpleName();
	TextView currentVersionMain, currentVersionLabel1, currentVersionLabel2,
		currentVersionLabel3,
		whatIsItMain, whatIsItLabel1, whatIsItLabel2,whatIsItLabel3,
		latestVersionMain, latestVersionLabel1, licenseMain, licenseLabel1,
		licenseLabel2, licenseLabel3, licenseLabel4, licenseLabel5;
	
	SharpFixApplicationClass SF;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SF = ((SharpFixApplicationClass)getApplication() );
		
		setContentView(R.layout.about_sub_menu);

		currentVersionMain = (TextView) findViewById(R.id.currentVersionMain);
		currentVersionMain.setText("Current Version Information");
		
		currentVersionLabel1 = (TextView) findViewById(R.id.currentVersionLabel1);
		try{
		currentVersionLabel1.setText("Release Version: "
				+this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode);
		}catch(Exception e){}
		
		currentVersionLabel2 = (TextView) findViewById(R.id.currentVersionLabel2);
		try{
		currentVersionLabel2.setText("Version Name/Alias: "
				+ this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
		}catch(Exception e){}
		
		currentVersionLabel3 = (TextView) findViewById(R.id.currentVersionLabel3);
		currentVersionLabel3.setText("Database Version: " + SQLiteHelper.getDatabaseVersion());
		
		whatIsItMain = (TextView) findViewById(R.id.whatIsItMain);
		whatIsItMain.setText("What is it ?");
		
		whatIsItLabel1 = (TextView) findViewById(R.id.whatIsItLabel1);
		whatIsItLabel1.setText("SharpFixAndroid is a File Management Utility for mobile phones using Android Operating System. "
		+ "It automatically performs the tasks of file administration such as file duplication deletion and file segregation or "
				+"also known as \"File Designation\". "
				+"It is targeted for Android 2.3 (Gingerbread – API Level 10) and above.");
		
		whatIsItLabel2 = (TextView) findViewById(R.id.whatIsItLabel2);
		whatIsItLabel2.setText("The utility aims to maintain the SD-Card and Internal Memory directory of the mobile device to be free of duplicate "
		+"files as well as properly segregated with respect to its file types. "
				+"It scans the said directories as well as the sub directories and performs the appropriate tasks according"
		+" to the user’s settings and preferences.");
		
		whatIsItLabel3 = (TextView) findViewById(R.id.whatIsItLabel3);
		whatIsItLabel3.setText("This project demonstrates on how to develop and interact with the Android Platform "
		+"which includes accessing the File System, devising different threading models, applying appropriate computing "
				+"concepts (parallel computing, concurrent computing, etc.) and testing different algorithms’ efficiency and performance.");
		
		latestVersionMain = (TextView) findViewById(R.id.latestVersionMain);
		latestVersionMain.setText("Latest Version");
		
		latestVersionLabel1 = (TextView) findViewById(R.id.latestVersionLabel1);
		latestVersionLabel1.setText("For latest updates on this project, please visit the GitHub repository of SharpFix Android at: ");
		
		licenseMain = (TextView) findViewById(R.id.licenseMain);
		licenseMain.setText("License");
		
		licenseLabel1 = (TextView) findViewById(R.id.licenseLabel1);
		licenseLabel1.setText("SharpFix Android App Copyright 2013 c0d3s1x");
		
		licenseLabel2 = (TextView) findViewById(R.id.licenseLabel2);
		licenseLabel2.setText("This program is free software: you can redistribute it and/or modify"
		+" it under the terms of the GNU General Public License as published by the Free Software Foundation, "
				+"either version 3 of the License, or (at your option) any later version.");
		
		licenseLabel3 = (TextView) findViewById(R.id.licenseLabel3);
		licenseLabel3.setText("This program is distributed in the hope it will be useful, "
		+"but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or "
				+"FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.");
		
		licenseLabel4 = (TextView) findViewById(R.id.licenseLabel4);
		licenseLabel4.setText("You should have received a copy of the GNU General Public License along with this program. If not, see: ");
		
		
		
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	
	@Override
	public void onClick(View src) {
		
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
