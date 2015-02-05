package tk.idclxvii.sharpfixandroid;

import tk.idclxvii.sharpfixandroid.databasemodel.ModelPreferences;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SubMenuLogs extends GlobalExceptionHandlerActivity{

	private final String TAG = this.getClass().getSimpleName();
	private SharpFixApplicationClass SF;
	TextView title, scanLogs, scanLogsLabel, progressLogs, progressLogsLabel, errorLogs, errorLogsLabel,
				email, emailLabel;
	
	SQLiteHelper db;
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getDb(this);
		SF = ((SharpFixApplicationClass) getApplication() );
		setContentView(R.layout.view_logs);
		title = (TextView)findViewById(R.id.title);
		title.setText(title.getText().toString().toUpperCase());
		
		

		scanLogsLabel = (TextView) findViewById(R.id.logs_label1);
		scanLogsLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				scanLogs.setPressed(true);
				scanLogs.performClick();
			}
			
		});
		scanLogs = (TextView) findViewById(R.id.logs_selection1);
		scanLogs.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// call scheduled scan settings Activity
				Intent i = new Intent(SubMenuLogs.this, CheckLogs.class);
				// 0  = scan logs, 1 = progress logs
				i.putExtra("logs", 0);
				startActivity(i);
			}
			
		});
		
		progressLogsLabel = (TextView) findViewById(R.id.logs_label2);
		progressLogsLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				progressLogs.setPressed(true);
				progressLogs.performClick();
			}
			
		});
		progressLogs = (TextView) findViewById(R.id.logs_selection2);
		progressLogs.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// call scheduled scan settings Activity
				Intent i = new Intent(SubMenuLogs.this, CheckLogs.class);
				// 0  = scan logs, 1 = progress logs
				i.putExtra("logs", 1);
				startActivity(i);
				
			}
			
		});
	
		
		errorLogs = (TextView) findViewById(R.id.logs_selection3);
		errorLogs.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// call scheduled scan settings Activity
				Intent i = new Intent(SubMenuLogs.this, CheckLogs.class);
				// 0  = scan logs, 1 = progress logs
				i.putExtra("logs", -1);
				startActivity(i);
				
			}
			
		});
		
		errorLogsLabel = (TextView) findViewById(R.id.logs_label3);
		errorLogsLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				errorLogs.setPressed(true);
				errorLogs.performClick();
			}
			
		});
		
		emailLabel = (TextView) findViewById(R.id.logs_label4);
		emailLabel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				email.setPressed(true);
				email.performClick();
			}
			
		});
		
		email = (TextView) findViewById(R.id.logs_selection4);
		email.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// call scheduled scan settings Activity
				AlertDialog.Builder builder = new AlertDialog.Builder(SubMenuLogs.this);
				builder.setTitle("Input Email Address:");

				// Set up the input
				final EditText input = new EditText(SubMenuLogs.this);
				// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
				//input.setInputType(InputType.TYPE_CLASS_TEXT);
				input.setText(SF.getEmail());
				builder.setView(input);

				// Set up the buttons
				builder.setPositiveButton("Save", new DialogInterface.OnClickListener() { 
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				       // m_Text = input.getText().toString();
				    	try{
				    		ModelPreferences newParams = new ModelPreferences(SF.getAccountId(),
									SF.getFddSwitch(),
									SF.getFdSwitch(),
									SF.getFddPref(),
									SF.getAutoLogin(),
									SF.getFddFilterSwitch(),
									SF.getFdFilterSwitch(),
									
									// new fields
									// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
									SF.getServiceSwitch(),
									SF.getServiceHour(),
									SF.getServiceMin(),
									SF.getServiceAMPM(),
									SF.getServiceUpdateSwitch(),
									SF.getServiceRepeat(),
									SF.getServiceNoti(),
									SF.getAuSwitch(),
									SF.getEmail()
									);
				    		
				    		
				    		ModelPreferences oldParams = new ModelPreferences(SF.getAccountId(),
									SF.getFddSwitch(),
									SF.getFdSwitch(),
									SF.getFddPref(),
									SF.getAutoLogin(),
									SF.getFddFilterSwitch(),
									SF.getFdFilterSwitch(),
									
									// new fields
									// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
									SF.getServiceSwitch(),
									SF.getServiceHour(),
									SF.getServiceMin(),
									SF.getServiceAMPM(),
									SF.getServiceUpdateSwitch(),
									SF.getServiceRepeat(),
									SF.getServiceNoti(),
									SF.getAuSwitch(),
									SF.getEmail()
									);
				    		newParams.setEmail(input.getText().toString());
				    	db.update(Tables.preferences,
				    			oldParams, 
				    		newParams, null);
				    	SF.updatePreferences(db); 	
				    	}catch(Exception e){
				    		
				    		
				    	}
				    }
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        dialog.cancel();
				    }
				});

				builder.show();
				
			}
			
		});
	
		//emailLabel.setText(emailLabel.getText() + "\n" + (SF.getEmail().isEmpty()? "NO EMAIL HAS BEEN SET UP!": SF.getEmail()));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	@Override
	  public boolean onCreateOptionsMenu(Menu menu){
		  MenuInflater mi = getMenuInflater();
		  mi.inflate(R.menu.main, menu);
		  return true;
	 }
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item){
		  // handle item selection:
		  switch (item.getItemId()){
		  
		  
		  case R.id.MenuLogout:
			// call method / do task.
			  SF.resetAll();
			  Intent i = new Intent(this,MainActivity.class);
			  i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			  startActivity(i);
			  finish();
			  return true;
			
			  
		  default:
			  return super.onOptionsItemSelected(item);
				  
		  }
	  }
	
}
