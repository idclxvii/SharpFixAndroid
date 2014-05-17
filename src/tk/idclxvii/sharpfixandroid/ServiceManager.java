package tk.idclxvii.sharpfixandroid;

import android.os.*;
import android.util.*;
import android.content.*;
import android.app.*;
public class ServiceManager extends Activity {
	private static final String TAG = "SharpFix";
	
	public static void startFileDesignationService() {
	    Log.i(TAG, "ServiceManager.startSerivce()...");
	    Intent intent = new Intent(FileDesignationService.class.getName());
	  //  startService(intent);
	    
	}
}
