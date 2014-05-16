package com.freetzi.idclxvii.sharpfixandroid;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.view.WindowManager.LayoutParams;
import android.widget.*;
import java.io.*;
import java.util.List;
public class CheckLogs extends Activity{

	private static final String TAG = "ServicesDemo";
	 TextView textview;
	 List<ImageView> appImages;
	 
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.check_logs);
	    
	 //   textview.setMovementMethod(new ScrollingMovementMethod());
	    /*try{
   		 String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
   		 String FILENAME = "keylogger.txt";
   		 FileInputStream fis;
   		 fis = openFileInput(SDCARD+File.separator+FILENAME);
   		 StringBuffer fileContent = new StringBuffer("");

   		 byte[] buffer = new byte[1024];

   		 while (fis.read(buffer) != -1) {
   			 fileContent.append(new String(buffer));
   		  
   		 }
   		 fis.close();
   		textview.setText(fileContent);
   	 }catch(Exception e) {
   		 Log.d("EXCEPTION",e.getMessage());
   	 }
	    */
	    textview = (TextView)  findViewById(R.id.logged);
	   // textview.setText(readSavedData());
	   // textview.setText(readFromFile());
	    textview.setText(readFileNew(textview));
	    textview.setMovementMethod(new ScrollingMovementMethod());
	    
	  }

	 
	 
	  
	  private String readFileNew(TextView textview) {
		 
		//  File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"SharpFix.log");

		//Read text from file
		StringBuilder text = new StringBuilder();
		int maxLines =0;
		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		    	try{
		    		maxLines++;
			    	/*
		    		byte[] yourKey =  Security.generateKey("password");
	                byte[] decodedData = Security.decodeFile(yourKey, line.getBytes());
	                */
	                text.append(/*decodedData.toString()*/line);
			        text.append('\n');
			       
			   	}catch(Exception ee){
		    				    		
		    	}
		       
		    }
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}
		textview.setMaxLines(maxLines);
		textview.setMovementMethod(new ScrollingMovementMethod());
		return text.toString();
		  
	  }
	    
 }

