package tk.idclxvii.sharpfixandroid;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.WindowManager.LayoutParams;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.*;
import java.util.List;
public class CheckLogs extends GlobalExceptionHandlerActivity{

	private final String TAG = this.getClass().getSimpleName();
	
	TextView title, log;
	SeekBar seek;
	Button clearLogs; 
	ScrollView scroll;
	List<ImageView> appImages;
	private int logMode = 0;
	private String wholeLog = "";
	
	private class TASK extends GlobalAsyncTask<Void,Void,Void>{
		
		public TASK(){
			 super(CheckLogs.this, "Loading","Reading log file, please wait . . .");
		}
		
		@Override
		protected Void doTask(Void... params) throws Exception {
			// TODO Auto-generated method stub
			
			wholeLog = readFileNew(/*log*/);
			publishProgress();
			return null;
		}

		@Override
		protected void onException(Exception e) {
			Log.e(TAG,"EXCEPTION CAUGHT: CheckLogs.java");
			e.printStackTrace();
			// TODO Auto-generated method stub
			
		}
		
		@Override
		protected void onProgressUpdate(Void... params){
			log.setText(wholeLog);
			seek.setMax(log.getLineCount());
			
			
			// log.setMaxLines(maxLines);
			// log.setMovementMethod(new ScrollingMovementMethod());
			
		}
		
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.check_logs);
	    
	    
	    
	    
	    
	    logMode = getIntent().getExtras().getInt("logs");
	    
	    title = (TextView)findViewById(R.id.title);
		title.setText((logMode < 0 ? "Error " :  (logMode == 0 ? "Scan " :"Progress ")).toUpperCase() + title.getText().toString().toUpperCase());
		
		
		
		log = (TextView)findViewById(R.id.log);
		
		scroll = (ScrollView) findViewById(R.id.scroll);

		seek = (SeekBar) findViewById(R.id.seekBar1);
		
		clearLogs = (Button)findViewById(R.id.btnClearLogs);
		
		clearLogs.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = new File(CheckLogs.this.getExternalFilesDir(null).getParent(),
						(logMode < 0 ? "error_logs.log" : (logMode == 0) ? "last_scan.log" : "sf_reports.log"));
				if(file.delete()){
					new TASK().executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
				}
				
			}
			
		});
		/*
		scroll.setOnTouchListener(new View.OnTouchListener(){

			final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new
                    ViewTreeObserver.OnScrollChangedListener() {

				@Override
				public void onScrollChanged() {
				 //do stuff here 
					//seek.setProgress(log.getScrollY());
					
					Log.i(TAG,"Log's Y scroll: " + log.getLayout());
				}
			};
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				ViewTreeObserver observer = scroll.getViewTreeObserver();
		        observer.addOnScrollChangedListener(onScrollChangedListener);

		        return false;
			}
			
		});
		*/
		
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, final int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				Log.i(TAG, "Seekbar value: " + progress);
				//log.scrollTo(0, progress);
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

	 
	  
	  private String readFileNew(/*TextView textview*/) {
		 
		//  File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		
		  File file = new File(CheckLogs.this.getExternalFilesDir(null).getParent(),
					(logMode < 0 ? "error_logs.log" : (logMode == 0) ? "last_scan.log" : "sf_reports.log"));

		//Read text from file
		StringBuilder text = new StringBuilder();
		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		    	try{
			    	/*
		    		byte[] yourKey =  Security.generateKey("password");
	                byte[] decodedData = Security.decodeFile(yourKey, line.getBytes());
	                */
	                text.append(/*decodedData.toString()*/line + "\n");
			       
			   	}catch(Exception ee){
		    				   ee.printStackTrace(); 		
		    	}
		       
		    }
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
			e.printStackTrace();
		}
		/*
		textview.setMaxLines(maxLines);
		textview.setMovementMethod(new ScrollingMovementMethod());
		*/
		
		return text.toString();
		  
	  }
	    
 }

