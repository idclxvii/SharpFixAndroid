package tk.idclxvii.sharpfixandroid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.idclxvii.sharpfixandroid.utils.AndroidUtils;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public abstract class GlobalExceptionHandlerActivity extends Activity implements
			UncaughtExceptionHandler{
	
	
	
	  
	  public final String TAG =  this.getClass().getSimpleName();;  
	  
	  private ProgressDialog dialog;
	  
	  //System default UncaughtException class making
	  private Thread.UncaughtExceptionHandler mDefaultHandler;  
	  
	  //Used to store information and abnormal information making equipment
	  private Map<String, String> infos = new HashMap<String, String>();  
	  
	  //Used to format the date, the name of the log file as part of making
	//  private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  
	
	List<String> GLOBAL_LOGS; 
	
	
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  //Acquisition system default UncaughtException processor making
		  mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
	      	
		  //The CrashHandler settings for the default processor making program
		  Thread.setDefaultUncaughtExceptionHandler(this);  
		  
		  dialog = new ProgressDialog(GlobalExceptionHandlerActivity.this);
	      dialog.setTitle("Ooops! This is embarassing!");
	      dialog.setMessage("SharpFix has encountered an unexpected error! Restarting application state . . .");
	      dialog.setIndeterminate(true);
	      dialog.setCancelable(false); 
	     
		  /*  
		 	The code snippet below logs the error to a log file and then passes the errors to Android System 
		  	which does not handle the default Application-Error-Dialog of Android
		  	
		  final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		  Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
	                @Override
	                public void uncaughtException(Thread paramThread,Throwable paramThrowable){
	                    //Do your own error handling here
	                	GLOBAL_LOGS = new ArrayList<String>();
	                	StringWriter errors = new StringWriter();
	                	paramThrowable.printStackTrace(new PrintWriter(errors));
	                	GLOBAL_LOGS.add(AndroidUtils.convertMillis(System.currentTimeMillis()) + 
	        						errors.toString());
	                	AndroidUtils.logGlobalException(GlobalExceptionHandlerActivity.this, GLOBAL_LOGS.toArray(new String[GLOBAL_LOGS.size()]));
	        			
	                	if (oldHandler != null)
	                		oldHandler.uncaughtException(paramThread, paramThrowable); //Delegates to Android's error handling
	                    else
	                        System.exit(2); //Prevents the service/app from freezing
	                }
		  });
		  
		  */
	  }
	  
	  
	  /*
	   * ##################################################################################################################
	   * 
	   */

	
	  
	  
	   
	 
	  
	    /** 
	Making making * when UncaughtException occurs to the function to process the
	     */  
	    @Override  
	    public void uncaughtException(Thread thread, Throwable ex) {  
	        if (!handleException(ex) && mDefaultHandler != null) {  
	            //The exception handler if the user does not have the processing allows the system default to deal making
	            mDefaultHandler.uncaughtException(thread, ex);  
	        } else {  
	            try {  
	                Thread.sleep(3000);  
	            } catch (InterruptedException e) {  
	                Log.e(TAG, "error : ", e);  
	            } 
	            
	            Intent mStartActivity = new Intent(this, MainActivity.class);
	            mStartActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		        int mPendingIntentId = 123456;
		        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, mPendingIntent);
		        dialog.cancel();
		        System.exit(0);
	            /*
	            //Exit the program, startup program code to restart the following
	            android.os.Process.killProcess(android.os.Process.myPid());  
	            System.exit(1);  
	            // Restart the program, notes above the exit procedures
	            Intent intent = new Intent();
		        intent.setClass(this,MainActivity.class);
		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intent);
		        
		        */
		       
		        //android.os.Process.killProcess(android.os.Process.myPid());
		  
	           /*
		            Intent i = getBaseContext().getPackageManager()
		                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
		            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		            startActivity(i);
	            */
	        }  
	    }  
	  
	    /** 
	Making making * custom error handling, collect error information, send a bug report and other operations are completed in this
	     *  
	     * @param ex 
	     * @return true: If the exception information; otherwise it returns false
	     */  
	    private boolean handleException(Throwable ex) {  
	        if (ex == null) {  
	            return false;  
	        }  
	  
	        //Use Toast to display the abnormal information making
	        new Thread() {  
	            @Override  
	            public void run() {  
	                Looper.prepare();  
	                /*
	                Toast.makeText(GlobalExceptionHandlerActivity.this, "Ooops, this is embarassing! SharpFix has encountered an unexpected error!\n" + 
	                "Logging the error dump and restarting the application . . .", Toast.LENGTH_LONG).show();  
	                */
	                
	    	        dialog.show();
	                Looper.loop();  
	            }  
	        }.start();  
	  
	        //Collection device parameter information making
	        collectDeviceInfo(GlobalExceptionHandlerActivity.this);  
	        //Save log documentation
	        saveCrashInfo2File(ex);  
	        return true;  
	    }  
	  
	    /** 
	Making making * collection device parameter information
	     * @param ctx 
	     */  
	    public void collectDeviceInfo(Context ctx) {  
	        try {  
	            PackageManager pm = ctx.getPackageManager();  
	            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);  
	  
	            if (pi != null) {  
	                String versionName = pi.versionName == null ? "null" : pi.versionName;  
	                String versionCode = pi.versionCode + "";  
	                infos.put("versionName", versionName);  
	                infos.put("versionCode", versionCode);  
	            }  
	        } catch (NameNotFoundException e) {  
	            Log.e(TAG, "an error occured when collect package info", e);  
	        }  
	  
	        Field[] fields = Build.class.getDeclaredFields();  
	        for (Field field : fields) {  
	            try {  
	                field.setAccessible(true);  
	                infos.put(field.getName(), field.get(null).toString());  
	                Log.d(TAG, field.getName() + " : " + field.get(null));  
	            } catch (Exception e) {  
	                Log.e(TAG, "an error occured when collect crash info", e);  
	            }  
	        }  
	    }  
	  
	    /** 
	Making making * save error information to a file
	    * 
	     * @param ex 
	     * @Return making returns the file name, the file is transferred to the server
	     */  
	    private String saveCrashInfo2File(Throwable ex) {  
	        StringBuffer sb = new StringBuffer();  
	        for (Map.Entry<String, String> entry : infos.entrySet()) {  
	            String key = entry.getKey();  
	            String value = entry.getValue();  
	            sb.append(key + "=" + value + "\n");  
	        }  
	  
	        Writer writer = new StringWriter();  
	        PrintWriter printWriter = new PrintWriter(writer);  
	        ex.printStackTrace(printWriter);  
	        Throwable cause = ex.getCause();  
	        while (cause != null) {  
	            cause.printStackTrace(printWriter);  
	            cause = cause.getCause();  
	        }  
	        printWriter.close();  
	  
	        String result = writer.toString();  
	        sb.append(result);  
	        try {  
	            //long timestamp = System.currentTimeMillis();  
	            // String time = formatter.format(new Date());  
	            // String fileName = "crash-" + time + "-" + timestamp + ".log";  
	              
	           // if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
	                //  String path = "/sdcard/crash/";  
	                File file = new File(GlobalExceptionHandlerActivity.this.getExternalFilesDir(null).getParent(), "error_logs.log");  
	              
	                FileOutputStream fos = new FileOutputStream(file,true);  
	                fos.write(sb.toString().getBytes());  
	                fos.close();  
	            // }  
	  
	            return file.getAbsolutePath();  
	        } catch (Exception e) {  
	            Log.e(TAG, "an error occured while writing file...", e);  
	        }  
	  
	        return null;  
	    }  
}
