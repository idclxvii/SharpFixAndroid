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

import tk.idclxvii.sharpfixandroid.databasemodel.*;

import java.io.*;
import java.util.*;

public class SubMenuFilterRulesActivity extends Activity implements
		OnClickListener, OnCheckedChangeListener,
		FolderDialog.ChosenDirectoryListener {

	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	
	boolean onEditRule = false;
	String onEditTitle = "";
	private ModelFdSettings oldParams;
	SQLiteHelper db;
	TextView title;
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
		setContentView(R.layout.filters_sub_menu_rules);
		title = (TextView) findViewById(R.id.title);
		RL = (RelativeLayout) findViewById(R.id.Rules);
		String filter = this.getIntent().getExtras().getString("filter");
		if(filter.equals("fdd")){
			title.setText("File Duplication Detection Filter Rules");
		}else if(filter.equals("fd")){
			title.setText("File Designation Filter Rules");
		}else{
			if(this.LOGCAT){
				Log.e(this.TAG, this.TAG +  "Undefined FILTER: " +filter);
			}
		}
		
		
		noRules = (TextView) findViewById(R.id.noRules);
		noRulesHr = (View) findViewById(R.id.noRulesHr);
		createRule = (TextView) findViewById(R.id.createRule);
		createRule.setOnClickListener(this);
		createRule.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// 2TODO Auto-generated method stub
				Toast.makeText(
		            	SubMenuFilterRulesActivity.this,"FUCK YOU! You dont hold this button down asshole!", Toast.LENGTH_LONG).show();
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
							final Object rule = SubMenuFilterRulesActivity.this.db.select(Tables.file_designation_settings, ModelFdSettings.class,
								new Object[][]{{"file_type", fileType}}, null);
							SubMenuFilterRulesActivity.this.oldParams = (ModelFdSettings) rule;
							SubMenuFilterRulesActivity.this.holdDialog = new Dialog(SubMenuFilterRulesActivity.this);
							SubMenuFilterRulesActivity.this.holdDialog.setContentView(R.layout.rules_dialog);
							SubMenuFilterRulesActivity.this.holdDialog.setTitle(ruleName);
							Button edit = (Button) SubMenuFilterRulesActivity.this.holdDialog.findViewById(R.id.editRule);
							Button delete = (Button) SubMenuFilterRulesActivity.this.holdDialog.findViewById(R.id.deleteRule);
							SubMenuFilterRulesActivity.this.holdDialog .setCancelable(true);
								
							edit.setOnClickListener(new OnClickListener(){
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									SubMenuFilterRulesActivity.this.onEditRule = true;
									SubMenuFilterRulesActivity.this.onEditTitle = fileType;
									SubMenuFilterRulesActivity.this.createRule.performClick();
									SubMenuFilterRulesActivity.this.dialog.setTitle(fileType);
									SubMenuFilterRulesActivity.this.ruleName.setText(((ModelFdSettings)rule).getRule_name());
									SubMenuFilterRulesActivity.this.choose.setText("Target File Type:\n"+((ModelFdSettings)rule).getFile_type());
									SubMenuFilterRulesActivity.this.designationDir.setText("Target Designation Directory:"+"\n" + ((ModelFdSettings)rule).getDesignation_path());
									SubMenuFilterRulesActivity.this.positive.setText("Update Rule");
									SubMenuFilterRulesActivity.this.chosenDir = ((ModelFdSettings)rule).getDesignation_path();
									SubMenuFilterRulesActivity.this.chosenMagicNumber = ((ModelFdSettings)rule).getFile_type();
									
									
								}
								
							});
							
							delete.setOnClickListener(new OnClickListener(){

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									final Dialog d = new Dialog(SubMenuFilterRulesActivity.this);
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
												if(SubMenuFilterRulesActivity.this.db.delete(Tables.file_designation_settings,
														SubMenuFilterRulesActivity.this.oldParams, null)){
													Log.d(TAG, "Successfully deleted the rule: ");
												}else{
													Log.e(TAG, "Failed deleting the rule: ");
												}
												d.dismiss();
												SubMenuFilterRulesActivity.this.holdDialog.dismiss();
												SubMenuFilterRulesActivity.this.onEditRule = false;
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
							SubMenuFilterRulesActivity.this.holdDialog.setOnCancelListener(new OnCancelListener(){

								@Override
							
								public void onCancel(DialogInterface dialog) {
									// TODO Auto-generated method stub
									SubMenuFilterRulesActivity.this.onEditRule = false;
								}
								
							});
							SubMenuFilterRulesActivity.this.holdDialog.show();
							
						
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
							final Object rule = SubMenuFilterRulesActivity.this.db.select(Tables.file_designation_settings, ModelFdSettings.class,
								new Object[][]{{"file_type", fileType}}, null);
							Intent i = new Intent(SubMenuFilterRulesActivity.this, ViewRuleActivity.class);
							
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
			  ModelPreferences oldParams = new ModelPreferences( ((SharpFixApplicationClass) getApplication()).getAccountId(),
						((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
						((SharpFixApplicationClass) getApplication()).getFddPref(),
						((SharpFixApplicationClass) getApplication()).getAutoLogin(),
						((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
			  ModelPreferences newParams = new ModelPreferences(((SharpFixApplicationClass) getApplication()).getAccountId(),
						((SharpFixApplicationClass) getApplication()).getFddSwitch(), ((SharpFixApplicationClass) getApplication()).getFdSwitch(),
						((SharpFixApplicationClass) getApplication()).getFddPref(),
						((SharpFixApplicationClass) getApplication()).getAutoLogin(),
						((SharpFixApplicationClass) getApplication()).getFddFilterSwitch(),
						((SharpFixApplicationClass) getApplication()).getFdFilterSwitch());
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
			
			
			fileTypeDialog = new Dialog(SubMenuFilterRulesActivity.this);
			fileTypeDialog.setContentView(R.layout.file_types_dialog);
			fileTypeDialog.setCancelable(true);
			fileTypeDialog.setTitle("Choose File Type");
			SubMenuFilterRulesActivity.this.chosenDir = null;
			SubMenuFilterRulesActivity.this.chosenMagicNumber = null;
			
			
			
			
			chooseFileType.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// query database here and outout another dialog which lists down the file types taken from the query
						try{
							//################################################################################
							// CURRENT PROBLEM
							//################################################################################
							
							Object[] bridge = SubMenuFilterRulesActivity.this.db.selectAll(Tables.magic_number, ModelMagicNumber.class, null);
							List<String> fileTypes = new ArrayList<String>();
							ArrayAdapter<String> adapter = new ArrayAdapter<String>(SubMenuFilterRulesActivity.this,
									android.R.layout.simple_list_item_1);
								
							for(int x =0; x < bridge.length; x++){
								fileTypes.add(((ModelMagicNumber)bridge[x]).getFile_type().toString());
								
							}
							SubMenuFilterRulesActivity.this.noDupes = new ArrayList<String>(new LinkedHashSet<String>(fileTypes));
							
							for(String str :SubMenuFilterRulesActivity.this.noDupes){
								adapter.add(str);
							}
							SubMenuFilterRulesActivity.this.lv = (ListView) fileTypeDialog.findViewById(R.id.lv);
							SubMenuFilterRulesActivity.this.lv.setAdapter(adapter);
							SubMenuFilterRulesActivity.this.lv.setOnItemClickListener(new OnItemClickListener(){

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									String str = (String)parent.getItemAtPosition(position);
									
									   Toast.makeText(SubMenuFilterRulesActivity.this, str,
											   Toast.LENGTH_SHORT).show();
									try{
										SubMenuFilterRulesActivity.this.chosenMagicNumber = null;
										/*
										Object[] bridge = SubMenuFilterRulesActivity.this.db.selectMulti(Tables.magic_number, ModelMagicNumber.class,
										new Object[][]{{"file_type",str}}, null);
										for(Object m : bridge){
											Log.i(TAG, "File Type: "+((ModelMagicNumber)m).getFile_type());
											Log.i(TAG, "4-bytes Signature: "+((ModelMagicNumber)m).getSignature_4_bytes());
											Log.i(TAG, "8-bytes Signature: "+((ModelMagicNumber)m).getSignature_8_bytes());
											Log.i(TAG, "MIME: "+((ModelMagicNumber)m).getMime());
											
										}
										*/
										SubMenuFilterRulesActivity.this.chosenMagicNumber = str;
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
							
							SubMenuFilterRulesActivity.this.fileTypeDialog.setOnCancelListener(new OnCancelListener(){

								@Override
								public void onCancel(DialogInterface dialog) {
									// TODO Auto-generated method stub
									SubMenuFilterRulesActivity.this.chosenMagicNumber = null;
									choose.setText("Target File Type:");
									fileTypeDialog.dismiss();
									
								}
								
							});
							SubMenuFilterRulesActivity.this.fileTypeDialog.show();
							
						}catch(Exception e){
							
					}
				}
			});
	 
			browseDir.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					// call FolderDialog
					FolderDialog fd = new FolderDialog(SubMenuFilterRulesActivity.this,SubMenuFilterRulesActivity.this);
					fd.setNewFolderEnabled(true);
					fd.chooseDirectory("");
					SubMenuFilterRulesActivity.this.chosenDir = null;
					designationDir.setText("Target Designation Directory:");
					
				}
			});
			
			positive.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					// CREATE RULE BUTTON
					// insert the new rule to the database, but check the fields first of course
					if(!SubMenuFilterRulesActivity.this.onEditRule){
						if(SubMenuFilterRulesActivity.this.chosenDir != null){
							// valid chosen directory
							if(SubMenuFilterRulesActivity.this.chosenMagicNumber != null){
								// valid chosen Magic number
								
								if(ruleName.getText().toString() != null || ruleName.getText().toString().length() < 1){
									// rulename is not empty
									SQLiteHelper db = SubMenuFilterRulesActivity.this.getDb(SubMenuFilterRulesActivity.this);
									try{
										// public ModelFdSettings(Integer accountId, String ruleName, String designationPath, String fileType)
										if(db.insert(Tables.file_designation_settings,
													new ModelFdSettings(SF.getAccountId(), ruleName.getText().toString(),
															SubMenuFilterRulesActivity.this.chosenDir, SubMenuFilterRulesActivity.this.chosenMagicNumber) ,
											null)){
											Toast.makeText(SubMenuFilterRulesActivity.this,
													"Successfully created the new rule!",
													Toast.LENGTH_LONG).show();
											Log.d(TAG, "Successfully created new rule: " + ruleName.getText().toString() + "\n" +
													"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenDir + "\n" +
													"Chosen file type: " +SubMenuFilterRulesActivity.this.chosenMagicNumber );
										}else{
											Toast.makeText(SubMenuFilterRulesActivity.this,
													"The rule you have specified is already existing!",
													Toast.LENGTH_LONG).show();
											Log.e(TAG, "Failed creating new rule: " + ruleName.getText().toString() + "\n" +
													"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenDir + "\n" +
													"Chosen file type: " +SubMenuFilterRulesActivity.this.chosenMagicNumber );
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
								Toast.makeText(SubMenuFilterRulesActivity.this, "Please input a Rule Name!",
										Toast.LENGTH_LONG).show();
							
								}
							}else{

							// chosen magic number is either null or invalid
							Toast.makeText(SubMenuFilterRulesActivity.this, "Please select a file type!",
									Toast.LENGTH_LONG).show();
							}
						}else{
						// chosen directory is either null or invalid
							Toast.makeText(SubMenuFilterRulesActivity.this, "The directory you have chosen is invalid!",
								Toast.LENGTH_LONG).show();
						}
						
					}else{
						// on Edit mode
						
						if(SubMenuFilterRulesActivity.this.chosenDir != null){
							// valid chosen directory
							if(SubMenuFilterRulesActivity.this.chosenMagicNumber != null){
								// valid chosen Magic number
								
								if(ruleName.getText().toString() != null || ruleName.getText().toString().length() < 1){
									// rulename is not empty
									SQLiteHelper db = SubMenuFilterRulesActivity.this.getDb(SubMenuFilterRulesActivity.this);
									try{
										// public ModelFdSettings(Integer accountId, String ruleName, String designationPath, String fileType)
										if(db.update(Tables.file_designation_settings,
												SubMenuFilterRulesActivity.this.oldParams, 
												new ModelFdSettings(SF.getAccountId(), ruleName.getText().toString(),
														SubMenuFilterRulesActivity.this.chosenDir, SubMenuFilterRulesActivity.this.chosenMagicNumber),
												null)){
											Toast.makeText(SubMenuFilterRulesActivity.this,
													"Successfully updated the rule!",
													Toast.LENGTH_LONG).show();
											Log.d(TAG, "Successfully updated rule: " + ruleName.getText().toString() + "\n" +
													"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenDir + "\n" +
													"Chosen file type: " +SubMenuFilterRulesActivity.this.chosenMagicNumber );
										}else{
											Toast.makeText(SubMenuFilterRulesActivity.this,
													"The rule you have specified is already existing!",
													Toast.LENGTH_LONG).show();
											Log.e(TAG, "Failed updating rule: " + ruleName.getText().toString() + "\n" +
													"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenDir + "\n" +
													"Chosen file type: " +SubMenuFilterRulesActivity.this.chosenMagicNumber );
										}
										dialog.dismiss();
										SubMenuFilterRulesActivity.this.holdDialog.dismiss();
										SubMenuFilterRulesActivity.this.onEditRule = false;
										finish();
										startActivity(getIntent());
										
									}catch(Exception e){
										Toast.makeText(SubMenuFilterRulesActivity.this,
												"The file type you have specified is already existing as a rule!",
												Toast.LENGTH_LONG).show();
										Log.e(TAG, "Failed updating rule (RULES CONFLICT): " + ruleName.getText().toString() + "\n" +
												"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenDir + "\n" +
												"Chosen file type: " +SubMenuFilterRulesActivity.this.chosenMagicNumber );
										if(LOGCAT){
											StackTraceElement[] st = e.getStackTrace();
				    						for(int y= 0; y <st.length; y++){
				    							Log.w(TAG, st[y].toString());
				    						}
										}
									}
								}else{
								// rule name is blank!
								Toast.makeText(SubMenuFilterRulesActivity.this, "Please input a Rule Name!",
										Toast.LENGTH_LONG).show();
							
								}
							}else{

							// chosen magic number is either null or invalid
							Toast.makeText(SubMenuFilterRulesActivity.this, "Please select a file type!",
									Toast.LENGTH_LONG).show();
							}
						}else{
						// chosen directory is either null or invalid
							Toast.makeText(SubMenuFilterRulesActivity.this, "The directory you have chosen is invalid!",
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
					SubMenuFilterRulesActivity.this.onEditRule = false;
					
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
	
	

	@Override
	public void onChosenDir(String chosenDir) {
		// TODO Auto-generated method stub
		designationDir.setText("Target Designation Directory:"+"\n" + chosenDir);
		Toast.makeText(
            	SubMenuFilterRulesActivity.this, "Chosen directory: " + 
              chosenDir, Toast.LENGTH_LONG).show();
		File f = new File(chosenDir);
		if(f.exists() && f.canRead() && f.canWrite()  && !(f.isHidden()) ){
			// check if the directory exists, can be read, is not hidden and can be accessed to write
			SubMenuFilterRulesActivity.this.chosenDir = chosenDir;
		}else{
			SubMenuFilterRulesActivity.this.chosenDir = null;
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


	
}
