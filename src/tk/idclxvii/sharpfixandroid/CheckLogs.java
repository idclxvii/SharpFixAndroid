/*
 * CheckLogs.java
 * 1.1.2 Alpha Release Version
 * 
 * Magarzo, Randolf Josef V.

 * Copyright (c) 2013 Magarzo, Randolf Josef V.
 * Project SharpFix Android
 * 
 * SHARPFIX ANDROID FILE MANAGEMENT UTILITY 2014 - 2015 
 * Area of Computer Science College of Accountancy, 
 * Business Administration and Computer Studies
 * San Sebastian College - Recoletos, Manila, Philippines
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. 
 * If not, see http://www.gnu.org/licenses
 * 
 */


package tk.idclxvii.sharpfixandroid;

import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import java.io.*;
import java.util.Locale;

/**
 * This class is a sub class of {@link GlobalExceptionHandlerActivity} responsible for 
 * showing the Check Logs function on the Main Menu (.
 * 
 * @version 1.1.2 Alpha Release Version
 * @author Magarzo, Randolf Josef V.
 *
 */
public class CheckLogs extends GlobalExceptionHandlerActivity{

	
	/**
	 * The text views used in this menu layout
	 */
	private TextView title, log;
	
	/**
	 * The seek bar view used in this menu layout
	 */
	private SeekBar seek;
	
	/**
	 * The button used in this menu layout
	 */
	private Button clearLogs; 
	
	/**
	 * The scroll views used in this menu layout
	 */
	private ScrollView scroll;
	
	/**
	 * The intent extra which define if this layout was called
	 * to show Scan Logs or Progress Logs. 
	 * <br />
	 * <br />
	 * 0 = Scan Logs
	 * <br />
	 * 1 = Progress Logs
	 */
	private int logMode = 0;
	
	/**
	 * The string that will contain the full logs read in the 
	 * log file
	 */
	private String wholeLog = "";
	
	/**
	 * An Asynchronous private class that reads the log file
	 * and updates the UI when all logs has been read. This 
	 * Asynchronous task uses a dialog that cannot be closed
	 * while it's still running its background tasks.
	 */
	private class TASK extends GlobalAsyncTask<Void,Void,Void>{
		
		public TASK(){
			 super(CheckLogs.this, "Loading","Reading log file, please wait . . .");
		}
		
		@Override
		protected Void doTask(Void... params) throws Exception {
			
			wholeLog = readFileNew();
			publishProgress();
			return null;
		}

		@Override
		protected void onException(Exception e) {
			
			e.printStackTrace();
		}
		
		@Override
		protected void onProgressUpdate(Void... params){
			try{
				log.setText(wholeLog);
				seek.setMax(log.getLineCount());
			}catch(Exception e){
				// general exception, usually encountered when log file is too large and phone memory's too small to output
				log.setText("Progress Logs has been detected to overflow! This error has been encoutered because of Out of Memory Exception. "
						+ "Previous progress logs has been deleted,");
				//clearLogs.performClick();
			}
			
		}
		
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.check_logs);
	    logMode = getIntent().getExtras().getInt("logs");
	    title = (TextView)findViewById(R.id.title);
		title.setText((logMode < 0 ? "Error " :  (logMode == 0 ? "Scan " :"Progress ")).toUpperCase(Locale.US) + title.getText().toString().toUpperCase());
		log = (TextView)findViewById(R.id.log);
		scroll = (ScrollView) findViewById(R.id.scroll);
		seek = (SeekBar) findViewById(R.id.seekBar1);
		clearLogs = (Button)findViewById(R.id.btnClearLogs);

		clearLogs.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				log.setText("");
				/*
				File file = new File(CheckLogs.this.getExternalFilesDir(null).getParent(),
						(logMode < 0 ? "error_logs.log" : (logMode == 0) ? "last_scan.log" : "sf_reports.log"));
				if(file.delete()){
					new TASK().executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
				}
				*/
			}
			
		});
	
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, final int progress,
					boolean fromUser) {
				scroll.post(new Runnable() {
				    @Override
				    public void run() {
				        int y = log.getLayout().getLineTop(progress); // e.g. I want to scroll to line defined by progress
				        scroll.scrollTo(0, y);
				    }
				});
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		new TASK().executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);;
	  }

	 
	  /**
	   * Returns the full log contents currently being requested by the intent {@link #logMode}
	   * 
	   * @return All the log contents as a single  {@link java.lang.String} instance
	   */
	  private String readFileNew() {
		 
		  //Get the text file
		
		  File file = new File(CheckLogs.this.getExternalFilesDir(null).getParent(),
					(logMode < 0 ? "error_logs.log" : (logMode == 0) ? "quick_logs.log" : "full_logs.log"));

		  //Read text from file
		  StringBuilder text = new StringBuilder();
		  try {
			  BufferedReader br = new BufferedReader(new FileReader(file));
			  String line;

			  while ((line = br.readLine()) != null) {
				  try{
					  text.append(line + "\n");
			       
				  }catch(Exception ee){
					  ee.printStackTrace(); 
					  br.close();
				  }
		       
			  }
			  br.close();
		  }catch (IOException e) {
			  //You'll need to add proper error handling here
			  e.printStackTrace();
		  }
	
		  return text.toString();
	}
	    
 }

