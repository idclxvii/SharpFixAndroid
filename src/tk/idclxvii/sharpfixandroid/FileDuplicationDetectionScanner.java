package tk.idclxvii.sharpfixandroid;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import tk.idclxvii.sharpfixandroid.databasemodel.*;

import android.content.Context;

public class FileDuplicationDetectionScanner {

	
	
	private SQLiteHelper db;
	private Context c;
	
	
	
	public FileDuplicationDetectionScanner(Context c){
		this.db = new SQLiteHelper(c);
		this.c = c;
	}
	
	
	public FileDuplicationDetectionScanner(Context c, File f){
		this.db = new SQLiteHelper(c);
		this.c = c;
		new Task().executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR, f);
		
		
	}
	
	private class Task extends GlobalAsyncTask<File, String, Void>{

		@Override
		protected Void doTask(File... params) throws Exception {
			// TODO Auto-generated method stub
			checkPreferences(params[0]);
			
			
			
			
			
			return null;
		}

		@Override
		protected void onException(Exception e) {
			// TODO Auto-generated method stub
			
		}
		
		
		private void checkPreferences(File f) throws IllegalArgumentException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException{
			
			if(	((ModelPreferences)db.selectAll(Tables.preferences, ModelPreferences.class, null)[0]).getFdd_switch() == 1){
				// file duplication detection switch is turned on
				if( f.isDirectory() && f.canRead() && f.canWrite() && !f.isHidden() && f.exists() ){
					// legal directory
					if( ((ModelPreferences)db.selectAll(Tables.preferences, ModelPreferences.class, null)[0]).getFdd_Filter_switch() == 1){
						// fdd filter switch is turned on
						ModelDirFilter mdf = (ModelDirFilter) db.select(Tables.dir_filter, ModelDirFilter.class,
								new Object[][] {{"dir", f.getAbsolutePath()}},null);
						if( mdf.getDir() != null && mdf.getDir().equals(f.getAbsolutePath())){
							// this dir is being filtered!
							
						}else{
							// this dir is not being filtered!
							// scan this directory
							scan(f);
						}
					}else{
						// fdd filter switch is turned off
						// scan this directory
						scan(f);
						
						
					}
					
					
				}else{
					if(!f.isDirectory() && f.canRead() && f.canWrite() && !f.isHidden() && f.exists()){
						// legal file
						if( ((ModelPreferences)db.selectAll(Tables.preferences, ModelPreferences.class, null)[0]).getFdd_Filter_switch() == 1){
							// fdd filter switch is turned on
							ModelFileFilter mff = (ModelFileFilter) db.select(Tables.file_filter, ModelFileFilter.class,
									new Object[][] {{"file", f.getAbsolutePath()}},null);
							if( mff.getFile() != null && mff.getFile().equals(f.getAbsolutePath())){
								// this dir is being filtered!
								
							}else{
								// this dir is not being filtered!
								// scan this directory
								scan(f);
							}
						}else{
							// fdd filter switch is turned off
							scan(f);
						}
					}
				}
				
				
			}
		}
		
	
	
		private void scan(File f){
			
		}
	
	}
	
	
}
