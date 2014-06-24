package tk.idclxvii.sharpfixandroid;

import java.io.*;
import java.util.*;

import tk.idclxvii.sharpfixandroid.ListenerList.FireHandler;
import tk.idclxvii.sharpfixandroid.databasemodel.ModelMagicNumber;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import tk.idclxvii.sharpfixandroid.utils.*;

import android.app.*;
import android.content.*;
import android.content.DialogInterface.*;
import android.os.*;
import android.util.*;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileDialog {
	
	// LogCat switch and tag
	private SharpFixApplicationClass SF;
	private boolean LOGCAT = true;
	private final String TAG = getClass().getName();
    	
	private SQLiteHelper db;
	private static final String PARENT_DIR = "..";
    
    private HashMap<String, String> fileListWithImage = new HashMap<String,String>();
    private File topMostPath = null;
    private boolean developer = false;
    private String[] fileList;
    private File currentPath;
    
    public interface FileSelectedListener {
        void fileSelected(File file);
    }
    public interface DirectorySelectedListener {
        void directorySelected(File directory);
    }
    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<FileDialog.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList = new ListenerList<FileDialog.DirectorySelectedListener>();
    private final Activity activity;
    private boolean selectDirectoryOption;
    private String fileEndsWith;    

    
    private synchronized SQLiteHelper getDb(Context context){
		db = new SQLiteHelper(context);
		
		return this.db;
	}
    /**
     * @param activity 
     * @param initialPath
     */
    public FileDialog(Activity activity, File path, boolean dev) {
        this.activity = activity;
        this.db = this.getDb(this.activity);
        this.SF = ((SharpFixApplicationClass) activity.getApplication());
        this.developer = dev;
        	// there are more than 1 mounted storage shit
        HashMap <String, File> hm = this.SF.getMountedVolumeDirs();
        if(hm.size() > 1){
        	boolean flag = true;
        	File previous = null;
        	for(String str : hm.keySet().toArray(new String[hm.size()])){
        		try{
	            	if(!hm.get(str).getParentFile().exists() && !previous.getParentFile().equals(hm.get(str).getParentFile())){
	            		flag = false;
	            	}
	            	previous = hm.get(str).getParentFile();
            	}catch(Exception e){
            		// Null pointer exception on previous
            		previous = hm.get(str).getParentFile();
            		
            	}
            }
        	topMostPath = (flag && (this.SF.getDevMode() && this.developer)) ? Environment.getRootDirectory().getParentFile() : previous;
        	currentPath = topMostPath;
        }else{
        	topMostPath = ( hm.get("primary").exists() && (this.SF.getDevMode() && this.developer)) ? 
        			Environment.getRootDirectory().getParentFile() 
        			: hm.get("primary");
        	currentPath = topMostPath;
        }
        
       
        if (!path.exists()) path = topMostPath;//Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    /**
     * @return file dialog
     */
    public Dialog createFileDialog() {
    	loadFileList(currentPath);
    	Dialog dialog = null;
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        
        builder.setTitle(currentPath.getPath());
        if (selectDirectoryOption) {
            builder.setPositiveButton("Select directory", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, currentPath.getPath());
                    fireDirectorySelectedEvent(currentPath);
                }
            });
        }

        /*
        
        builder.setItems(fileList, new DialogInterface.OnClickListener() {
        	
            public void onClick(DialogInterface dialog, int which) {
                String fileChosen = fileList[which];
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                } else fireFileSelectedEvent(chosenFile);
            }
        });
        
        */
                
        
        
        //################################################################################################
       
        
        
        
        builder.setNeutralButton("Cancel", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        
        builder.setAdapter(
        		new AndroidLayoutUtils.CustomListView.ItemAdapter(activity,R.layout.custom_listview_row, fileList),
        		new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String fileChosen = fileList[which];
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                } else fireFileSelectedEvent(chosenFile);
			}
		});
        
        
        //################################################################################################
        
        
       // dialog = builder.show();
       dialog = builder.create();
       //final AlertDialog d = (AlertDialog) dialog;
       
       dialog.setCancelable(false);
       
       dialog.setOnKeyListener(new OnKeyListener(){
    	   @Override
    	     public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
    	         if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
    	        	 try{
	    	        	 File chosenFile = getChosenFile(PARENT_DIR);
	    	             loadFileList(chosenFile);
	    	             dialog.cancel();
	    	             dialog.dismiss();
	    	             showDialog();
	    	             return true;
    	        	 }catch(Exception e){
    	             	// possible error from here is the current directory is the top most directory on the file system
    	             	Logcat.logCaughtException(activity, e.getStackTrace());
    	             	return false;
    	             }
    	        	 
    	            
    	         }else{
    	             return false;
    	         }
    	     }
       });
       
       dialog.setOnShowListener(new OnShowListener(){
    	   
		@Override
		public void onShow(DialogInterface dialog) {
			// TODO Auto-generated method stub
			ListView lv = ((AlertDialog) dialog).getListView();
			lv.setOnItemLongClickListener(new OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					String selection = ((String)parent.getItemAtPosition(position));
					selection =  selection.replace("{Dir} ", "");
					selection =  selection.replace("{File} ", "");
					
					
					//################################################################################################
					//File f = (selection.equals(PARENT_DIR)) ? new File(currentPath.getParent()) :new File(currentPath,selection);
					File f = new File(currentPath,selection);
					
					Log.d(TAG,"Selected Item's Properties:");
					Log.d(TAG, "FILE: " + f.toString());
					Log.d(TAG, "Current Path: " +currentPath);
					Log.d(TAG, "Name:" + selection);
					Log.d(TAG, "Type: " + (f.isDirectory() ? "Folder" : "File"));
					FolderProperties fp = null;
					if(f.isDirectory()) fp = new FolderProperties(f);
					Log.d(TAG, (f.isDirectory() ?
							"Size: " + FileProperties.formatFileSize(fp.getFolderSize()) + "\nTotal Files: " + fp.getTotalFile() + "\nTotal Folders: " + fp.getTotalFolder() :
							"Size: " + (FileProperties.formatFileSize(f.length()) )
								) );
					Log.d(TAG, "Last Modified: " + FileProperties.formatFileLastMod(f.lastModified()));
					Log.d(TAG, "Exists?: " + Boolean.toString(f.exists()));
					Log.d(TAG, "Can Read?: " + Boolean.toString(f.canRead()));
					Log.d(TAG, "Can Write?: " + Boolean.toString(f.canWrite()));
					Log.d(TAG, "Is Hidden?: (This should be false!)" + Boolean.toString(f.isHidden()));
					
					Dialog dx = new Dialog(activity);
					dx.setContentView(R.layout.file_dialog_properties_dialog);
					dx.setTitle("Selected "+ (f.isDirectory() ? "Folder" : "File") + " Properties: ");
					
					TextView absPath = (TextView) dx.findViewById(R.id.absPath);
					absPath.setText("Absolute Path:");
					TextView absPathVal = (TextView) dx.findViewById(R.id.absPathValue);
					absPathVal.setText(f.toString());
					
					TextView parentPath = (TextView) dx.findViewById(R.id.parentPath);
					parentPath.setText("Parent Directory Path:");
					TextView parentPathVal = (TextView) dx.findViewById(R.id.parentPathValue);
					parentPathVal.setText(currentPath.toString());
					
					TextView fileName = (TextView) dx.findViewById(R.id.fileName);
					fileName.setText((f.isDirectory()? "Folder Name:" : "File Name:"));
					TextView fileNameVal = (TextView) dx.findViewById(R.id.fileNameValue);
					fileNameVal.setText(selection);
					
					TextView fileType = (TextView) dx.findViewById(R.id.fileType);
					fileType.setText("File Type:");
					TextView fileTypeVal = (TextView) dx.findViewById(R.id.fileTypeValue);
					String ft = null;
					
					//########################################################################################
					try{
						String temp = null;
						temp = ((ModelMagicNumber) (
								FileDialog.this.db.select(
										Tables.magic_number, ModelMagicNumber.class, 
										new Object[][]{ {"signature_8_bytes", FileProperties.getMagicNumber(f, 8)} }, null))).getFile_type();
						if(temp != null){
							ft = temp;
						}else{
							temp = ((ModelMagicNumber) (
									FileDialog.this.db.select(
											Tables.magic_number, ModelMagicNumber.class, 
											new Object[][]{ {"signature_4_bytes", FileProperties.getMagicNumber(f, 4)} }, null))).getFile_type();
							if(temp != null){
								ft = temp;
							}else{	
								if(LOGCAT){
									ft = "Unknown ";
									Log.d(TAG,"Unknown File Type detected: " + f.toString());
									try{
										Log.d(TAG,"8 bytes signature: " + FileProperties.getMagicNumber(f, 8));
										Log.d(TAG,"4 bytes signature: " + FileProperties.getMagicNumber(f, 4));
									}catch(Exception eee){
										if(LOGCAT){
											Log.e(TAG,"An error in getting a file's n-bytes signature has been encountered!");
											
										}
									}
								}
							}
						}
						fileTypeVal.setText((f.isDirectory()? "Folder" : (ft.contains("File") ? ft : ft + " File") ));
							
					}catch(Exception e){
						if(LOGCAT){
							fileTypeVal.setText("Unknown File");
							Log.d(TAG,"Unknown File Type detected: " + f.toString());
							try{
								Log.d(TAG,"4 bytes signature: " + FileProperties.getMagicNumber(f, 4));
								Log.d(TAG,"4 bytes signature: " + FileProperties.getMagicNumber(f, 8));
							}catch(Exception eee){
								if(LOGCAT){
									Log.e(TAG,"An error occured in getting the file's n-bytes signature!");
									Log.e(TAG,"Checking root cause of the error occurence . . .");
									Log.e(TAG, (f.isDirectory() ? "The file " +f.toString() + " is a directory! Directories do not have Magic Number Signatures." :
										"Please recheck the file \"" + f.toString() + "\" as it does not seem to be a directory to encounter this error!"));
									if(f.isDirectory()){
										fileTypeVal.setText("Folder");
									}else{
										fileTypeVal.setText("Unknown File");
									}
									
								}
							}
						}
					}
					
					
					
					
					//########################################################################################
					
					TextView fileSize = (TextView) dx.findViewById(R.id.fileSize);
					fileSize.setText((f.isDirectory()? "Folder Size:" : "File Size:"));
					TextView fileSizeVal = (TextView) dx.findViewById(R.id.fileSizeValue);
					fileSizeVal.setText((f.isDirectory() ?
							"Size: " + FileProperties.formatFileSize(fp.getFolderSize()) :
							"Size: " + (FileProperties.formatFileSize(f.length()) )
								));
					
					TextView totalContents = (TextView) dx.findViewById(R.id.totalContents);
					totalContents.setText("Total Contents:");
					TextView totalContentsVal = (TextView) dx.findViewById(R.id.totalContentsValue);
					totalContentsVal.setText((f.isDirectory() ?
							fp.getTotalFolder() + " Folders and " +fp.getTotalFile() + " Files" :
							"N/A" 
								));
					
					
					TextView lastMod = (TextView) dx.findViewById(R.id.lastMod);
					lastMod.setText("Last Modified:");
					TextView lastModVal = (TextView) dx.findViewById(R.id.lastModValue);
					lastModVal.setText(FileProperties.formatFileLastMod(f.lastModified()));
					
					dx.show();
			        return true;
				}
				
			});
			
		}
    	   
       });
        
       
       
       dialog.show();
       return dialog;
    }

    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    public void removeFileListener(FileSelectedListener listener) {
        fileListenerList.remove(listener);
    }

    public void setSelectDirectoryOption(boolean selectDirectoryOption) {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    public void addDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.add(listener);
    }

    public void removeDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.remove(listener);
    }

    /**
     * Show file dialog
     */
    public void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new FireHandler<FileDialog.FileSelectedListener>() {
            public void fireEvent(FileSelectedListener listener) {
            	String s = file.toString();
            	s = s.replace("{Dir} ", "");
            	s = s.replace("{File} ", "");
            	final File f = new File(s);
                listener.fileSelected(f);
            }
        });
    }

    private void fireDirectorySelectedEvent(final File directory) {
        dirListenerList.fireEvent(new FireHandler<FileDialog.DirectorySelectedListener>() {
            public void fireEvent(DirectorySelectedListener listener) {
            	String s = directory.toString();
            	s = s.replace("{Dir} ", "");
            	s = s.replace("{File} ", "");
            	final File f = new File(s);
                listener.directorySelected(f);
            }
        });
    }

    private void loadFileList(File path) {
        this.currentPath = path;
        List<String> r = new ArrayList<String>();
        List<String> dirs = new ArrayList<String>(); // array list of folders
        List<String> files = new ArrayList<String>(); // array list of files
            if (path.exists()) {
	            
	        	// ################################################################################################
	        	// if (path.getParentFile() != null ) r.add(PARENT_DIR);
	            
	        	FilenameFilter filter = new FilenameFilter() {
	                public boolean accept(File dir, String filename) {
	                    File sel = new File(dir, filename);
	                    if (!sel.canRead() || !sel.canWrite() || sel.isHidden()) return false;
	                    if (selectDirectoryOption) return sel.isDirectory();
	                    else {
	                        boolean endsWith = fileEndsWith != null ? filename.toLowerCase().endsWith(fileEndsWith) : true;
	                        return endsWith || sel.isDirectory();
	                    }
	                }
	            };
	            if(this.SF.getRootAccess()){ // device is rooted
	            	
	            	String[] fileList1 = Shell.sendShellCommand(
	            			(this.developer) ? new String[]{"su","-c", "ls "+path.toString()} :
	            				new String[] {"ls " +path.toString()},
	            			
	            			this.activity);
	            	if( ((SharpFixApplicationClass)this.activity.getApplicationContext()).getRootPermission() ){
	            		
	            		for (String file : fileList1) {
	                    	File f = new File(currentPath,file);
		                    if(selectDirectoryOption){
		                    	if(f.isDirectory()){
		                    		
		                    		dirs.add(file);
		                    		// r.add(file); //("{Dir} " +file);
		                    		// key = file name, value = image 
		                    		fileListWithImage.put(file.toString(), "folder_icon_2.png");
		                    	}
		                    	
		                    }else{
		                    	if(f.isDirectory()){
		                    		
		                    		dirs.add(file);
		                    		// r.add(file); //("{Dir} " +file);
		                    		// key = file name, value = image 
		                    		fileListWithImage.put(file.toString(), "folder_icon_2.png");
		                    	}else{
		                    		
		                    		files.add(file);
		                    		//r.add(file); //("{File} " +file);
		                    		fileListWithImage.put(file.toString(), "file_icon_2.png");
		                    	}
		                    	
		                    }
	                        
	                    }
	            	}else{
	            		
	            		fileList1 = path.list(filter);
	            		try{
		                    for (String file : fileList1) {
		                    	File f = new File(currentPath,file);	
		                    	if(selectDirectoryOption){
			                    	if(f.isDirectory()){
			                    		
			                    		dirs.add(file);
			                    		// r.add(file); //("{Dir} " +file);
			                    		// key = file name, value = image 
			                    		fileListWithImage.put(file.toString(), "folder_icon_2.png");
			                    	}
			                    	
			                    }else{
			                    	if(f.isDirectory()){
			                    		
			                    		dirs.add(file);
			                    		// r.add(file); //("{Dir} " +file);
			                    		// key = file name, value = image 
			                    		fileListWithImage.put(file.toString(), "folder_icon_2.png");
			                    	}else{
			                    		
			                    		files.add(file);
			                    		//r.add(file); //("{File} " +file);
			                    		fileListWithImage.put(file.toString(), "file_icon_2.png");
			                    	}
			                    	
			                    }
		                        
		                    }
	            		}catch(Exception e){
	            			Log.e(TAG,"WARNING! ROOT PERMISSION WAS FORCEFULLY DENIED! ");
	            			
	            		}
	            	}
	            	
	            }else{
	            	String[] fileList1 = path.list(filter);
	                for (String file : fileList1) {
	                	File f = new File(currentPath,file);
	                	if(selectDirectoryOption){
	                    	if(f.isDirectory()){
	                    		
	                    		dirs.add(file);
	                    		// r.add(file); //("{Dir} " +file);
	                    		// key = file name, value = image 
	                    		fileListWithImage.put(file.toString(), "folder_icon_2.png");
	                    	}
	                    	
	                    }else{
	                    	if(f.isDirectory()){
	                    		
	                    		dirs.add(file);
	                    		// r.add(file); //("{Dir} " +file);
	                    		// key = file name, value = image 
	                    		fileListWithImage.put(file.toString(), "folder_icon_2.png");
	                    	}else{
	                    		
	                    		files.add(file);
	                    		//r.add(file); //("{File} " +file);
	                    		fileListWithImage.put(file.toString(), "file_icon_2.png");
	                    	}
	                    	
	                    }
	                    
	                }
	            }
	            
	        }
            
            // sort all r
            if(selectDirectoryOption){
            	Collections.sort(dirs);
                r.addAll(dirs);
                fileList = (String[]) r.toArray(new String[]{});
                AndroidLayoutUtils.CustomListView.Model.refreshModel();
    	        for (int i= 0; i < fileList.length; i++){
    	        	AndroidLayoutUtils.CustomListView.Model.LoadModel(i,
    	        			"folder_icon_2.png"
    	        				, fileList[i]);
    	        }
            }else{
            	Collections.sort(dirs);
                Collections.sort(files);
                r.addAll(dirs);
                r.addAll(files);
                fileList = (String[]) r.toArray(new String[]{});
                AndroidLayoutUtils.CustomListView.Model.refreshModel();
    	        for (int i= 0; i < fileList.length; i++){
    	        	AndroidLayoutUtils.CustomListView.Model.LoadModel(i,
    	        			(fileListWithImage.get(fileList[i]) != null ? fileListWithImage.get(fileList[i]): 
    	        				"folder_icon_2.png")
    	        				, fileList[i]);
    	        }
            }
            
	        
        

        
    }

    private File getChosenFile(String fileChosen) {
    	fileChosen = fileChosen.replace("{Dir} ", "");
    	fileChosen = fileChosen.replace("{File} ", "");
    	
        if (fileChosen.equals(PARENT_DIR)) return (!currentPath.getAbsolutePath().equals(topMostPath.getAbsolutePath())) ? currentPath.getParentFile() : topMostPath;
        else return new File(currentPath, fileChosen);
    }

    public void setFileEndsWith(String fileEndsWith) {
        this.fileEndsWith = fileEndsWith != null ? fileEndsWith.toLowerCase() : fileEndsWith;
    }
 }

class ListenerList<L> {
	private List<L> listenerList = new ArrayList<L>();
	
	public interface FireHandler<L> {
	    void fireEvent(L listener);
	}
	
	public void add(L listener) {
	    listenerList.add(listener);
	}
	
	public void fireEvent(FireHandler<L> fireHandler) {
	    List<L> copy = new ArrayList<L>(listenerList);
	    for (L l : copy) {
	        fireHandler.fireEvent(l);
	    }
	}
	
	public void remove(L listener) {
	    listenerList.remove(listener);
	}
	
	public List<L> getListenerList() {
	    return listenerList;
	}
}
