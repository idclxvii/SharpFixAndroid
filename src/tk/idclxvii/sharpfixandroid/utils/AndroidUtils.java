package tk.idclxvii.sharpfixandroid.utils;


import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import android.content.Context;
import android.os.*;
import android.util.Log;

public abstract class AndroidUtils {
	
	private final static String TAG = AndroidUtils.class.getName();
	
	
	public static enum API_CODE_NAME{
		BASE,
		BASE_1_1,
		CUPCAKE,
		DONUT,
		ECLAIR,
		ECLAIR_0_1,
		ECLAIR_MR1,
		FROYO,
		GINGERBREAD,
		GINGERBREAD_MR1,
		HONEYCOMB,
		HONEYCOMB_MR1,
		HONEYCOMB_MR2,
		ICE_CREAM_SANDWICH,
		ICE_CREAM_SANDWICH_MR1,
		JELLY_BEAN,
		JELLY_BEAN_MR1,
		JELLY_BEAN_MR2,
		KITKAT,
		CUR_DEVELOPMENT,
	};
	
	public static String[] API_NAME = {
		"No Code Name",
		"Petit Four",
		"Cupcake",
		"Donut",
		"Eclair",
		"Eclair",
		"Eclair Minor Revision 1",
		"Froyo",
		"Gingerbread",
		"Gingerbread Minor Revision 1",
		"Honeycomb",
		"Honeycomb Minor Revision 1",
		"Honeycomb Minor Revision 2",
		"Ice Cream Sandwich",
		"Ice Cream Sandwich Minor Revision 1",
		"Jellybean",
		"Jellybean Minor Revision 1",
		"Jellybean Minor Revision 2",
		"KitKat",
		"Current Development Build",
	};
	
	public static String[] getCurrentAndroidVersionInfo(){
		
		return (new String[]{
				("Detected Full Android Version Information: Android " + android.os.Build.VERSION.RELEASE + " (" +
						API_CODE_NAME.values()[android.os.Build.VERSION.SDK_INT - 1].toString() + ")"),
				("Android Build Release: Android " + android.os.Build.VERSION.RELEASE),
				("API level " + android.os.Build.VERSION.SDK_INT + " " +API_NAME[android.os.Build.VERSION.SDK_INT - 1])
				
		});
		
	}
	
	
	public static String[] getMountedVolumes(){
		final String state = Environment.getExternalStorageState();
		if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {  // we can read the External Storage...           
		    //Retrieve the primary External Storage:
		    final File primaryExternalStorage = Environment.getExternalStorageDirectory();

		    //Retrieve the External Storages root directory:
		    String externalStorageRootDir = null;
		    if ( (externalStorageRootDir = primaryExternalStorage.getParent()) == null ) {  // no parent...
		    	return (
		    			new String[] {
			    			"ONLY A SINGLE VOLUME HAS BEEN DETECTED!",
			    			(Environment.isExternalStorageRemovable() ? "(REMOVABLE SD-CARD)" : "(INTERNAL STORAGE)")
			    			+ " PRIMARY STORAGE: " + primaryExternalStorage
		    	});
		    }
		    else {
		        final File externalStorageRoot = new File( externalStorageRootDir );
		        final File[] files = externalStorageRoot.listFiles(new FilenameFilter(){

					@Override
					public boolean accept(File dir, String filename) {
						// TODO Auto-generated method stub
						
						File file = new File(dir, filename);
						if(file.isDirectory() && file.canRead() && file.canWrite() 
		            		&& !file.isHidden()){
							return true;
						}
						return false;
					}
		        	
		        }); //.listFiles();
		        List<String> data = new ArrayList<String>();
		        if(files.length > 1){
		        	data.add("MULTIPLE VOLUMES HAS BEEN DETECTED!");
			        data.add("Enumerating detected volumes . . .");
			    }else{
		        	data.add("ONLY A SINGLE VOLUME HAS BEEN DETECTED!");
			    }
		        
		        for ( final File file : files ) {
		            if ( file.isDirectory() && file.canRead() && file.canWrite() 
		            		&& !file.isHidden() && (files.length > 0) ) {  // it is a real directory (not a USB drive)...
		            	if(file.toString().equals(primaryExternalStorage.toString()))
		            		data.add((Environment.isExternalStorageRemovable() ? "(REMOVABLE SD-CARD)" : "(INTERNAL Memory)")
					    			+ " PRIMARY STORAGE: " + file.getAbsolutePath());
		            	else{
		            		data.add( ((file.toString().contains("usb") || 
		            				file.toString().contains("USB")) ? "MOUNTED USB" : "MOUNTED") + " STORAGE: " + file.getAbsolutePath());
		            	}
		            }
		        }
		        return data.toArray(new String[data.size()]);
		    }
		}else{
			// we cannot read the External Storage..., return null
			return null;
		}
		
	}
	
	
	
	public static void tryCatchLogVoid(){
		
		
		
	}
	
	public static boolean tryCatchLogBoolean(){
		
		return true;
	}
	
	
	public static Class<?> tryCatchLogReturn(){
		
		return null;
	}
	
	
}
