package tk.idclxvii.sharpfixandroid;

import java.io.File;

import android.content.Context;


public class FileDesignationScanner {

	
	private SQLiteHelper db;
	private Context c;
	
	public FileDesignationScanner(Context c){
		this.db = new SQLiteHelper(c);
		this.c = c;
	}
	
	
	public FileDesignationScanner(Context c, File f){
		this.db = new SQLiteHelper(c);
		this.c = c;
		
		new Task().executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
		
		
	}
	
	private class Task extends GlobalAsyncTask<File, String, Void>{

		@Override
		protected Void doTask(File... params) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onException(Exception e) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	
	
	
}
