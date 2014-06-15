package tk.idclxvii.sharpfixandroid;

import android.app.*;
import android.os.*;
import android.util.Log;
import android.widget.*;

import tk.idclxvii.sharpfixandroid.databasemodel.*;

public class ViewRuleActivity extends Activity {

	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	TextView ruleName, fileType, fileTypeLabel, designationDir, designationDirLabel;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_rule);
		this.TAG = this.getClass().getName().replace(this.getPackageName(), "");
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onCreate()");
		}
		ruleName = (TextView) findViewById(R.id.rule_name);
		ruleName.setText(this.getIntent().getStringExtra("RuleName"));
		fileType = (TextView) findViewById(R.id.file_type);
		
		fileType.setText(this.getIntent().getStringExtra("FileType"));
		fileTypeLabel = (TextView) findViewById(R.id.file_type_label);
		
		fileTypeLabel.setText( (this.getIntent().getStringExtra("Instance").equals("fd")) ? "Targeted File Type:" : "Filter Rule Type:");
		
		designationDir = (TextView) findViewById(R.id.designation);
		designationDir.setText(this.getIntent().getStringExtra("Designation"));
		
		designationDirLabel = (TextView) findViewById(R.id.designation_label);
		designationDirLabel.setText((this.getIntent().getStringExtra("Instance").equals("fd")) ? "Target Directory:" : "Target:");
	}
	
	
	@Override
	public void onStart(){
		super.onStart();
		if(LOGCAT){
			Log.d(this.TAG, this.TAG +  "onStart()");
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(LOGCAT){
			Log.d(this.TAG, this.TAG +  "onPause()");
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(LOGCAT){
			Log.d(this.TAG, this.TAG +  "onResume()");
		}
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		if(LOGCAT){
			Log.d(this.TAG, this.TAG +  "onRestart()");
		}
	}
	
	public void onDestroy(){
		super.onDestroy();
		if(LOGCAT){
			Log.d(this.TAG, this.TAG +  "onDestroy()");
		}
	}
	
}
