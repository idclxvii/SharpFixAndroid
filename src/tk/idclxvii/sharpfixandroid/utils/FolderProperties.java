package tk.idclxvii.sharpfixandroid.utils;

import java.io.File;

import android.util.Log;

public class FolderProperties {

    int totalFolder = 0;
    int totalFile = 0;
    long size = 0;
    private final String TAG = this.getClass().getSimpleName();
    
    public FolderProperties(File folder){
        
        try {
        	  this.size = startScan(folder);
        } catch (Exception e) {
        	StackTraceElement[] st = e.getStackTrace();
			for(int y= 0; y <st.length; y++){
				Log.e("FOLDER PROPERTIES", st[y].toString());
			}
        }
    }
    
    public FolderProperties(String folder){
        
        try {
        	this.size = startScan(new File(folder));
        } catch (Exception e) {
        	StackTraceElement[] st = e.getStackTrace();
			for(int y= 0; y <st.length; y++){
				Log.e("FOLDER PROPERTIES", st[y].toString());
			}
        }
    }

    public long startScan(File folder) {
        totalFolder++;
        // System.out.println("Folder: " + folder.getName());
        long foldersize = 0;
        File[] filelist = folder.listFiles();
        for (int i = 0; i < filelist.length; i++) {
            if (filelist[i].isDirectory()) {
                foldersize += startScan(filelist[i]);
            } else {
                totalFile++;
                foldersize += filelist[i].length();
            }
        }
        return foldersize;
    }

    public int getTotalFolder() {
        return this.totalFolder -1 ;
    }

    public int getTotalFile() {
        return this.totalFile;
    }
    
    public long getFolderSize(){
    	return this.size;
    }
    
}