package tk.idclxvii.sharpfixandroid;


import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.*;

import tk.idclxvii.sharpfixandroid.FileDialog.FileSelectedListener;
import tk.idclxvii.sharpfixandroid.databasemodel.*;

import java.io.*;
import java.util.*;

public class SubMenuFdRulesActivity extends Activity implements
		OnClickListener, OnCheckedChangeListener,
		/*FolderDialog.ChosenDirectoryListener, */ FileDialog.DirectorySelectedListener {

	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	
	boolean onEditRule = false;
	String onEditTitle = "";
	private ModelFdSettings oldParams;
	SQLiteHelper db;
	private RelativeLayout RL;
	private ListView RULES;
	TextView noRules;
	View noRulesHr;
	TextView createRule;
	View hr2;
	
	Dialog dialog, holdDialog;
	EditText ruleName;
	TextView choose; 
	TextView designationDir;
	Button chooseFileType;
	Button browseDir;
	Button positive; 
	Button negative; 
	

	private List<String> noDupes;
	private ListView lv;
	private Dialog fileTypeDialog;
	
	private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		return this.db;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.db = this.getDb(getApplicationContext());
		this.TAG = this.getClass().getName().replace(this.getPackageName(), "");
		this.SF = ((SharpFixApplicationClass) getApplication() );
		this.LOGCAT = this.SF.getLogCatSwitch();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onCreate()");
		}
		setContentView(R.layout.fd_sub_menu_rules);
		RL = (RelativeLayout) findViewById(R.id.Rules);
		
		noRules = (TextView) findViewById(R.id.noRules);
		noRulesHr = (View) findViewById(R.id.noRulesHr);
		createRule = (TextView) findViewById(R.id.createRule);
		createRule.setOnClickListener(this);
		createRule.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// 2TODO Auto-generated method stub
				Toast.makeText(
		            	SubMenuFdRulesActivity.this,"Developer Mode currently disabled!", Toast.LENGTH_LONG).show();
				return false;
			}
			
		});
		hr2 = (View) findViewById(R.id.hr2);
		this.RULES = (ListView) findViewById(R.id.listViewRules);
		
		try{
			Object [] r = this.db.selectAll(Tables.file_designation_settings, ModelFdSettings.class, null);
			if(r.length > 0){
			
				noRules.setVisibility(View.GONE);
				noRulesHr.setVisibility(View.GONE);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						R.layout.custom_textview);
				for(Object f : r){
					adapter.add(((ModelFdSettings)f).getRule_name() + "\n" + ((ModelFdSettings)f).getFile_type());
					
				}
				this.RULES.setAdapter(adapter);
				RULES.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						
						try{
							final String selection = ((String)parent.getItemAtPosition(position));
							final String fileType = selection.substring(selection.indexOf("\n")+1);
							final String ruleName = selection.substring(0, selection.indexOf("\n"));
							final Object rule = SubMenuFdRulesActivity.this.db.select(Tables.file_designation_settings, ModelFdSettings.class,
								new Object[][]{{"file_type", fileType}}, null);
							SubMenuFdRulesActivity.this.oldParams = (ModelFdSettings) rule;
							SubMenuFdRulesActivity.this.holdDialog = new Dialog(SubMenuFdRulesActivity.this);
							SubMenuFdRulesActivity.this.holdDialog.setContentView(R.layout.rules_dialog);
							SubMenuFdRulesActivity.this.holdDialog.setTitle(ruleName);
							Button edit = (Button) SubMenuFdRulesActivity.this.holdDialog.findViewById(R.id.editRule);
							Button delete = (Button) SubMenuFdRulesActivity.this.holdDialog.findViewById(R.id.deleteRule);
							SubMenuFdRulesActivity.this.holdDialog .setCancelable(true);
								
							edit.setOnClickListener(new OnClickListener(){
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									SubMenuFdRulesActivity.this.onEditRule = true;
									SubMenuFdRulesActivity.this.onEditTitle = fileType;
									SubMenuFdRulesActivity.this.createRule.performClick();
									SubMenuFdRulesActivity.this.dialog.setTitle(fileType);
									SubMenuFdRulesActivity.this.ruleName.setText(((ModelFdSettings)rule).getRule_name());
									SubMenuFdRulesActivity.this.choose.setText("Target File Type:\n"+((ModelFdSettings)rule).getFile_type());
									SubMenuFdRulesActivity.this.designationDir.setText("Target Designation Directory:"+"\n" + ((ModelFdSettings)rule).getDesignation_path());
									SubMenuFdRulesActivity.this.positive.setText("Update Rule");
									SubMenuFdRulesActivity.this.chosenDir = ((ModelFdSettings)rule).getDesignation_path();
									SubMenuFdRulesActivity.this.chosenMagicNumber = ((ModelFdSettings)rule).getFile_type();
									
									
								}
								
							});
							
							delete.setOnClickListener(new OnClickListener(){

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									final Dialog d = new Dialog(SubMenuFdRulesActivity.this);
									d.setContentView(R.layout.rules_dialog);
									final Button yes = (Button) d.findViewById(R.id.editRule);
									yes.setText("Yes, delete this rule");
									final Button no = (Button) d.findViewById(R.id.deleteRule);
									no.setText("No, I was just testing this stuff");
									d.setTitle("Confirm Deletion");
								
									no.setOnClickListener(new OnClickListener(){

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											d.dismiss();
										}
										
									});
									
									yes.setOnClickListener(new OnClickListener(){

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											try{
												if(SubMenuFdRulesActivity.this.db.delete(Tables.file_designation_settings,
														SubMenuFdRulesActivity.this.oldParams, null)){
													Log.d(TAG, "Successfully deleted the rule: ");
												}else{
													Log.e(TAG, "Failed deleting the rule: ");
												}
												d.dismiss();
												SubMenuFdRulesActivity.this.holdDialog.dismiss();
												SubMenuFdRulesActivity.this.onEditRule = false;
												finish();
												startActivity(getIntent());
											}catch(Exception e){
												if(LOGCAT){
									    			StackTraceElement[] st = e.getStackTrace();
													for(int y= 0; y <st.length; y++){
														Log.w(TAG, st[y].toString());
													}
									    		}
											}
										}
										
									});
									
									d.setOnCancelListener(new OnCancelListener(){

										@Override
										public void onCancel(
												DialogInterface dialog) {
											// TODO Auto-generated method stub
											d.dismiss();
										}
										
									});
									d.show();
								}
								
							});
							SubMenuFdRulesActivity.this.holdDialog.setOnCancelListener(new OnCancelListener(){

								@Override
							
								public void onCancel(DialogInterface dialog) {
									// TODO Auto-generated method stub
									SubMenuFdRulesActivity.this.onEditRule = false;
								}
								
							});
							SubMenuFdRulesActivity.this.holdDialog.show();
							
						
						}catch(Exception e){
							if(LOGCAT){
				    			StackTraceElement[] st = e.getStackTrace();
								for(int y= 0; y <st.length; y++){
									Log.w(TAG, st[y].toString());
								}
				    		}
			
						}
						return false;
					}
					
				});
				RULES.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						try{
							final String selection = ((String)parent.getItemAtPosition(position));
							final String fileType = selection.substring(selection.indexOf("\n")+1);
							final String ruleName = selection.substring(0, selection.indexOf("\n"));
							final Object rule = SubMenuFdRulesActivity.this.db.select(Tables.file_designation_settings, ModelFdSettings.class,
								new Object[][]{{"file_type", fileType}}, null);
							Intent i = new Intent(SubMenuFdRulesActivity.this, ViewRuleActivity.class);
							i.putExtra("Instance", "fd");
							i.putExtra("RuleName", ((ModelFdSettings)rule).getRule_name());
							i.putExtra("FileType", ((ModelFdSettings)rule).getFile_type());
							i.putExtra("Designation", ((ModelFdSettings)rule).getDesignation_path());
							startActivity(i);
						}catch(Exception e){
							if(LOGCAT){
				    			StackTraceElement[] st = e.getStackTrace();
								for(int y= 0; y <st.length; y++){
									Log.w(TAG, st[y].toString());
								}
				    		}
							
						}
					}
					
				});
			}else{
				// no rules are defined, 
			//	selection1.setVisibility(View.GONE);
			//	label1.setVisibility(View.GONE);
				RULES.setVisibility(View.GONE);
				hr2.setVisibility(View.GONE);
			}
		}catch(Exception e){
			if(LOGCAT){
    			StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.w(TAG, st[y].toString());
				}
    		}
		}
		
		
		try{
			// set the custom dialog components - text, image and button
			
			
		}catch(Exception e){}
		
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		if(LOGCAT){
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
			Log.d(this.TAG, this.TAG +  "onPause()");
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		this.db.close();
		if(this.LOGCAT){
			Log.d(this.TAG, this.TAG +  "onDestroy()");
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
			  ModelPreferences oldParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
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
						((SharpFixApplicationClass) getApplication()).getAuSwitch());
			  ModelPreferences newParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
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
						((SharpFixApplicationClass) getApplication()).getAuSwitch());
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
	
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		
	}
	
	
	String chosenDir = "";
	String chosenMagicNumber = null;
	

	@Override
	public void onClick(View src) {
		
		switch (src.getId()){
			case R.id.createRule:
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.fd_create_rule);
			if(!this.onEditRule){
				dialog.setTitle("Create New Rule");
			}
			ruleName = (EditText) dialog.findViewById(R.id.ruleName);
			choose  = (TextView) dialog.findViewById(R.id.choose);
			designationDir = (TextView) dialog.findViewById(R.id.designationDir);
			chooseFileType = (Button) dialog.findViewById(R.id.chooseFileType);
			browseDir = (Button) dialog.findViewById(R.id.chooseDir);
			positive = (Button) dialog.findViewById(R.id.positiveButton);
			negative = (Button) dialog.findViewById(R.id.negativeButton);	
			
			
			fileTypeDialog = new Dialog(SubMenuFdRulesActivity.this);
			fileTypeDialog.setContentView(R.layout.file_types_dialog);
			fileTypeDialog.setCancelable(true);
			fileTypeDialog.setTitle("Choose File Type");
			SubMenuFdRulesActivity.this.chosenDir = null;
			SubMenuFdRulesActivity.this.chosenMagicNumber = null;
			
			
			
			
			chooseFileType.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// query database here and outout another dialog which lists down the file types taken from the query
						try{
							//################################################################################
							// CURRENT PROBLEM
							//################################################################################
							
							Object[] bridge = SubMenuFdRulesActivity.this.db.selectAll(Tables.magic_number, ModelMagicNumber.class, null);
							List<String> fileTypes = new ArrayList<String>();
							ArrayAdapter<String> adapter = new ArrayAdapter<String>(SubMenuFdRulesActivity.this,
									android.R.layout.simple_list_item_1);
								
							for(int x =0; x < bridge.length; x++){
								fileTypes.add(((ModelMagicNumber)bridge[x]).getFile_type().toString());
								
							}
							SubMenuFdRulesActivity.this.noDupes = new ArrayList<String>(new LinkedHashSet<String>(fileTypes));
							
							for(String str :SubMenuFdRulesActivity.this.noDupes){
								adapter.add(str);
							}
							SubMenuFdRulesActivity.this.lv = (ListView) fileTypeDialog.findViewById(R.id.lv);
							SubMenuFdRulesActivity.this.lv.setAdapter(adapter);
							SubMenuFdRulesActivity.this.lv.setOnItemClickListener(new OnItemClickListener(){

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									String str = (String)parent.getItemAtPosition(position);
									
									   Toast.makeText(SubMenuFdRulesActivity.this, str,
											   Toast.LENGTH_SHORT).show();
									try{
										SubMenuFdRulesActivity.this.chosenMagicNumber = null;
										/*
										Object[] bridge = SubMenuFdRulesActivity.this.db.selectMulti(Tables.magic_number, ModelMagicNumber.class,
										new Object[][]{{"file_type",str}}, null);
										for(Object m : bridge){
											Log.i(TAG, "File Type: "+((ModelMagicNumber)m).getFile_type());
											Log.i(TAG, "4-bytes Signature: "+((ModelMagicNumber)m).getSignature_4_bytes());
											Log.i(TAG, "8-bytes Signature: "+((ModelMagicNumber)m).getSignature_8_bytes());
											Log.i(TAG, "MIME: "+((ModelMagicNumber)m).getMime());
											
										}
										*/
										SubMenuFdRulesActivity.this.chosenMagicNumber = str;
										choose.setText("Target File Type:\n"+ str);
										fileTypeDialog.dismiss();
										
									}catch(Exception e){
										if(LOGCAT){
				    		    			StackTraceElement[] st = e.getStackTrace();
				    						for(int y= 0; y <st.length; y++){
				    							Log.w(TAG, st[y].toString());
				    						}
				    		    		}
				    	
									}
								}
							});
							
							SubMenuFdRulesActivity.this.fileTypeDialog.setOnCancelListener(new OnCancelListener(){

								@Override
								public void onCancel(DialogInterface dialog) {
									// TODO Auto-generated method stub
									SubMenuFdRulesActivity.this.chosenMagicNumber = null;
									choose.setText("Target File Type:");
									fileTypeDialog.dismiss();
									
								}
								
							});
							SubMenuFdRulesActivity.this.fileTypeDialog.show();
							
						}catch(Exception e){
							
					}
				}
			});
	 
			browseDir.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					// call FolderDialog
					
					/*
					FolderDialog fd = new FolderDialog(SubMenuFdRulesActivity.this,SubMenuFdRulesActivity.this);
					fd.setNewFolderEnabled(true);
					fd.chooseDirectory("");
					
					*/
					FileDialog f = new FileDialog(SubMenuFdRulesActivity.this, new File(""), false);
					f.addDirectoryListener(SubMenuFdRulesActivity.this);
					f.setSelectDirectoryOption(true);
					
					f.createFileDialog();
					SubMenuFdRulesActivity.this.chosenDir = null;
					designationDir.setText("Target Designation Directory:");
					
				}
			});
			
			positive.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					// CREATE RULE BUTTON
					// insert the new rule to the database, but check the fields first of course
					if(!SubMenuFdRulesActivity.this.onEditRule){
						if(SubMenuFdRulesActivity.this.chosenDir != null){
							// valid chosen directory
							if(SubMenuFdRulesActivity.this.chosenMagicNumber != null){
								// valid chosen Magic number
								
								if(ruleName.getText().toString() != null || ruleName.getText().toString().length() < 1){
									// rulename is not empty
									SQLiteHelper db = SubMenuFdRulesActivity.this.getDb(SubMenuFdRulesActivity.this);
									try{
										// public ModelFdSettings(Integer accountId, String ruleName, String designationPath, String fileType)
										if(db.insert(Tables.file_designation_settings,
													new ModelFdSettings(SF.getAccountId(), ruleName.getText().toString(),
															SubMenuFdRulesActivity.this.chosenDir, SubMenuFdRulesActivity.this.chosenMagicNumber) ,
											null)){
											Toast.makeText(SubMenuFdRulesActivity.this,
													"Successfully created the new rule!",
													Toast.LENGTH_LONG).show();
											Log.d(TAG, "Successfully created new rule: " + ruleName.getText().toString() + "\n" +
													"Chosen dir: " + SubMenuFdRulesActivity.this.chosenDir + "\n" +
													"Chosen file type: " +SubMenuFdRulesActivity.this.chosenMagicNumber );
										}else{
											Toast.makeText(SubMenuFdRulesActivity.this,
													"The rule you have specified is already existing!",
													Toast.LENGTH_LONG).show();
											Log.e(TAG, "Failed creating new rule: " + ruleName.getText().toString() + "\n" +
													"Chosen dir: " + SubMenuFdRulesActivity.this.chosenDir + "\n" +
													"Chosen file type: " +SubMenuFdRulesActivity.this.chosenMagicNumber );
										}
										dialog.dismiss();
										finish();
										startActivity(getIntent());
									}catch(Exception e){
										if(LOGCAT){
											StackTraceElement[] st = e.getStackTrace();
				    						for(int y= 0; y <st.length; y++){
				    							Log.w(TAG, st[y].toString());
				    						}
										}
									}
								}else{
								// rule name is blank!
								Toast.makeText(SubMenuFdRulesActivity.this, "Please input a Rule Name!",
										Toast.LENGTH_LONG).show();
							
								}
							}else{

							// chosen magic number is either null or invalid
							Toast.makeText(SubMenuFdRulesActivity.this, "Please select a file type!",
									Toast.LENGTH_LONG).show();
							}
						}else{
						// chosen directory is either null or invalid
							Toast.makeText(SubMenuFdRulesActivity.this, "The directory you have chosen is invalid!",
								Toast.LENGTH_LONG).show();
						}
						
					}else{
						// on Edit mode
						
						if(SubMenuFdRulesActivity.this.chosenDir != null){
							// valid chosen directory
							if(SubMenuFdRulesActivity.this.chosenMagicNumber != null){
								// valid chosen Magic number
								
								if(ruleName.getText().toString() != null || ruleName.getText().toString().length() < 1){
									// rulename is not empty
									SQLiteHelper db = SubMenuFdRulesActivity.this.getDb(SubMenuFdRulesActivity.this);
									try{
										// public ModelFdSettings(Integer accountId, String ruleName, String designationPath, String fileType)
										if(db.update(Tables.file_designation_settings,
												SubMenuFdRulesActivity.this.oldParams, 
												new ModelFdSettings(SF.getAccountId(), ruleName.getText().toString(),
														SubMenuFdRulesActivity.this.chosenDir, SubMenuFdRulesActivity.this.chosenMagicNumber),
												null)){
											Toast.makeText(SubMenuFdRulesActivity.this,
													"Successfully updated the rule!",
													Toast.LENGTH_LONG).show();
											Log.d(TAG, "Successfully updated rule: " + ruleName.getText().toString() + "\n" +
													"Chosen dir: " + SubMenuFdRulesActivity.this.chosenDir + "\n" +
													"Chosen file type: " +SubMenuFdRulesActivity.this.chosenMagicNumber );
										}else{
											Toast.makeText(SubMenuFdRulesActivity.this,
													"The rule you have specified is already existing!",
													Toast.LENGTH_LONG).show();
											Log.e(TAG, "Failed updating rule: " + ruleName.getText().toString() + "\n" +
													"Chosen dir: " + SubMenuFdRulesActivity.this.chosenDir + "\n" +
													"Chosen file type: " +SubMenuFdRulesActivity.this.chosenMagicNumber );
										}
										dialog.dismiss();
										SubMenuFdRulesActivity.this.holdDialog.dismiss();
										SubMenuFdRulesActivity.this.onEditRule = false;
										finish();
										startActivity(getIntent());
										
									}catch(Exception e){
										Toast.makeText(SubMenuFdRulesActivity.this,
												"The file type you have specified is already existing as a rule!",
												Toast.LENGTH_LONG).show();
										Log.e(TAG, "Failed updating rule (RULES CONFLICT): " + ruleName.getText().toString() + "\n" +
												"Chosen dir: " + SubMenuFdRulesActivity.this.chosenDir + "\n" +
												"Chosen file type: " +SubMenuFdRulesActivity.this.chosenMagicNumber );
										if(LOGCAT){
											StackTraceElement[] st = e.getStackTrace();
				    						for(int y= 0; y <st.length; y++){
				    							Log.w(TAG, st[y].toString());
				    						}
										}
									}
								}else{
								// rule name is blank!
								Toast.makeText(SubMenuFdRulesActivity.this, "Please input a Rule Name!",
										Toast.LENGTH_LONG).show();
							
								}
							}else{

							// chosen magic number is either null or invalid
							Toast.makeText(SubMenuFdRulesActivity.this, "Please select a file type!",
									Toast.LENGTH_LONG).show();
							}
						}else{
						// chosen directory is either null or invalid
							Toast.makeText(SubMenuFdRulesActivity.this, "The directory you have chosen is invalid!",
								Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			
			negative.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					// close this shit, fuck the user, no nevermind, the user is a retard for opening this shit and then decides to just close it.
					
					dialog.dismiss();
					
					
				}
			});
			
			dialog.setOnCancelListener(new OnCancelListener(){

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					SubMenuFdRulesActivity.this.onEditRule = false;
					
				}
				
			});
			dialog.show();
			
			break;
			
			case R.id.listViewRules:
				
				break;
			
			default:
				break;
		}
		
	}
	
	
