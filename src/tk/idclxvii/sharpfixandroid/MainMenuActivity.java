package tk.idclxvii.sharpfixandroid;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Locale; // used on API 10 above: .toString(Locale)

import android.app.*;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.*;
import android.widget.*;
import tk.idclxvii.sharpfixandroid.databasemodel.*;
import tk.idclxvii.sharpfixandroid.utils.AndroidUtils;
import tk.idclxvii.sharpfixandroid.utils.Logcat;

public class MainMenuActivity extends GlobalExceptionHandlerActivity implements OnClickListener{
	
	
	
	private SharpFixApplicationClass SF;
	private final String TAG = this.getClass().getSimpleName();
	private boolean LOGCAT;
	
	private TextView title;
	private TextView fdds;
	private TextView fddl;
	private TextView fds;
	private TextView fdl;
	private TextView filters;
	private TextView filtersl;
	private TextView services;
	private TextView servicesl;
	private TextView abouts;
	private TextView aboutl;
	
	private SQLiteHelper db;
	private long backPressed = 0;

	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	  }
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getDb(getApplicationContext());
		setContentView(R.layout.main_menu);
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onCreate()");
		}
		title = (TextView)findViewById(R.id.title);
		title.setText(title.getText().toString().toUpperCase(Locale.getDefault()));
		
		fdds = (TextView) findViewById(R.id.selection1);
		fdds.setOnClickListener(MainMenuActivity.this);
		fddl = (TextView)findViewById(R.id.label1);
		fddl.setOnClickListener(MainMenuActivity.this);
		
		fds = (TextView) findViewById(R.id.selection2);
		fds.setOnClickListener(MainMenuActivity.this);
		fdl = (TextView) findViewById(R.id.label2);
		fdl.setOnClickListener(MainMenuActivity.this);
		
		filters = (TextView) findViewById(R.id.selection3);
		filters.setOnClickListener(MainMenuActivity.this);
		filtersl = (TextView) findViewById(R.id.label3);
		filtersl.setOnClickListener(MainMenuActivity.this);
		
		services = (TextView) findViewById(R.id.selection4);
		services.setOnClickListener(MainMenuActivity.this);
		servicesl = (TextView) findViewById(R.id.label4);
		servicesl.setOnClickListener(MainMenuActivity.this);
		
		abouts = (TextView) findViewById(R.id.selection5);
		abouts.setOnClickListener(MainMenuActivity.this);
		aboutl = (TextView) findViewById(R.id.label5);
		aboutl.setOnClickListener(MainMenuActivity.this);
		
		// DEVELOPER MODE:
		if(MainMenuActivity.this.SF.getDevMode()){
			abouts.setOnLongClickListener(new OnLongClickListener(){
	
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Dialog d = new Dialog(MainMenuActivity.this);
					d.setContentView(R.layout.dev_mode /*fd_sub_menu_rules*/);
					
					d.setTitle("{ } DEVELOPER MODE " + (MainMenuActivity.this.SF.getRootAccess() ? "root" : "h4x0r"));
					
					//TextView title = (TextView) d.findViewById(R.id.title);
					//title.setText("Active and Mounted Volumes Detection");
					//TextView noRules = (TextView) d.findViewById(R.id.noRules);
					//View noRulesHr = (View) d.findViewById(R.id.noRulesHr);
					TextView volumesNote = (TextView) d.findViewById(R.id.selection1);
					//noRules.setVisibility(View.GONE);
					//noRulesHr.setVisibility(View.GONE);
					ListView VOLUMES = (ListView) d.findViewById(R.id.listViewVolumes);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainMenuActivity.this,
							R.layout.custom_textview);
					String[] storage = AndroidUtils.getMountedVolumes();
					for(int i = 0; i < storage.length; i++){
						
						if(storage.length > 2){
							// multiple storage detected
							if(i == 0){
								volumesNote.setText(storage[i]);
							}else if( i > 1){
								adapter.add(storage[i]);
							}
							
						}else{
							if(i == 0){
								volumesNote.setText(storage[i]);
							}else{
								adapter.add(storage[i]);
							}
						}
					}
					
					VOLUMES.setAdapter(adapter);
					VOLUMES.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							try{
								final String selection = ((String)parent.getItemAtPosition(position));
								final String dir = selection.substring(selection.indexOf("/"), selection.length());
								FileDialog f = new FileDialog(MainMenuActivity.this, new File(dir), true);
								f.addFileListener(new FileDialog.FileSelectedListener(){
	
									@Override
									public void fileSelected(File file) {
										// TODO Auto-generated method stub
										try{
											tk.idclxvii.sharpfixandroid.utils.AndroidUtils.openFile(MainMenuActivity.this, file);
										}catch(Exception e){
											tk.idclxvii.sharpfixandroid.utils.Logcat.logCaughtException(
													MainMenuActivity.this, e.getStackTrace());
										}
									}
									
								});
								f.setSelectDirectoryOption(false);
								f.createFileDialog();
								
							}catch(Exception e){
								if(LOGCAT){
					    			Logcat.logCaughtException(MainMenuActivity.this, e.getStackTrace());
					    		}
								
							}
						}
	
						
					});
					
					TextView titleDatabase = (TextView) d.findViewById(R.id.titleDatabase);
					//noRules.setVisibility(View.GONE);
					//noRulesHr.setVisibility(View.GONE);
					ListView DATABASE = (ListView) d.findViewById(R.id.listViewDatabase);
					ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(MainMenuActivity.this,
							R.layout.custom_textview);
					adapter2.add("Import Database");
					adapter2.add("Export Database");
					adapter2.add("Drop Database");
					
					
					DATABASE.setAdapter(adapter2);
					DATABASE.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							final int selection = position;
							Log.i(TAG,"Selected item: " + ((String)parent.getItemAtPosition(selection)));
							switch(selection){
							
							case 0:
								// import
								importDB();
								
								
								break;
							case 1:
								// xport
								exportDB();
								break;
								
							case 2:
								// delete
								deleteDB();
								break;
							}
							
						}
	
						final private void deleteDB(){
							Log.i(TAG, 
									( MainMenuActivity.this.deleteDatabase(SQLiteHelper.getDbName())? "Successfully deleted database!":
										"ERROR! Database was not successfully deleted!"));
						}
						
						final private void exportDB(){
							FileDialog f = new FileDialog(MainMenuActivity.this, new File(""), true);
							f.addDirectoryListener(new FileDialog.DirectorySelectedListener(){
								
								@Override
								public void directorySelected(File directory) {
									// TODO Auto-generated method stub
									try{
										FileChannel source = null;
									    FileChannel destination = null;
									    String currentDBPath = SF.getDbFileDirPath()+ "/" + SQLiteHelper.getDbName(); //"/data/"+ "com.authorwjf.sqliteexport" +"/databases/"+SAMPLE_DB_NAME;
									    
									    File currentDB = new File(currentDBPath);
									    File backupDB = new File(directory.getAbsolutePath(), "export.db");
									    
									    
									    try {
									    	source = new FileInputStream(currentDB).getChannel();
									        destination = new FileOutputStream(backupDB).getChannel();
									        destination.transferFrom(source, 0, source.size());
									        source.close();
									        destination.close();

									    	Log.i(TAG, "Database successfully exported to: " + directory.getAbsolutePath());
									    }catch(IOException e) {
									    	Log.i(TAG, "Database exported failed!" );
									    	e.printStackTrace();
									    }
										
									}catch(Exception e){
										tk.idclxvii.sharpfixandroid.utils.Logcat.logCaughtException(
												MainMenuActivity.this, e.getStackTrace());
									}
								}

								
								
							});
							// set as dir browser
							f.setSelectDirectoryOption(true);
							f.createFileDialog();
							
						}
						
						final private void importDB(){
							FileDialog f = new FileDialog(MainMenuActivity.this, new File(""), true);
							f.addFileListener(new FileDialog.FileSelectedListener(){
								
								@Override
								public void fileSelected(File file) {
									// TODO Auto-generated method stub
									try{
										FileChannel source = null;
									    FileChannel destination = null;
									    String currentDBPath = SF.getDbFileDirPath()+ "/" +SQLiteHelper.getDbName(); //"/data/"+ "com.authorwjf.sqliteexport" +"/databases/"+SAMPLE_DB_NAME;
									    
									    File currentDB = new File(currentDBPath);
									    File importDB = new File(file.getAbsolutePath());
									    
									    
									    try {
									    	source = new FileInputStream(importDB).getChannel();
									        destination = new FileOutputStream(currentDB).getChannel();
									        destination.transferFrom(source, 0, source.size());
									        source.close();
									        destination.close();

									    	Log.i(TAG, "Database "+ file.getAbsolutePath() + "successfully imported to: " + currentDBPath);
									    }catch(IOException e) {
									    	Log.i(TAG, "Database import failed!" );
									    	e.printStackTrace();
									    }
									}catch(Exception e){
										tk.idclxvii.sharpfixandroid.utils.Logcat.logCaughtException(
												MainMenuActivity.this, e.getStackTrace());
									}
								}
								
							});
							// set as file browser
							f.setSelectDirectoryOption(false);
							f.createFileDialog();
							
						
						}
					});
					
					d.show();
					return true;
					
					
				}
				
			});
			
			aboutl.setOnLongClickListener(new OnLongClickListener(){
	
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					abouts.performLongClick();
					return true;
				}
				
			});
		}
		
		
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onStart()");
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onResume()");
		}
		
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG + "onPause()");
		}
	}
	
	@Override
	public void onStop(){
		//setResult(((SharpFixApplicationClass)getApplication()).getAutoLogin());
		super.onStop();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG + "onStop()");
		}
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG + "onRestart()");
		}
	}
	
	@Override
	public void onDestroy(){
		//setResult(((SharpFixApplicationClass)getApplication()).getAutoLogin());
		super.onDestroy();
		this.db.close();
		if(LOGCAT){
			Log.d(TAG, this.TAG + " onDestroy()");
		}
		
	}
	
	
	@Override
	public void onBackPressed(){
		if (this.backPressed + 2000 > System.currentTimeMillis()){
			setResult(((SharpFixApplicationClass)getApplication()).getAutoLogin());
			finish();
		}else{
			Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
		}
        this.backPressed = System.currentTimeMillis();
        if(LOGCAT){
			Log.d(TAG, this.TAG + " onBackPressed()");
		}
	}
	
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu){
		  MenuInflater mi = getMenuInflater();
		  mi.inflate(R.menu.main, menu);
		  if(LOGCAT){
				Log.d(TAG, this.TAG + " onCreateOptionsMenu()");
			}
		  return true;
	  }
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item){
		  // handle item selection:
		  switch (item.getItemId()){
		  
		  
		  case R.id.MenuLogout:
			// call method / do task.
			  ModelPreferences oldParams = new ModelPreferences( ((SharpFixApplicationClass) getApplication()).getAccountId(),
						((SharpFixApplicationClass) getApplication()).getFddSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdSwitch(),
						((SharpFixApplicationClass) getApplication()).getFddPref(),
						((SharpFixApplicationClass) getApplication()).getAutoLogin(),
						((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdFilterSwitch(),
						
						// new fields
						// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
						((SharpFixApplicationClass) getApplication()).getServiceSwitch(),
						((SharpFixApplicationClass) getApplication()).getServiceHour(),
						((SharpFixApplicationClass) getApplication()).getServiceMin(),
						((SharpFixApplicationClass) getApplication()).getServiceAMPM(),
						((SharpFixApplicationClass) getApplication()).getServiceUpdateSwitch(),
						((SharpFixApplicationClass) getApplication()).getServiceRepeat(),
						((SharpFixApplicationClass) getApplication()).getServiceNoti(),
						((SharpFixApplicationClass) getApplication()).getAuSwitch()
						
						);
			  
			  ModelPreferences newParams = new ModelPreferences( ((SharpFixApplicationClass) getApplication()).getAccountId(),
						((SharpFixApplicationClass) getApplication()).getFddSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdSwitch(),
						((SharpFixApplicationClass) getApplication()).getFddPref(),
						((SharpFixApplicationClass) getApplication()).getAutoLogin(),
						((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdFilterSwitch(),
						
						// new fields
						// ServiceSwitch, ServiceHour, ServiceMin, ServiceAMPM, ServiceUpdateSwitch, ServiceRepeat, AuSwitch
						((SharpFixApplicationClass) getApplication()).getServiceSwitch(),
						((SharpFixApplicationClass) getApplication()).getServiceHour(),
						((SharpFixApplicationClass) getApplication()).getServiceMin(),
						((SharpFixApplicationClass) getApplication()).getServiceAMPM(),
						((SharpFixApplicationClass) getApplication()).getServiceUpdateSwitch(),
						((SharpFixApplicationClass) getApplication()).getServiceRepeat(),
						((SharpFixApplicationClass) getApplication()).getServiceNoti(),
						((SharpFixApplicationClass) getApplication()).getAuSwitch()
					  );
			  
			  // trade-offs
			  newParams.setAuto_login(0);
			  
			  try{
				  this.db.update(Tables.preferences, oldParams, newParams, null);
				  ((SharpFixApplicationClass) getApplication()).updatePreferences(this.db);
				  
			  }catch(Exception e){}
			  Intent i = new Intent(this,MainActivity.class);
			  i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			  startActivity(i);
			  finish();
			  return true;
			  
		  default:
			  return super.onOptionsItemSelected(item);
				  
		  }
	  }
	
	public void onClick(View src) {
		switch (src.getId()) {
	    
		
		// fdd
		case R.id.selection1:
			startActivity(new Intent(this, SubMenuFddActivity.class));
			break;
			
		case R.id.label1:
			fdds.setPressed(true);
			fdds.performClick();
			break;
			
		// fd
		case R.id.selection2:
			startActivity(new Intent(this, SubMenuFdActivity.class));
			break;
		
		case R.id.label2:
			fds.setPressed(true);
			fds.performClick();
			break;
		// filters
		case R.id.selection3:
			startActivity(new Intent(this, SubMenuFiltersActivity.class));
			break;
		
			
			
		case R.id.label3:
			filters.setPressed(true);
			filters.performClick();
			break;
			
		// services
		case R.id.selection4:
			startActivity(new Intent(this, SubMenuServicesActivity.class));
			break;
			
		case R.id.label4:
			services.setPressed(true);
			services.performClick();
			break;



		// about
		case R.id.selection5:
			startActivity(new Intent(this, SubMenuAboutActivity.class));
			break;
			
		case R.id.label5:
			abouts.setPressed(true);
			abouts.performClick();
			break;
				
				
		}
	}
}
