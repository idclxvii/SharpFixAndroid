package tk.idclxvii.sharpfixandroid;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import tk.idclxvii.sharpfixandroid.databasemodel.*;
import tk.idclxvii.sharpfixandroid.serverutils.MailSender;
import tk.idclxvii.sharpfixandroid.serverutils.ServerCommunication;
import tk.idclxvii.sharpfixandroid.utils.*;

import android.app.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.content.*;
import android.os.Bundle;
import android.os.Environment;
import android.text.*;
import android.util.*;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;
import android.widget.CompoundButton.*;

public class MainActivity extends GlobalExceptionHandlerActivity implements OnClickListener, TextWatcher, OnCheckedChangeListener{

	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private final String TAG = this.getClass().getSimpleName();
	private boolean LOGCAT;
		
	
	Button buttonStart, buttonStop, buttonCheck, buttonDelete;
	SQLiteHelper db;
	EditText desiredLogin;
	EditText desiredPass;
	EditText confirmPass;
	EditText username;
	EditText password;
	Button login;
	Button createAccount;
	CheckBox ch;
	
	public void restartActivity(){
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}
	  
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	  }
	  
	
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  " onCreate()");
		}
		
		
    	
		// initialize database connection
		db = this.getDb(getApplicationContext());//new SQLiteHelper(getApplicationContext());
			try{
				if(db.selectAll(Tables.accounts_info,ModelAccountsInfo.class, null).length > 0){
					if(this.LOGCAT){
						Log.d(this.TAG, "An account exists in this instance of SharpFix!");
					}
			    	// an account exist in this instance
			    	setContentView(R.layout.activity_main);
			    	username = (EditText) findViewById(R.id.username);
			    	password = (EditText) findViewById(R.id.password);
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

			    	login = (Button) findViewById(R.id.login);
			    	login.setOnClickListener(this);
			    	ch = (CheckBox) findViewById(R.id.autoLogin);
			    	ch.setOnCheckedChangeListener(this);
			    	ModelPreferences result = (ModelPreferences) this.db.selectAll(Tables.preferences, ModelPreferences.class,null)[0];
	        		((SharpFixApplicationClass) getApplication()).setAutoLogin(result.getAuto_login());

			    	if(((SharpFixApplicationClass) getApplication()).getAutoLogin() == 1){
			    		ch.setChecked(true);
			    		if(this.LOGCAT){
							Log.d(this.TAG, "Autologin feature activated! Immediately granting access to user");
						}
			    		((SharpFixApplicationClass) getApplication()).setAccountId(result.getAccount());
			    		((SharpFixApplicationClass) getApplication()).setFddPref(result.getFdd_pref());
		    			((SharpFixApplicationClass) getApplication()).setFddSwitch(result.getFdd_switch());
		    			((SharpFixApplicationClass) getApplication()).setFdSwitch(result.getFd_switch());
		    			((SharpFixApplicationClass) getApplication()).setFddFilterSwitch(result.getFdd_Filter_switch());
		    			((SharpFixApplicationClass) getApplication()).setFdFilterSwitch(result.getFd_Filter_switch());
		    			((SharpFixApplicationClass) getApplication()).setServiceSwitch(result.getSss_switch());
						((SharpFixApplicationClass) getApplication()).setServiceHour(result.getSss_hh());
						((SharpFixApplicationClass) getApplication()).setServiceMin(result.getSss_mm());
						((SharpFixApplicationClass) getApplication()).setServiceAMPM(result.getSss_ampm());
						((SharpFixApplicationClass) getApplication()).setServiceUpdateSwitch(result.getSss_update());
						((SharpFixApplicationClass) getApplication()).setServiceRepeat(result.getSss_repeat());
						((SharpFixApplicationClass) getApplication()).setServiceNoti(result.getSss_noti());
						((SharpFixApplicationClass) getApplication()).setAuSwitch(result.getAu_switch());
						((SharpFixApplicationClass) getApplication()).setEmail(result.getEmail());
						



		    			//startActivity(new Intent(this, MainMenuActivity.class));
		    			startActivityForResult(new Intent(this, MainMenuActivity.class), 1);
			    	}else{
			    		if(this.LOGCAT){
							Log.d(this.TAG, "Autologin feature inactive! Authentication is required to continue.");
						}
			    	}
			    	
			    	
			    	
			    }else{
			    	if(this.LOGCAT){
						Log.d(this.TAG, "No account has been detected in this instance of SharpFix");
					}
			    	setContentView(R.layout.main_no_account);
			    	
			    	new GlobalAsyncTask<Void, Void, Void>(){
			    		
			    		// first instance of SharpFix, scan all mounted dirs
			    		
			    		
						@Override
						protected Void doTask(Void... params) throws Exception {
							// TODO Auto-generated method stub
							File[] f = AndroidUtils.getMountedVolumesAsFile();
							for(File file : f){
								MainActivity.this.db.insert(Tables.sd_info, new ModelSD(file.getAbsolutePath(), file.lastModified()), null);
							}
							// MainActivity.this.db.insert(Tables.preferences, new ModelPreferences(1,0,1,0,0,0,0,0,0,0,0,7,1,1),null);
							/*
							 * 
							 * public ModelPreferences(Integer id, Integer account, Integer fddSw, Integer fdSw, Integer fddPref, Integer autoLogin,
			Integer fddFilterSwitch, Integer fdFilterSwitch, Integer srvcSw, Integer srvcHH, Integer srvcMM,
			Integer srvcAMPM, Integer srvcUSw, Integer srvcRepeat, Integer srvcNoti, Integer auSw){
		this.id = id;
		this.accountId = account;
		this.fddSwitch = fddSw;
		this.fdSwitch = fdSw;
		this.fdFilterSwitch = fdFilterSwitch;
		this.fddFilterSwitch = fddFilterSwitch;
		this.fddPref = fddPref;
		this.autoLogin = autoLogin;
		this.serviceSwitch = srvcSw;
		this.serviceHour = srvcHH;
		this.serviceMin = srvcMM;
		this.serviceAMPM = srvcAMPM;
		this.serviceUpdateSwitch = srvcUSw;
		this.serviceRepeat = srvcRepeat;
		this.auSwitch = auSw;
		this.serviceNoti = srvcNoti;
		
	}
		
	}
							 * 
							 * */
							SF.setAccountId(0);
							SF.setAutoLogin(0);
							SF.setFddPref(0);
							SF.setFddSwitch(1);
							SF.setFdSwitch(0);
							SF.setFdFilterSwitch(0);
							SF.setFdFilterSwitch(0);
							// new fields
							// Sss_switch, Sss_hh, Sss_mm, Sss_ampm, Sss_update, Sss_repeat, Au_switch
							
							SF.setServiceSwitch(1);
							SF.setServiceHour(12);
							SF.setServiceMin(0);
							SF.setServiceAMPM(1);
							SF.setServiceUpdateSwitch(1);
							SF.setServiceRepeat(7);
							SF.setServiceNoti(1);
							SF.setAuSwitch(1);
							//SF.setEmail();
							//SF.updatePreferences(db);
							
							MainActivity.this.startService(new Intent(MainActivity.this, DirectoryScanner.class));
							return null;
						}

						@Override
						protected void onException(Exception e) {
							// TODO Auto-generated method stub
							
						}
						
						
						
			    		
			    		
			    	}.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
			    	createAccount = (Button) findViewById(R.id.createAccount);
			    	createAccount.setOnClickListener(this);

			    	desiredLogin = (EditText) findViewById(R.id.desiredLogin);
					desiredPass = (EditText) findViewById(R.id.desiredPassword);
					confirmPass = (EditText) findViewById(R.id.confirmPassword);

					desiredLogin.setOnEditorActionListener(new OnEditorActionListener(){
			    		@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
			    			boolean handled = false;

			    			if(actionId == EditorInfo.IME_ACTION_NEXT){
			    				desiredPass.requestFocus();
			    				handled = true;
			    			}

			    			return handled;
						}

			    	});

					desiredPass.setOnEditorActionListener(new OnEditorActionListener(){
			    		@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
			    			boolean handled = false;

			    			if(actionId == EditorInfo.IME_ACTION_NEXT){
			    				confirmPass.requestFocus();
			    				handled = true;
			    			}

			    			return handled;
						}

			    	});

					confirmPass.setOnEditorActionListener(new OnEditorActionListener(){
			    		@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
			    			boolean handled = false;

			    			if(actionId == EditorInfo.IME_ACTION_DONE){
			    				createAccount.performClick();
			    				handled = true;
			    			}

			    			return handled;
						}

			    	});
					desiredLogin.addTextChangedListener(this);
					desiredPass.addTextChangedListener(this);
					confirmPass.addTextChangedListener(this);

					 try{
						 if(desiredPass.getText().toString().length() > 4 && desiredLogin.getText().toString().length() > 4 
			    				  && desiredPass.getText().toString().equals(confirmPass.getText().toString()) && 
			    				 ! (desiredPass.getText().toString().isEmpty() && confirmPass.getText().toString().isEmpty()) &&
			    				 ! desiredLogin.getText().toString().isEmpty() ){
							createAccount.setEnabled(true);
			    		  }else{
			    			  createAccount.setEnabled(false); 
			    		  }
			    	  }catch(Exception e){
			    		  createAccount.setEnabled(false); 
			    	  }

			    }	 
			  }catch(Exception e){
				  if(LOGCAT){
		    			StackTraceElement[] st = e.getStackTrace();
						for(int y= 0; y <st.length; y++){
							Log.w(TAG, st[y].toString());

						}
		    		}
			  }
			    db.closeConnection();
			   
				
	  	}

	
	@Override
	public void onStart(){
		super.onStart();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  " onStart()");
		}
		/*
		 BEBE Device: Sony Experia Go ST27i
		 Note for codes: 
		 	Sony Experia locks the Internal Storage when connected via Android Debug Bridge (adb)
		 	causing the {Active and Mounted Volumes} Detection to catch the SD-Card as the 
		 	only mounted device {SINGLE VOLUME DETECTED}. Even though it outputs SECONDARY STORAGE, this might 
		 	lead to confusion. Always note of the given Type (PRIMARY, SECONDARY) of the storage. 
		
		String log = "";
		for(String str : AndroidUtils.getMountedVolumes()){
			log += (str+"\n");
		}
		Toast.makeText(this, log, Toast.LENGTH_LONG).show();
		
		*/
		
		
	}
	
	
	  @Override
	  public void onResume(){
		  try{
			  if(((SharpFixApplicationClass) getApplication()).getAutoLogin() == 1){
				  ch.setChecked(true);
			  }else{
				  ch.setChecked(false);
			  }
		  }catch(Exception e){
			  // No database data
		  }
			  super.onResume();
			  if(this.LOGCAT){
					Log.d(this.TAG, this.TAG +  " onResume()");
				}
	  }
	  
	  @Override
	  public void onStop(){
		  super.onStop();
		  if(this.LOGCAT){
				Log.d(this.TAG, this.TAG +  " onStop()");
			}
	  }

	  @Override
	  public void onRestart(){
		  super.onRestart();
		  if(this.LOGCAT){
				Log.d(this.TAG, this.TAG +  " onRestart()");
			}
		  
	  }
	  
	  @Override
	  public void onDestroy(){
		  super.onDestroy();
		  this.db.close();
		  if(this.LOGCAT){
				Log.d(this.TAG, this.TAG +  " onDestroy()");
			}
	  }
	  

	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data){
		  if(resultCode == 1){
			  finish();
		  }
		  super.onActivityResult(requestCode, resultCode, data);
		  
	  }
	  
	  public void onClick(View src) {
		  db = this.getDb(getApplicationContext());//new SQLiteHelper(getApplicationContext());
			
		  switch (src.getId()) {
	    
			case R.id.createAccount:
				//this.db.insertAccountInfo(new ModelAccountsInfo(desiredLogin.getText().toString(),
					//	this.md5Hash(desiredPass.getText().toString())));
				
				new GlobalAsyncTask<Void,Void,Void>(this, "Loading","Creating your account, please wait . . ."){

					@Override
					protected Void doTask(Void... params) throws Exception {
						// TODO Auto-generated method stub
						try{
							if(MainActivity.this.db.insert(Tables.accounts_info, new ModelAccountsInfo(desiredLogin.getText().toString(),
									Security.md5Hash(desiredPass.getText().toString())), null )){
								ModelAccountsInfo mai = (ModelAccountsInfo) MainActivity.this.db.select(Tables.accounts_info, ModelAccountsInfo.class, 
										new Object[][]{{"login",desiredLogin.getText().toString()}, {"password", Security.md5Hash(desiredPass.getText().toString())}}, null);
								MainActivity.this.db.insert(Tables.preferences, new ModelPreferences(mai.getId(), 1,0,1,0,0,0,1,12,0,1,1,7,1,1),null);
								
								/*
								 * public ModelPreferences(Integer account, Integer fddSw, Integer fdSw, Integer fddPref, Integer autoLogin,
			Integer fddFilterSwitch, Integer fdFilterSwitch, Integer srvcSw, Integer srvcHH, Integer srvcMM,
			Integer srvcAMPM, Integer srvcUSw, Integer srvcRepeat, Integer srvcNoti, Integer auSw){
								 * 
								 * */
								
								
								if(MainActivity.this.LOGCAT){
									Log.d(MainActivity.this.TAG, "An account has been successfully created!");
								}
								//Toast.makeText(this, "Successfully created an account!", Toast.LENGTH_LONG).show();

								MainActivity.this.restartActivity();
							}else{
								if(MainActivity.this.LOGCAT){
									Log.w(TAG, "SQL FAILED: INSERT INTO accounts_info (login, password) VALUES("+desiredLogin.getText().toString()+", "+
										desiredPass.getText().toString()+")");
								}
								/*Toast.makeText(this, "Failed creating an account! Please be sure that no SQL Injection is being performed as it will" +
										" be automatically blocked by SharpFix!", Toast.LENGTH_LONG).show();
										*/
							}
						}catch(Exception e){
							if(MainActivity.this.LOGCAT){
								Log.w(TAG, "EXCEPTION OCCURED! SQL FAILED: INSERT INTO accounts_info (login, password) VALUES("+desiredLogin.getText().toString()+", "+
									desiredPass.getText().toString()+")");
							}
						}
						return null;
					}

					@Override
					protected void onException(Exception e) {
						// TODO Auto-generated method stub
						
					}
					
				}.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
				
				
				
				/*
				startService(new Intent(this, FileDesignationService.class));
				startService(new Intent(this, FileDuplicationDetectionService.class));
				*/
				
				
		      
		      
		      break;
		    case R.id.login:
		    	new GlobalAsyncTask<Void,EditText,Void>(this, "Loading","Checking account, please wait . . ."){

					@Override
					protected Void doTask(Void... params) throws java.lang.reflect.InvocationTargetException,
					NoSuchMethodException, IllegalAccessException, InstantiationException{
						// TODO Auto-generated method stub
						//try{
				    		ModelAccountsInfo mai = (ModelAccountsInfo) MainActivity.this.db.select(Tables.accounts_info, ModelAccountsInfo.class,
				    				new Object[][]{{"login",username.getText().toString()}}, null);//this.db.getAccountInfo(username.getText().toString());
				    		if(mai.getPassword().equals(Security.md5Hash(password.getText().toString()))){
				    			//Toast.makeText(this, "Welcome "+ mai.getLogin()+" !", Toast.LENGTH_LONG).show();
				    			//i.putExtra("accountId", mai.getId());
				    			((SharpFixApplicationClass) getApplication()).setAccountId(mai.getId());
				    			try{
				    				ModelPreferences oldParams = (ModelPreferences) MainActivity.this.db.select(Tables.preferences, ModelPreferences.class, new Object[][] {{"account",
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
					    				MainActivity.this.db.update(Tables.preferences, oldParams, newParams, null);
					    				((SharpFixApplicationClass) getApplication()).updatePreferences(MainActivity.this.db);
				    				}
				    				
				    			
				    			}catch(Exception e){
				    				
				    			}
				    			//startActivity(new Intent(this, MainMenuActivity.class));
				    			if(MainActivity.this.LOGCAT){
									Log.d(MainActivity.this.TAG, "Login successful! Access granted.");
								}
				    			MainActivity.this.startActivityForResult(new Intent(MainActivity.this, MainMenuActivity.class), 1);
				    			
				    			publishProgress(new EditText[]{password,username});
				    			/*
				    			username.setText(null);
				    			password.setText(null);
				    			*/
				    		}else{
				    			//Toast.makeText(this, "Incorrect password!", Toast.LENGTH_LONG).show();
				    			if(MainActivity.this.LOGCAT){
									Log.d(MainActivity.this.TAG, "Login failure! Access denied!");
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
						if(MainActivity.this.LOGCAT){
							// e.printStackTrace();
							Log.d(MainActivity.this.TAG, "Username does not exist! Login failure!");
							/*
							 * The code below produces a NullPointerException, from e.getCause()
							 * However, it is not yet removed for purposes of demonstrating how
							 * the GlobalExceptionHandlerActivity functions by using a non existing
							 * username as an input in Login screen.
							 */
							Log.d(MainActivity.this.TAG, "Exception cause: " +e.getCause().toString());
							
							
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
					}
		    		
		    	}.executeOnExecutor(tk.idclxvii.sharpfixandroid.utils.AsyncTask.THREAD_POOL_EXECUTOR);
		    	
		    	/*
		    	stopService(new Intent(this, FileDesignationService.class));
		  		stopService(new Intent(this, FileDuplicationDetectionService.class));
		    	*/
		      break;
		  }

		  db.closeConnection();
	 }

	      @Override
	      public void onTextChanged(CharSequence s, int start, int before, int count) {
	    	  try{
	    		  if(desiredPass.getText().toString().length() > 4 && desiredLogin.getText().toString().length() > 4 
	    				  && desiredPass.getText().toString().equals(confirmPass.getText().toString()) && 
	    				 ! (desiredPass.getText().toString().isEmpty() && confirmPass.getText().toString().isEmpty()) &&
	    				 ! desiredLogin.getText().toString().isEmpty() ){
					createAccount.setEnabled(true);
	    		  }else{
	    			  createAccount.setEnabled(false); 
	    			  
	    		  }
	    	  }catch(Exception e){
	    		  createAccount.setEnabled(false); 
	    	  }
	      }

	      @Override
	      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	      }

	      @Override
	      public void afterTextChanged(Editable s) {

	      }

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(ch.isChecked()){
				((SharpFixApplicationClass) getApplication()).setAutoLogin(1);
				
			}else{
				((SharpFixApplicationClass) getApplication()).setAutoLogin(0);
				
			}
			
		}

	      
	  
} // end of class

