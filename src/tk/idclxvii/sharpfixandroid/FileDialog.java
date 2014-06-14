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
    public FileDialog(Activity activity, File path) {
        this.activity = activity;
        this.db = this.getDb(this.activity);
        
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    /**
     * @return file dialog
     */
    public Dialog createFileDialog() {
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
        
        
       // dialog = builder.show();
       dialog = builder.create();
       //final AlertDialog d = (AlertDialog) dialog;
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
						
					}catch(Exception e){
						if(LOGCAT){
							fileTypeVal.setText("Unknown File");
							Log.d(TAG,"Unknown File Type detected: " + f.toString());
							try{
								Log.d(TAG,"4 bytes signature: " + FileProperties.getMagicNumber(f, 4));
								Log.d(TAG,"4 bytes signature: " + FileProperties.getMagicNumber(f, 8));
							}catch(Exception eee){
								if(LOGCAT){
									Log.e(TAG,"An error in getting a file's n-bytes signature has been encountered!");
									
								}
							}
						}
					}
					
					
					
					fileTypeVal.setText((f.isDirectory()? "Folder" : (ft.contains("File") ? ft : ft + " File") ));
					
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
        if (path.exists()) {
            if (path.getParentFile() != null) r.add(PARENT_DIR);
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
            String[] fileList1 = path.list(filter);
            for (String file : fileList1) {
            	File f = new File(currentPath,file);
            	if(f.isDirectory()){
            		r.add("{Dir} " +file);
            	}else{
            		r.add("{File} " +file);
            	}
                
            }
        }
        fileList = (String[]) r.toArray(new String[]{});
    }

    private File getChosenFile(String fileChosen) {
    	fileChosen = fileChosen.replace("{Dir} ", "");
    	fileChosen = fileChosen.replace("{File} ", "");
    	
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
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
