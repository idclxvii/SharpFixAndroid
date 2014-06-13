package tk.idclxvii.sharpfixandroid.utils;

import java.io.*;	
import java.text.*;
import java.util.Locale;

public class FileProperties {

		
	public static String formatFileSize(long size){
		
		if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
		
	public static String formatFileLastMod(long lastMod){
		return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa - EEE", Locale.getDefault()).format(lastMod);
		
	}
		

}
