package tk.idclxvii.sharpfixandroid.utils;

import android.content.Context;
import android.util.Log;
public abstract class Logcat {

	public static final String TAG = Logcat.class.getName();
		
	public static void d(Context c, String[] array){
		try{
			for(String str : array){
				Log.d(c.getPackageName(), str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	public static void i(Context c, String[] array){
		try{
			for(String str : array){
				Log.i(c.getPackageName(), str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	public static void wtf(Context c, String[] array){
		try{
			for(String str : array){
				Log.wtf(c.getPackageName(), str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	
	public static void v(Context c, String[] array){
		try{
			for(String str : array){
				Log.v(c.getPackageName(), str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	public static void e(Context c, String[] array){
		try{
			for(String str : array){
				Log.e(c.getPackageName(), str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	public static void w(Context c, String[] array){
		try{
			for(String str : array){
				Log.w(c.getPackageName(), str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
		// ##############################################################################################################
	
	public static void d(String packageName, String[] array){
		try{
			for(String str : array){
				Log.d(packageName, str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	public static void i(String packageName, String[] array){
		try{
			for(String str : array){
				Log.i(packageName, str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	public static void wtf(String packageName, String[] array){
		try{
			for(String str : array){
				Log.wtf(packageName, str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	
	public static void v(String packageName, String[] array){
		try{
			for(String str : array){
				Log.v(packageName, str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	public static void e(String packageName, String[] array){
		try{
			for(String str : array){
				Log.e(packageName, str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	public static void w(String packageName, String[] array){
		try{
			for(String str : array){
				Log.w(packageName, str);
			}
		}catch(Exception e){
			try{
				StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.e(TAG, st[y].toString());
				}
			}catch(Exception ee){
				Log.wtf(TAG, "Stacktrace unavailable for Logcat.i(Context, String[]) ");
			}
		}
	}
	
	public static void logCaughtException(Context c, StackTraceElement[] stackTrace ){
		if(stackTrace != null){
			for(int y= 0; y <stackTrace.length; y++){
				Log.w(c.getPackageName(), stackTrace[y].toString());
			}
		}else{
			Log.e(TAG, "Stacktrace unavailable for Logcat.logCaughtException from " +c.getClass().getName());
		}
	}

	public static void logCaughtException(String packageName, StackTraceElement[] stackTrace) {
		// TODO Auto-generated method stub
		if(stackTrace != null){
			for(int y= 0; y <stackTrace.length; y++){
				Log.w(packageName, stackTrace[y].toString());
			}
		}else{
			Log.e(TAG, "Stacktrace unavailable for Logcat.logCaughtException from " +packageName);
		}
	}
	
}
