package tk.idclxvii.sharpfixandroid;

import android.app.Activity;

import tk.idclxvii.sharpfixandroid.databasemodel.ModelAccountsInfo;
import tk.idclxvii.sharpfixandroid.databasemodel.ModelPreferences;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import tk.idclxvii.sharpfixandroid.utils.*;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SubMenuDirectScanControls extends GlobalExceptionHandlerActivity {

	
	
	private SharpFixApplicationClass SF;
	private final String TAG = this.getClass().getSimpleName();
	private boolean LOGCAT;
	SQLiteHelper db;
		
	// layout fields
	TextView title;
	Button start, stop;
		
	// service Intents
	private Intent dsIntent, fdsIntent, fddsIntent;
		
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
			this.SF = ((SharpFixApplicationClass) getApplication() );
			this.LOGCAT = this.SF.getLogCatSwitch();
			if(this.LOGCAT){
				Log.d(this.TAG, this.TAG +  " onCreate()");
			}
			// initialize database connection
			db = this.getDb(getApplicationContext());
			
			// ######################## ALPHA RELEASE 1.1.2 ########################
			
			/* ALPHA RELEASE 1.1.2 FIX
			 * 
			 * This patch fixes the error that the Notification Pending Intent used by
			 * the Scanners is directly calling SubMenuDirectScanControls class,
			 * regardless if Auto login in is disabled or enabled. An unauthorized 
			 * user can then stop or start scans from this menu.
			 * 
			 * Solution:
			 * 
			 * 		Check for Auto login switch before proceeding on how to handle
			 * 		the intent. If auto login is disabled, a user authentication is
			 * 		needed in order to continue.
			 * 
			 * */
			
			if(getIntent().getExtras().getBoolean("notification")){
				// this activity has been called from a Notification intent
				// Check whether autologin and handle the request 
				if(SF.getAutoLogin() == 1){
					// auto login is switched on
					setContentView(R.layout.direct_scan_controls);
					createServiceIntents();
					
					title = (TextView)findViewById(R.id.title);
					title.setText(title.getText().toString().toUpperCase());
					
					
					
					
					
					start = (Button) findViewById(R.id.scan);
					start.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							SubMenuDirectScanControls.this.startService(dsIntent);
						}
						
					});
					stop = (Button) findViewById(R.id.stop);
					stop.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//SubMenuDirectScanControls.this.stopService(dsIntent);

							SubMenuDirectScanControls.this.stopService(new Intent(SubMenuDirectScanControls.this, DirectoryScanner.class));
							SubMenuDirectScanControls.this.stopService(new Intent(SubMenuDirectScanControls.this, FileDuplicationDetectionScanner.class));
							SubMenuDirectScanControls.this.stopService(new Intent(SubMenuDirectScanControls.this, FileDesignationScanner.class));
							
							
						}
					
					});
					
				}else{
					// auto login is switched off
					setContentView(R.layout.activity_main);
					
					final EditText username = (EditText) findViewById(R.id.username);
					final EditText password = (EditText) findViewById(R.id.password);
			    	username.setOnEditorActionListener(new OnEditorActionListener(){
			    		@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
			    			boolean handled = false;

			    			if(actionId == EditorInfo.IME_ACTION_NEXT){
			    				password.requestFocus();
			    				handled = true;
			    			}

			    			return handled;
						}

			    	});

			    	final Button login = (Button) findViewById(R.id.login);
			    	final CheckBox ch = (CheckBox) findViewById(R.id.autoLogin);
			    	ch.setOnCheckedChangeListener(new OnCheckedChangeListener(){

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if(ch.isChecked()){
								((SharpFixApplicationClass) getApplication()).setAutoLogin(1);
								
							}else{
								((SharpFixApplicationClass) getApplication()).setAutoLogin(0);
								
							}
							
						}
			    		
			    	});

				    	
				    	/*
				    	stopService(new Intent(this, FileDesignationService.class));
				  		stopService(new Intent(this, FileDuplicationDetectionService.class));
				    	*/
			    	
			    	
			    	login.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
							new GlobalAsyncTask<Void,EditText,Void>(SubMenuDirectScanControls.this, "Loading","Checking account, please wait . . ."){
								
								boolean access = false;
								@Override
								protected Void doTask(Void... params) throws java.lang.reflect.InvocationTargetException,
								NoSuchMethodException, IllegalAccessException, InstantiationException{
									// TODO Auto-generated method stub
									//try{
								    ModelAccountsInfo mai = (ModelAccountsInfo) db.select(Tables.accounts_info, ModelAccountsInfo.class,
								    		new Object[][]{{"login",username.getText().toString()}}, null);//this.db.getAccountInfo(username.getText().toString());
								    if(mai.getPassword().equals(Security.md5Hash(password.getText().toString()))){
								    	//Toast.makeText(this, "Welcome "+ mai.getLogin()+" !", Toast.LENGTH_LONG).show();
								    	//i.putExtra("accountId", mai.getId());
								    	((SharpFixApplicationClass) getApplication()).setAccountId(mai.getId());
								    	try{
								    		ModelPreferences oldParams = (ModelPreferences) db.select(Tables.preferences, ModelPreferences.class, new Object[][] {{"account",
								    				mai.getId()}},null);
								    		((SharpFixApplicationClass) getApplication()).setFddPref(oldParams.getFdd_pref());
								    		((SharpFixApplicationClass) getApplication()).setFddSwitch(oldParams.getFdd_switch());
								    		((SharpFixApplicationClass) getApplication()).setFdSwitch(oldParams.getFd_switch());
								    		((SharpFixApplicationClass) getApplication()).setFddFilterSwitch(oldParams.getFdd_Filter_switch());
								    		((SharpFixApplicationClass) getApplication()).setFdFilterSwitch(oldParams.getFd_Filter_switch());
								    				
								    		((SharpFixApplicationClass) getApplication()).setServiceSwitch(oldParams.getSss_switch());
											((SharpFixApplicationClass) getApplication()).setServiceHour(oldParams.getSss_hh());
											((SharpFixApplicationClass) getApplication()).setServiceMin(oldParams.getSss_mm());
											((SharpFixApplicationClass) getApplication()).setServiceAMPM(oldParams.getSss_ampm());
											((SharpFixApplicationClass) getApplication()).setServiceUpdateSwitch(oldParams.getSss_update());
											((SharpFixApplicationClass) getApplication()).setServiceRepeat(oldParams.getSss_repeat());
											((SharpFixApplicationClass) getApplication()).setServiceNoti(oldParams.getSss_noti());
											((SharpFixApplicationClass) getApplication()).setAuSwitch(oldParams.getAu_switch());
								    				
											if((ch.isChecked() && oldParams.getAuto_login() == 0) ||
													!ch.isChecked() && oldParams.getAuto_login() == 1){
								    				// changes has been made, update database
												ModelPreferences newParams = new ModelPreferences();
									    		newParams.setAuto_login((ch.isChecked() ? 1 : 0));
									    		db.update(Tables.preferences, oldParams, newParams, null);
									    		((SharpFixApplicationClass) getApplication()).updatePreferences(db);
								    		}
								    				
								    			
								    	}catch(Exception e){
								    				
								    	}
								    			//startActivity(new Intent(this, MainMenuActivity.class));
								    	if(LOGCAT){
								    		Log.d(TAG, "Login successful! Access granted.");
										}
								    	//startActivityForResult(new Intent(SubMenuDirectScanControls.this, SubMenuDirectScanControls.class), 1);
								    	access = true;
								    	publishProgress(new EditText[]{password,username});
								    			/*
								    			username.setText(null);
								    			password.setText(null);
								    			*/
								    	}else{
								    			//Toast.makeText(this, "Incorrect password!", Toast.LENGTH_LONG).show();
								    		if(LOGCAT){
								    			Log.d(TAG, "Login failure! Access denied!");
											}
								    		publishProgress(new EditText[]{password});
								    			/*
								    			password.setText(null);
								    			password.requestFocus();
								    			*/
								    	}
								    	//}catch(Exception e){
								    		
								    		/*
								    		username.setText(null);
								    		password.setText(null);
								    		username.requestFocus();
							    			*/
								    	//}
										return null;
									}

									@Override
									protected void onException(Exception e) {
										// TODO Auto-generated method stub
										if(LOGCAT){
											e.printStackTrace();
											Log.d(TAG, "Username does not exist! Login failure!");
											
											
											
										}
							    		//Toast.makeText(this, "Username does not exist!", Toast.LENGTH_LONG).show();
							    		publishProgress(new EditText[]{password,username});
										
									}
									
									@Override
									protected void onProgressUpdate(EditText... params){
										
										for(EditText et : params){
											et.setText(null);
											et.requestFocus();
										}
										
										
										
										/*
										username.setText(null);
							    		password.setText(null);
							    		username.requestFocus();
										*/
										if(access){
											setContentView(R.layout.direct_scan_controls);	
									    	createServiceIntents();
											title = (TextView)findViewById(R.id.title);
											title.setText(title.getText().toString().toUpperCase());
											
											start = (Button) findViewById(R.id.scan);
											start.setOnClickListener(new OnClickListener(){

												@Override
												public void onClick(View v) {
													// TODO Auto-generated method stub
													SubMenuDirectScanControls.this.startService(dsIntent);
												}
												
											});
											stop = (Button) findViewById(R.id.stop);
											stop.setOnClickListener(new OnClickListener(){

												@Override
												public void onClick(View v) {
													// TODO Auto-generated method stub
													//SubMenuDirectScanControls.this.stopService(dsIntent);

													SubMenuDirectScanControls.this.stopService(new Intent(SubMenuDirectScanControls.this, DirectoryScanner.class));
													SubMenuDirectScanControls.this.stopService(new Intent(SubMenuDirectScanControls.this, FileDuplicationDetectionScanner.class));
													SubMenuDirectScanControls.this.stopService(new Intent(SubMenuDirectScanControls.this, FileDesignationScanner.class));
													
													
												}
											
											});
										}
										
										
									}
									
									@Override
									protected void onPostExecute(Void Result){
										super.onPostExecute(Result);
										
										
										
									}
						    		
						    	}.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
						}
			    		
			    	});
			    	
					
				}
				
				
			}else{
				
				// if this activity has been called Main/Sub Menu 
				
				setContentView(R.layout.direct_scan_controls);
				
				createServiceIntents();
				
				title = (TextView)findViewById(R.id.title);
				title.setText(title.getText().toString().toUpperCase());
				
				
				
				
				
				start = (Button) findViewById(R.id.scan);
				start.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SubMenuDirectScanControls.this.startService(dsIntent);
					}
					
				});
				stop = (Button) findViewById(R.id.stop);
				stop.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//SubMenuDirectScanControls.this.stopService(dsIntent);

						SubMenuDirectScanControls.this.stopService(new Intent(SubMenuDirectScanControls.this, DirectoryScanner.class));
						SubMenuDirectScanControls.this.stopService(new Intent(SubMenuDirectScanControls.this, FileDuplicationDetectionScanner.class));
						SubMenuDirectScanControls.this.stopService(new Intent(SubMenuDirectScanControls.this, FileDesignationScanner.class));
						
						
					}
				
				});
				
			}
			
			// ######################## ALPHA RELEASE 1.1.2 ########################
			
			
		}

		
		
		private void createServiceIntents(){
			this.dsIntent = new Intent(this, DirectoryScanner.class);
		}
		
		/* (non-Javadoc)
		 * @see android.app.Activity#onStart()
		 */
		@Override
		protected void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			
			if(LOGCAT){
				Log.d(TAG, this.getClass().getName() + " onStart()");
			}
		}

		/* (non-Javadoc)
		 * @see android.app.Activity#onRestart()
		 */
		@Override
		protected void onRestart() {
			// TODO Auto-generated method stub
			super.onRestart();
			if(LOGCAT){
				Log.d(TAG, this.getClass().getName() + " onRestart()");
			}
		}

		/* (non-Javadoc)
		 * @see android.app.Activity#onResume()
		 */
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			if(LOGCAT){
				Log.d(TAG, this.getClass().getName() + " onResume()");
			}
			
		}

		/* (non-Javadoc)
		 * @see android.app.Activity#onPause()
		 */
		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			if(LOGCAT){
				Log.d(TAG, this.getClass().getName() + " onPause()");
			}
			
		}

		/* (non-Javadoc)
		 * @see android.app.Activity#onStop()
		 */
		@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			super.onStop();
			this.db.close();
			if(LOGCAT){
				Log.d(TAG, this.getClass().getName() + " onStop()");
			}
		}

		/* (non-Javadoc)
		 * @see android.app.Activity#onDestroy()
		 */
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			this.db.close();
			if(LOGCAT){
				Log.d(TAG, this.getClass().getName() + " onDestroy()");
			}
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
				  ((SharpFixApplicationClass) getApplication()).resetAll();
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