/*
	@Override
	public void onChosenDir(String chosenDir) {
		// TODO Auto-generated method stub
		designationDir.setText("Target Designation Directory:"+"\n" + chosenDir);
		Toast.makeText(
            	SubMenuFdRulesActivity.this, "Chosen directory: " + 
              chosenDir, Toast.LENGTH_LONG).show();
		File f = new File(chosenDir);
		if(f.exists() && f.canRead() && f.canWrite()  && !(f.isHidden()) ){
			// check if the directory exists, can be read, is not hidden and can be accessed to write
			SubMenuFdRulesActivity.this.chosenDir = chosenDir;
		}else{
			SubMenuFdRulesActivity.this.chosenDir = null;
			designationDir.setText("Target Designation Directory:"+"\n" + "The chosen directory " + chosenDir +
					" cannot be used as a Designation Directory due to System Permissions");
			if(LOGCAT){
				Log.d(TAG, "Target Designation Directory:"+"\n" + "The chosen directory " + chosenDir +
						" cannot be used as a Designation Directory due to System Permissions");
				Log.d(TAG, chosenDir + " Properties:");
				Log.d(TAG, "Exists?: " + Boolean.toString(f.exists()));
				Log.d(TAG, "Can Read?: " + Boolean.toString(f.canRead()));
				Log.d(TAG, "Can Write?: " + Boolean.toString(f.canWrite()));
				Log.d(TAG, "Is Hidden?: (This should be false!)" + Boolean.toString(f.isHidden()));
				
			}
		}
	}
*/



	@Override
	public void directorySelected(File directory) {
		// TODO Auto-generated method stub
		designationDir.setText("Target Designation Directory:"+"\n" + directory.getAbsolutePath());
		Toast.makeText(
            	SubMenuFdRulesActivity.this, "Chosen directory: " + 
            			directory.getAbsolutePath(), Toast.LENGTH_LONG).show();
		File f = directory;
		if(f.exists() && f.canRead() && f.canWrite()  && !(f.isHidden()) ){
			// check if the directory exists, can be read, is not hidden and can be accessed to write
			SubMenuFdRulesActivity.this.chosenDir = directory.getAbsolutePath();
		}else{
			SubMenuFdRulesActivity.this.chosenDir = null;
			designationDir.setText("Target Designation Directory:"+"\n" + "The chosen directory " + directory.getAbsolutePath() +
					" cannot be used as a Designation Directory due to System Permissions");
			if(LOGCAT){
				Log.d(TAG, "Target Designation Directory:"+"\n" + "The chosen directory " + directory.getAbsolutePath() +
						" cannot be used as a Designation Directory due to System Permissions");
				Log.d(TAG, directory.getAbsolutePath() + " Properties:");
				Log.d(TAG, "Exists?: " + Boolean.toString(f.exists()));
				Log.d(TAG, "Can Read?: " + Boolean.toString(f.canRead()));
				Log.d(TAG, "Can Write?: " + Boolean.toString(f.canWrite()));
				Log.d(TAG, "Is Hidden?: (This should be false!)" + Boolean.toString(f.isHidden()));
				
			}
		}
	}


	
}
