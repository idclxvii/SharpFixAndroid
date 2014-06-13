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
		FolderDialog.ChosenDirectoryListener, FileDialog.FileSelectedListener {

	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private String TAG;
	private boolean LOGCAT;
	
	private enum filterFlag { fd, fdd};
	
	
	private filterFlag instance;
	
	boolean onEditRule = false;
	String onEditTitle = "";
	private Object oldParams;
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
	TextView chosenFilter;
	Button browseDir;
	Button positive; 
	Button negative; 
	

	
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
			this.instance = filterFlag.fdd;
			title.setText("File Duplication Detection Filter Rules");
		}else if(filter.equals("fd")){
			this.instance = filterFlag.fd;
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
		            	SubMenuFilterRulesActivity.this,"Developer Mode currently disabled!", Toast.LENGTH_LONG).show();
				return false;
			}
			
		});
		hr2 = (View) findViewById(R.id.hr2);
		this.RULES = (ListView) findViewById(R.id.listViewRules);
		
		try{
			
			Object [] r = this.db.selectMulti(Tables.dir_filter, ModelDirFilter.class, 
					(this.instance.equals(filterFlag.fd) ? 
							new Object[][]{{"filter", "fd"}} 
							: new Object[][]{{"filter", "fdd"}})
					,null);
			Object [] q = this.db.selectMulti(Tables.file_filter, ModelFileFilter.class, 
					(this.instance.equals(filterFlag.fd) ? 
							new Object[][]{{"filter", "fd"}} 
							: new Object[][]{{"filter", "fdd"}})
					,null);
			if(r.length > 0 || q.length > 0 ){
			
				// rule(s) found
				
				noRules.setVisibility(View.GONE);
				noRulesHr.setVisibility(View.GONE);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						R.layout.custom_textview);
				
				// dir_filters
				for(Object f : r){
					adapter.add(((ModelDirFilter)f).getRule() + "\n" + "Directory Filter Rule" + "\n" +
							  ((ModelDirFilter)f).getDir());
					
				}
				
				// file_filters
				for(Object f : q){
					adapter.add(((ModelFileFilter)f).getRule() + "\n" + "File Filter Rule" + "\n" +
				 ((ModelFileFilter)f).getFile());
					
				}
				
				
				this.RULES.setAdapter(adapter);
				RULES.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						
						try{
							final String selection = ((String)parent.getItemAtPosition(position));
							final String ruleName = selection.substring(0, selection.indexOf("\n"));
							final String filterType = selection.substring(selection.indexOf("\n")+1).substring(0,
									(selection.substring(selection.indexOf("\n")+1).indexOf("\n")));
							final Object rule = (filterType.equals("Directory Filter Rule") ?
										(SubMenuFilterRulesActivity.this.db.select(Tables.dir_filter, ModelDirFilter.class,
												new Object[][]{{"filter", SubMenuFilterRulesActivity.this.instance.toString()},
											{"rule", ruleName}}, null))
									:
										(SubMenuFilterRulesActivity.this.db.select(Tables.file_filter, ModelFileFilter.class,
												new Object[][]{{"filter", SubMenuFilterRulesActivity.this.instance.toString()},
													{"rule", ruleName}}, null))
									
									
									);/* = SubMenuFilterRulesActivity.this.db.select(Tables, ModelFdSettings.class,
								new Object[][]{{"file_type", fileType}}, null);
							*/
							Toast.makeText(SubMenuFilterRulesActivity.this, "Filter type: '" + filterType +"'", Toast.LENGTH_LONG).show();
							
							SubMenuFilterRulesActivity.this.oldParams = rule;
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
									SubMenuFilterRulesActivity.this.onEditTitle = filterType;
									SubMenuFilterRulesActivity.this.createRule.performClick();
									SubMenuFilterRulesActivity.this.dialog.setTitle(filterType);
									SubMenuFilterRulesActivity.this.positive.setText("Update Rule");
									SubMenuFilterRulesActivity.this.ruleName.setText(ruleName);
									SubMenuFilterRulesActivity.this.chosenFilter.setText("Item(s) to be filtered: "+"\n" + 
									(filterType.equals("Directory Filter Rule") ? ((ModelDirFilter)rule).getDir() :
										 ((ModelFileFilter)rule).getFile()
											));
									SubMenuFilterRulesActivity.this.dirFilter = (filterType.equals("Directory Filter Rule") ? true : false);						/*
									SubMenuFilterRulesActivity.this.ruleName.setText(((ModelFdSettings)rule).getRule_name());
									SubMenuFilterRulesActivity.this.chosenFilter.setText("Item(s) to be filtered: "+"\n" + ((ModelFdSettings)rule).getDesignation_path());
									*/
									
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
												if(filterType.equals("Directory Filter Rule")){
													if(SubMenuFilterRulesActivity.this.db.delete(Tables.dir_filter,
															(ModelDirFilter) rule, null)){
														Log.d(TAG, "Successfully deleted the rule: ");
													}else{
														Log.e(TAG, "Failed deleting the rule: ");
													}
												}else{
													if(SubMenuFilterRulesActivity.this.db.delete(Tables.file_filter,
															(ModelFileFilter) rule, null)){
														Log.d(TAG, "Successfully deleted the rule: ");
													}else{
														Log.e(TAG, "Failed deleting the rule: ");
													}
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
							final Object rule = SubMenuFilterRulesActivity.this.db.select(Tables.file_filter, ModelFdSettings.class,
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
	
	
	String chosenFiltered = "";
	boolean dirFilter = true; // true if the user selected directory filtering, false otherwise

	@Override
	public void onClick(View src) {
		
		switch (src.getId()){
			case R.id.createRule:
				
				if(!this.onEditRule){
				//#####################################################################################################
					final Dialog d = new Dialog(SubMenuFilterRulesActivity.this);
					d.setContentView(R.layout.rules_dialog);
					final Button yes = (Button) d.findViewById(R.id.editRule);
					yes.setText("File Filter: Filters the selected file.");
					final Button no = (Button) d.findViewById(R.id.deleteRule);
					no.setText("Directory Filter: Filters all the files inside the selected directory including its subdirectories,");
					d.setTitle("Select Filter Type");
				
					no.setOnClickListener(new OnClickListener(){
	
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							SubMenuFilterRulesActivity.this.dirFilter = true;
							d.dismiss();
							SubMenuFilterRulesActivity.this.performTask();
						}	
						
					});
					
					yes.setOnClickListener(new OnClickListener(){
	
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							SubMenuFilterRulesActivity.this.dirFilter = false;
							d.dismiss();
							SubMenuFilterRulesActivity.this.performTask();
							
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
	
				}else{
					// Toast.makeText(this, "ON EDIT RULE SHIT", Toast.LENGTH_LONG).show();
					SubMenuFilterRulesActivity.this.performTask();
				}
			
			break;
			
			case R.id.listViewRules:
				
				break;
			
			default:
				break;
		}
		
	}
	
	private void performTask(){
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.filter_create_rule);
		if(!this.onEditRule){
			dialog.setTitle("Create New "+(this.instance.equals(filterFlag.fd) ? "File Designation Filter" 
					: "File Duplication Detection Filter")+" Rule");
		}
		ruleName = (EditText) dialog.findViewById(R.id.ruleName);
		chosenFilter = (TextView) dialog.findViewById(R.id.chosenFilter);
		browseDir = (Button) dialog.findViewById(R.id.chooser);
		positive = (Button) dialog.findViewById(R.id.positiveButton);
		negative = (Button) dialog.findViewById(R.id.negativeButton);	
		
		SubMenuFilterRulesActivity.this.chosenFiltered = null;
		
 
		browseDir.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				// call FolderDialog
				SubMenuFilterRulesActivity.this.chosenFiltered = null;
				if(SubMenuFilterRulesActivity.this.dirFilter){
					// user selected directory filtering 
					FolderDialog fd = new FolderDialog(SubMenuFilterRulesActivity.this,SubMenuFilterRulesActivity.this);
					fd.setNewFolderEnabled(true);
					fd.chooseDirectory("");
					
				}else{
					// user selected file filtering					
					FileDialog f = new FileDialog(SubMenuFilterRulesActivity.this,Environment.getExternalStorageDirectory());
					f.addFileListener(SubMenuFilterRulesActivity.this);
					f.setSelectDirectoryOption(false);
					f.createFileDialog();
					// Anonymous inner class
					/*
					f.addFileListener(new FileDialog.FileSelectedListener() {
						
						@Override
						public void fileSelected(File file) {
							// TODO Auto-generated method stub
							
						}
					});
					*/
					
				}
				
				chosenFilter.setText("Item(s) to be filtered: ");
				
			}
		});
		
		positive.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				// CREATE RULE BUTTON
				// insert the new rule to the database, but check the fields first of course
				if(!SubMenuFilterRulesActivity.this.onEditRule){
					
					
					if(SubMenuFilterRulesActivity.this.chosenFilter != null){
						// valid chosen directory
						if(ruleName.getText().toString() != null || ruleName.getText().toString().length() < 1){
							// rulename is not empty
							SQLiteHelper db = SubMenuFilterRulesActivity.this.getDb(SubMenuFilterRulesActivity.this);
							try{
								// public ModelFdSettings(Integer accountId, String ruleName, String designationPath, String fileType)
								
								if(db.insert((SubMenuFilterRulesActivity.this.dirFilter) ? Tables.dir_filter : Tables.file_filter,
										
										( (SubMenuFilterRulesActivity.this.dirFilter) ? 
												(SubMenuFilterRulesActivity.this.instance.equals(SubMenuFilterRulesActivity.filterFlag.fd)) ?
														new ModelDirFilter(SF.getAccountId(), SubMenuFilterRulesActivity.this.ruleName.getText().toString(),
																	SubMenuFilterRulesActivity.this.chosenFiltered, "fd")
													: 	
														new ModelDirFilter(SF.getAccountId(), SubMenuFilterRulesActivity.this.ruleName.getText().toString(),
																	SubMenuFilterRulesActivity.this.chosenFiltered, "fdd") 
										:
											
											
												(SubMenuFilterRulesActivity.this.instance.equals(SubMenuFilterRulesActivity.filterFlag.fd)) ?
														new ModelFileFilter(SF.getAccountId(), SubMenuFilterRulesActivity.this.ruleName.getText().toString(),
															SubMenuFilterRulesActivity.this.chosenFiltered, "fd")
													: 	
														new ModelFileFilter(SF.getAccountId(), SubMenuFilterRulesActivity.this.ruleName.getText().toString(),
													SubMenuFilterRulesActivity.this.chosenFiltered, "fdd")
										
												
												),
										
										
										
								/*
								 * 	new ModelFdSettings(SF.getAccountId(), ruleName.getText().toString(),
								 *		SubMenuFilterRulesActivity.this.chosenFilter, SubMenuFilterRulesActivity.this.chosenMagicNumber)
								 */
									null)){
									Toast.makeText(SubMenuFilterRulesActivity.this,
											"Successfully created the new rule!",
											Toast.LENGTH_LONG).show();
									Log.d(TAG, "Successfully created new rule: " + ruleName.getText().toString() + "\n" +
											"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenFiltered + "\n" +
											"Filter Flag: " +SubMenuFilterRulesActivity.this.instance.name() );
								}else{
									Toast.makeText(SubMenuFilterRulesActivity.this,
											"The rule you have specified is already existing!",
											Toast.LENGTH_LONG).show();
									Log.e(TAG, "Failed creating new rule: " + ruleName.getText().toString() + "\n" +
											"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenFiltered + "\n" +
											"Filter Flag: " +SubMenuFilterRulesActivity.this.instance.name() );
								}
								SubMenuFilterRulesActivity.this.holdDialog.dismiss();
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
					// chosen directory is either null or invalid
						Toast.makeText(SubMenuFilterRulesActivity.this, "The directory you have chosen is invalid!",
							Toast.LENGTH_LONG).show();
					}
					
				}else{
					// on Edit mode
					

					if(SubMenuFilterRulesActivity.this.chosenFilter != null){
						// valid chosen directory
						if(ruleName.getText().toString() != null || ruleName.getText().toString().length() < 1){
							// rulename is not empty
							SQLiteHelper db = SubMenuFilterRulesActivity.this.getDb(SubMenuFilterRulesActivity.this);
							try{
								// public ModelFdSettings(Integer accountId, String ruleName, String designationPath, String fileType)
								
								// db.update(table, oldParams, newParams, db)
								// db.insert(table, params, db)
								
								if(db.update(
										
										(SubMenuFilterRulesActivity.this.dirFilter) ? Tables.dir_filter : Tables.file_filter,
										
										// old params
										(SubMenuFilterRulesActivity.this.dirFilter) ?
												(ModelDirFilter)SubMenuFilterRulesActivity.this.oldParams :
												(ModelFileFilter)SubMenuFilterRulesActivity.this.oldParams ,
												
												((SubMenuFilterRulesActivity.this.dirFilter) ? 
														(SubMenuFilterRulesActivity.this.instance.equals(SubMenuFilterRulesActivity.filterFlag.fd)) ?
																new ModelDirFilter(SF.getAccountId(), SubMenuFilterRulesActivity.this.ruleName.getText().toString(),
																			SubMenuFilterRulesActivity.this.chosenFiltered, "fd")
															: 	
																new ModelDirFilter(SF.getAccountId(), SubMenuFilterRulesActivity.this.ruleName.getText().toString(),
																			SubMenuFilterRulesActivity.this.chosenFiltered, "fdd") 
												:
													
													
														(SubMenuFilterRulesActivity.this.instance.equals(SubMenuFilterRulesActivity.filterFlag.fd)) ?
																new ModelFileFilter(SF.getAccountId(), SubMenuFilterRulesActivity.this.ruleName.getText().toString(),
																	SubMenuFilterRulesActivity.this.chosenFiltered, "fd")
															: 	
																new ModelFileFilter(SF.getAccountId(), SubMenuFilterRulesActivity.this.ruleName.getText().toString(),
															SubMenuFilterRulesActivity.this.chosenFiltered, "fdd")
												
														
										),
								/*
								 * 	new ModelFdSettings(SF.getAccountId(), ruleName.getText().toString(),
								 *		SubMenuFilterRulesActivity.this.chosenFilter, SubMenuFilterRulesActivity.this.chosenMagicNumber)
								 */
									null)
									
										
										){
									Toast.makeText(SubMenuFilterRulesActivity.this,
											"Successfully created the new rule!",
											Toast.LENGTH_LONG).show();
									Log.d(TAG, "Successfully created new rule: " + ruleName.getText().toString() + "\n" +
											"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenFiltered + "\n" +
											"Filter Flag: " +SubMenuFilterRulesActivity.this.instance.name() );
								}else{
									Toast.makeText(SubMenuFilterRulesActivity.this,
											"The rule you have specified is already existing!",
											Toast.LENGTH_LONG).show();
									Log.e(TAG, "Failed creating new rule: " + ruleName.getText().toString() + "\n" +
											"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenFiltered + "\n" +
											"Filter Flag: " +SubMenuFilterRulesActivity.this.instance.name() );
								}
								SubMenuFilterRulesActivity.this.holdDialog.dismiss();
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
					// chosen directory is either null or invalid
						Toast.makeText(SubMenuFilterRulesActivity.this, "The directory you have chosen is invalid!",
							Toast.LENGTH_LONG).show();
					}
					
					/*
					if(SubMenuFilterRulesActivity.this.chosenFilter != null){
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
													SubMenuFilterRulesActivity.this.chosenFilter, SubMenuFilterRulesActivity.this.chosenMagicNumber),
											null)){
										Toast.makeText(SubMenuFilterRulesActivity.this,
												"Successfully updated the rule!",
												Toast.LENGTH_LONG).show();
										Log.d(TAG, "Successfully updated rule: " + ruleName.getText().toString() + "\n" +
												"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenFilter + "\n" +
												"Chosen file type: " +SubMenuFilterRulesActivity.this.chosenMagicNumber );
									}else{
										Toast.makeText(SubMenuFilterRulesActivity.this,
												"The rule you have specified is already existing!",
												Toast.LENGTH_LONG).show();
										Log.e(TAG, "Failed updating rule: " + ruleName.getText().toString() + "\n" +
												"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenFilter + "\n" +
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
											"Chosen dir: " + SubMenuFilterRulesActivity.this.chosenFilter + "\n" +
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
					*/
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
		
	}

	@Override
	public void onChosenDir(String chosenDir) {
		// TODO Auto-generated method stub
		try{
			chosenFilter.setText("Item(s) to be filtered: "+"\n" + chosenDir);
			Toast.makeText(
	            	SubMenuFilterRulesActivity.this, "Chosen directory: " + 
	              chosenDir, Toast.LENGTH_LONG).show();
			File f = new File(chosenDir);
			if(f.exists() && f.canRead() && f.canWrite()  && !(f.isHidden()) ){
				// check if the directory exists, can be read, is not hidden and can be accessed to write
				SubMenuFilterRulesActivity.this.chosenFiltered = chosenDir;
			}else{
				SubMenuFilterRulesActivity.this.chosenFilter = null;
				chosenFilter.setText("Target Designation Directory:"+"\n" + "The chosen directory " + chosenDir +
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
		}catch(Exception e){
			chosenFilter.setText("Item(s) to be filtered: "+"\n" + "The chosen directory " + chosenDir +
					" cannot be used as a Designation Directory due to System Permissions");
			if(LOGCAT){
				Log.d(TAG, "Item(s) to be filtered: "+"\n" + "The chosen directory " + chosenDir +
						" cannot be used as a Designation Directory due to System Permissions");
				
			}
		}
	}


	@Override
	public void fileSelected(File f) {
		
		// TODO Auto-generated method stub
		chosenFilter.setText("Item(s) to be filtered: "+"\n" + f.getAbsolutePath());
		Toast.makeText(
            	SubMenuFilterRulesActivity.this, "Chosen directory: " + 
            			f.getAbsolutePath(), Toast.LENGTH_LONG).show();
		
		Log.d(TAG, "Target Designation Directory:"+"\n" + "The chosen directory " + f.getAbsolutePath() +
				" cannot be used as a Designation Directory due to System Permissions");
		Log.d(TAG, f.getAbsolutePath() + " Properties:");
		Log.d(TAG, "Exists?: " + Boolean.toString(f.exists()));
		Log.d(TAG, "Can Read?: " + Boolean.toString(f.canRead()));
		Log.d(TAG, "Can Write?: " + Boolean.toString(f.canWrite()));
		Log.d(TAG, "Is Hidden?: (This should be false!)" + Boolean.toString(f.isHidden()));
		
		
		if(f.exists() && f.canRead() && f.canWrite()  && !(f.isHidden()) ){
			// check if the directory exists, can be read, is not hidden and can be accessed to write
			SubMenuFilterRulesActivity.this.chosenFiltered = f.getAbsolutePath();
		}else{
			SubMenuFilterRulesActivity.this.chosenFilter = null;
			chosenFilter.setText("Target Designation Directory:"+"\n" + "The chosen directory " + f.getAbsolutePath() +
					" cannot be used as a Designation Directory due to System Permissions");
			if(LOGCAT){
				Log.d(TAG, "Target Designation Directory:"+"\n" + "The chosen directory " + f.getAbsolutePath() +
						" cannot be used as a Designation Directory due to System Permissions");
				Log.d(TAG, f.getAbsolutePath() + " Properties:");
				Log.d(TAG, "Exists?: " + Boolean.toString(f.exists()));
				Log.d(TAG, "Can Read?: " + Boolean.toString(f.canRead()));
				Log.d(TAG, "Can Write?: " + Boolean.toString(f.canWrite()));
				Log.d(TAG, "Is Hidden?: (This should be false!)" + Boolean.toString(f.isHidden()));
				
			}
		}
	}


	


	
}
