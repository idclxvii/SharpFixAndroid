package tk.idclxvii.sharpfixandroid;
//DirectoryChooserDialog.java


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tk.idclxvii.sharpfixandroid.databasemodel.ModelMagicNumber;
import tk.idclxvii.sharpfixandroid.databasemodel.Tables;
import tk.idclxvii.sharpfixandroid.utils.FileProperties;
import tk.idclxvii.sharpfixandroid.utils.FolderProperties;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class FolderDialog {
	
	private SharpFixApplicationClass SF;
	private boolean LOGCAT = true;
	private final String TAG = getClass().getName();
    	
	private SQLiteHelper db;
	
	 private synchronized SQLiteHelper getDb(Context context){
			db = new SQLiteHelper(context);
			
			return this.db;
		}
	
 private boolean m_isNewFolderEnabled = true;
 private String m_sdcardDirectory = "";
 private Context m_context;
 private TextView m_titleView;

 private String m_dir = "";
 private List<String> m_subdirs = null;
 private ChosenDirectoryListener m_chosenDirectoryListener = null;
 private ArrayAdapter<String> m_listAdapter = null;

 //////////////////////////////////////////////////////
 // Callback interface for selected directory
 //////////////////////////////////////////////////////
 public interface ChosenDirectoryListener 
 {
     public void onChosenDir(String chosenDir);
 }

 public FolderDialog(Context context, ChosenDirectoryListener chosenDirectoryListener)
 {
     m_context = context;
     m_sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
     m_chosenDirectoryListener = chosenDirectoryListener;

     try
     {
         m_sdcardDirectory = new File(m_sdcardDirectory).getCanonicalPath();
     }
     catch (IOException ioe)
     {
     }
 }

 ///////////////////////////////////////////////////////////////////////
 // setNewFolderEnabled() - enable/disable new folder button
 ///////////////////////////////////////////////////////////////////////

 public void setNewFolderEnabled(boolean isNewFolderEnabled)
 {
     m_isNewFolderEnabled = isNewFolderEnabled;
 }

 public boolean getNewFolderEnabled()
 {
     return m_isNewFolderEnabled;
 }

 ///////////////////////////////////////////////////////////////////////
 // chooseDirectory() - load directory chooser dialog for initial
 // default sdcard directory
 ///////////////////////////////////////////////////////////////////////

 public void chooseDirectory()
 {
     // Initial directory is sdcard directory
     chooseDirectory(m_sdcardDirectory);
 }

 ////////////////////////////////////////////////////////////////////////////////
 // chooseDirectory(String dir) - load directory chooser dialog for initial 
 // input 'dir' directory
 ////////////////////////////////////////////////////////////////////////////////

 public void chooseDirectory(String dir)
 {
     File dirFile = new File(dir);
     if (! dirFile.exists() || ! dirFile.isDirectory())
     {
         dir = m_sdcardDirectory;
     }

     try
     {
         dir = new File(dir).getCanonicalPath();
     }
     catch (IOException ioe)
     {
         return;
     }

     m_dir = dir;
     m_subdirs = getDirectories(dir);

     class DirectoryOnClickListener implements DialogInterface.OnClickListener
     {
         public void onClick(DialogInterface dialog, int item) 
         {
             // Navigate into the sub-directory
             m_dir += "/" + ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
             updateDirectory();
         }
     }

 AlertDialog.Builder dialogBuilder = 
 createDirectoryChooserDialog(dir, m_subdirs, new DirectoryOnClickListener());

 dialogBuilder.setPositiveButton("OK", new OnClickListener() 
 {
     @Override
     public void onClick(DialogInterface dialog, int which) 
     {
         // Current directory chosen
         if (m_chosenDirectoryListener != null)
         {
             // Call registered listener supplied with the chosen directory
             m_chosenDirectoryListener.onChosenDir(m_dir);
         }
     }
 }).setNegativeButton("Cancel", null);

  Dialog dirsDialog = dialogBuilder.create();

 dirsDialog.setOnKeyListener(new OnKeyListener() 
 {
     @Override
     public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
     {
         if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
         {
             // Back button pressed
             if ( m_dir.equals(m_sdcardDirectory) )
             {
                 // The very top level directory, do nothing
                 return false;
             }
             else
             {
                 // Navigate back to an upper directory
                 m_dir = new File(m_dir).getParent();
                 updateDirectory();
             }
 
             return true;
         }
         else
         {
             return false;
         }
     }
 });

 // Show directory chooser dialog
 dirsDialog.setOnShowListener(new OnShowListener(){

	@Override
	public void onShow(DialogInterface dialog) {
		// TODO Auto-generated method stub
		ListView lv = ((AlertDialog) dialog).getListView();
		lv.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
				String selection = ((String)parent.getItemAtPosition(position));
				
				File f = new File(m_dir,selection);
				Log.d(TAG,"Selected Item's Properties:");
				Log.d(TAG, "FILE: " + f.toString());
				Log.d(TAG, "Current Path: " +m_dir);
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
				
				Dialog dx = new Dialog(m_context);
				dx.setContentView(R.layout.file_dialog_properties_dialog);
				dx.setTitle("Selected "+ (f.isDirectory() ? "Folder" : "File") + " Properties: ");
				
				TextView absPath = (TextView) dx.findViewById(R.id.absPath);
				absPath.setText("Absolute Path:");
				TextView absPathVal = (TextView) dx.findViewById(R.id.absPathValue);
				absPathVal.setText(f.toString());
				
				TextView parentPath = (TextView) dx.findViewById(R.id.parentPath);
				parentPath.setText("Parent Directory Path:");
				TextView parentPathVal = (TextView) dx.findViewById(R.id.parentPathValue);
				parentPathVal.setText(m_dir.toString());
				
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
							FolderDialog.this.db.select(
									Tables.magic_number, ModelMagicNumber.class, 
									new Object[][]{ {"signature_8_bytes", FileProperties.getMagicNumber(f, 8)} }, null))).getFile_type();
					if(temp != null){
						ft = temp;
					}else{
						temp = ((ModelMagicNumber) (
								FolderDialog.this.db.select(
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
 
 dirsDialog.show();
}

private boolean createSubDir(String newDir)
{
 File newDirFile = new File(newDir);
 if (! newDirFile.exists() )
 {
     return newDirFile.mkdir();
 }

 return false;
}

private List<String> getDirectories(String dir)
{
 List<String> dirs = new ArrayList<String>();

 try
 {
     File dirFile = new File(dir);
     if (! dirFile.exists() || ! dirFile.isDirectory())
     {
         return dirs;
     }
     // #######################################################################
     FilenameFilter filter = new FilenameFilter() {
         public boolean accept(File dir, String filename) {
             File sel = new File(dir, filename);
             if (!sel.canRead() || !sel.canWrite() || sel.isHidden()){
            	 if(LOGCAT){
            		 Log.d(TAG, "File " +sel.toString() + " is unaccessible!");
            		 Log.d(TAG, "canRead? " +sel.canRead());
            		 Log.d(TAG, "canWrite? " +sel.canWrite());
            		 Log.d(TAG, "isHidden? " +sel.isHidden());
            		 
            	 }
            	 return false;
             }
             
             return sel.isDirectory();
             
         }
     };
     
     String[] fileList1 = dirFile.list(filter);
     for (String file : fileList1) {
     	File f = new File(dirFile,file);
     	if(f.isDirectory()){
     		 dirs.add( f.getName() );
     	}
         
     }
     // #######################################################################
     
  // #######################################################################
     /*
     for (File file : dirFile.listFiles()) 
     {
         if ( file.isDirectory() )
         {
             dirs.add( file.getName() );
         }
     }
     */
  // #######################################################################
     
     
     
 }
 catch (Exception e)
 {
 }

 Collections.sort(dirs, new Comparator<String>()
 {
     public int compare(String o1, String o2) 
     {
         return o1.compareTo(o2);
     }
 });

 return dirs;
}

private AlertDialog.Builder createDirectoryChooserDialog(String title, List<String> listItems,
     DialogInterface.OnClickListener onClickListener)
{
 AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(m_context);

 // Create custom view for AlertDialog title containing 
 // current directory TextView and possible 'New folder' button.
 // Current directory TextView allows long directory path to be wrapped to multiple lines.
 LinearLayout titleLayout = new LinearLayout(m_context);
 titleLayout.setOrientation(LinearLayout.VERTICAL);

 m_titleView = new TextView(m_context);
 m_titleView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
 m_titleView.setTextAppearance(m_context, android.R.style.TextAppearance_Large);
 m_titleView.setTextColor( m_context.getResources().getColor(android.R.color.white) );
 m_titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
 m_titleView.setText(title);

 Button newDirButton = new Button(m_context);
 newDirButton.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
 newDirButton.setText("New folder");
 newDirButton.setOnClickListener(new View.OnClickListener() 
 {
     @Override
     public void onClick(View v) 
     {
         final EditText input = new EditText(m_context);

         // Show new folder name input dialog
         new AlertDialog.Builder(m_context).
         setTitle("New folder name").
         setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() 
         {
             public void onClick(DialogInterface dialog, int whichButton) 
             {
                 Editable newDir = input.getText();
                 String newDirName = newDir.toString();
                 // Create new directory
                 if ( createSubDir(m_dir + "/" + newDirName) )
                 {
                     // Navigate into the new directory
                     m_dir += "/" + newDirName;
                     updateDirectory();
                 }
                 else
                 {
                     Toast.makeText(
                     m_context, "Failed to create '" + newDirName + 
                       "' folder", Toast.LENGTH_SHORT).show();
                 }
             }
         }).setNegativeButton("Cancel", null).show(); 
     }
 });

 if (! m_isNewFolderEnabled)
 {
     newDirButton.setVisibility(View.GONE);
 }

 titleLayout.addView(m_titleView);
 titleLayout.addView(newDirButton);

 dialogBuilder.setCustomTitle(titleLayout);

 m_listAdapter = createListAdapter(listItems);

 dialogBuilder.setSingleChoiceItems(m_listAdapter, -1, onClickListener);
 dialogBuilder.setCancelable(false);

 return dialogBuilder;
}

private void updateDirectory()
{
 m_subdirs.clear();
 m_subdirs.addAll( getDirectories(m_dir) );
 m_titleView.setText(m_dir);

 m_listAdapter.notifyDataSetChanged();
}

private ArrayAdapter<String> createListAdapter(List<String> items)
{
 return new ArrayAdapter<String>(m_context, 
   android.R.layout.select_dialog_item, android.R.id.text1, items)
 {
     @Override
     public View getView(int position, View convertView,
     ViewGroup parent) 
     {
         View v = super.getView(position, convertView, parent);

         if (v instanceof TextView)
         {
             // Enable list item (directory) text wrapping
             TextView tv = (TextView) v;
             tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
             tv.setEllipsize(null);
         }
         return v;
     }
 };
}
}
