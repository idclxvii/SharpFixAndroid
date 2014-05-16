package com.freetzi.idclxvii.sharpfixandroid;


import java.util.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.*;
import android.widget.Toast;

import java.lang.reflect.*;
import com.freetzi.idclxvii.sharpfixandroid.databasemodel.*;

public class SQLiteHelper extends SQLiteOpenHelper {

	
	 // Logcat tag and switch
    private static final String TAG = "SQLiteHelper";
    private static boolean LOGCAT = true;
 
    // Database Version
    private static final int DATABASE_VERSION = 8;
 
    // Database Name
    private static final String DATABASE_NAME = "sharpfix_database.db";
    
    // Table Names
    private static final String[]  TABLES = {"sd_info","dirs_info","files_info","accounts_info", 
    		"magic_number","file_designation_settings",  "preferences", "dir_filter","file_filter"};
    
    // sd_info
    private static final String CREATE_SD_INFO = "CREATE TABLE"
             +" sd_info ( id INTEGER PRIMARY KEY, path TEXT NOT NULL , last_mod BIGINT NOT NULL  )";
 
    // dirs_info
    private static final String CREATE_DIRS_INFO = "CREATE TABLE"
            +" dirs_info ( path TEXT NOT NULL  PRIMARY KEY, sd_id INTEGER NOT NULL , last_mod BIGINT  NOT NULL , " +
            "FOREIGN KEY (sd_id) REFERENCES sd_info(id) )";
   
    private static final String CREATE_FILES_INFO = "CREATE TABLE"
            +" files_info ( path TEXT  NOT NULL PRIMARY KEY, dir TEXT NOT NULL , last_mod BIGINT NOT NULL , " +
            "crc32 TEXT NOT NULL , md5 TEXT NOT NULL , sha1 TEXT NOT NULL , size TEXT NOT NULL, " +
            " FOREIGN KEY (dir) REFERENCES dirs_info(path) )";
   
    private static final String CREATE_ACCOUNTS_INFO = "CREATE TABLE"
            +" accounts_info ( id INTEGER PRIMARY KEY, login TEXT NOT NULL UNIQUE, password TEXT NOT NULL )";

    private static final String CREATE_MAGIC_NUMBER = "CREATE TABLE"
            +" magic_number ( id INTEGER PRIMARY KEY, file_type TEXT  NOT NULL , signature_8_bytes TEXT NOT NULL, signature_4_bytes TEXT, " +
            "mime TEXT NOT NULL )";
    
    private static final String CREATE_FD_SETTINGS = "CREATE TABLE"
            +" file_designation_settings ( id INTEGER PRIMARY KEY, account INTEGER NOT NULL " +
            ", rule_name TEXT NOT NULL , designation_path TEXT NOT NULL , file_type TEXT NOT NULL UNIQUE," +
            "FOREIGN KEY  (account) REFERENCES accounts_info(id), " +
            "FOREIGN KEY (file_type) REFERENCES magic_number(file_type) )";
    
    private static final String CREATE_PREFERENCES = "CREATE TABLE"
            +" preferences ( id INTEGER PRIMARY KEY, account INTEGER NOT NULL , fdd_switch INTEGER NOT NULL " +
            ", fd_switch INTEGER NOT NULL , " +
            "filter_switch INTEGER NOT NULL , fdd_pref INTEGER NOT NULL , auto_login INTEGER NOT NULL," +
            "FOREIGN KEY  (account) REFERENCES accounts_info(id) )";
    
    private static final String CREATE_DIR_FILTER = "CREATE TABLE"
            +" dir_filter ( id INTEGER PRIMARY KEY, account INTEGER NOT NULL , dir TEXT NOT NULL , " +
            "FOREIGN KEY  (account) REFERENCES accounts_info(id)," +
            "FOREIGN KEY (dir) REFERENCES dirs_info(path) )";
    
    private static final String CREATE_FILE_FILTER = "CREATE TABLE"
            +" file_filter ( id INTEGER PRIMARY KEY, account INTEGER NOT NULL , file TEXT NOT NULL , " +
            "FOREIGN KEY  (account) REFERENCES accounts_info(id)," +
            "FOREIGN KEY (file) REFERENCES files_info(path) )";
   
	private  final String mime[][]  = {
			
	
	// FILE TYPE | 8 bytes | 4 bytes | MIME		
	
	// image formats
	{"(*.jpeg, *.jpg) JPG Image Format","FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFD8 FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFE0 0 10 4A 46",
		"FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFD8 FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFE1","MIME: image/jpg"},
	{"(*.png) PNG Image Format","FFFFFFFFFFFFFF89 50 4E 47 D A 1A A","NULL","MIME: image/png"},
	{"(*.ico) Icon Image Format","0 0 1 0 4 0 10 10","NULL","MIME: image/icon"},
	{"(*.gif) Image GIF Format","NULL","47 49 46 38","MIME: image/gif"},
	// audio formats
	
	{"(*.mp3) Audio File MP3 Format","49 44 33 3 0 0 0 0","49 44 33 4","MIME: audio/mp3"},
	{"(*.mp3) Audio File MP3 Format","49 44 33 3 0 0 0 0","49 44 33 3","MIME: audio/mp3"},
	{"(*.mp3) Audio File MP3 Format","49 44 33 3 0 0 0 0","49 44 33 2","MIME: audio/mp3"},
	
	// video formats
	{"(*.mp4) Video File MP4 Format","0 0 0 20 66 74 79 70","NULL","MIME: video/mp4"},
	
	// microsoft exclusive formats
	{ "(*.docx,*.pptx, *.xlsx) Microsoft Office Document Format","50 4B 3 4 14 0 6 0","NULL","MIME: application/msoffice"},
	{ "(*.lnk) Microsoft Windows Application Link","4C 0 0 0 1 14 2 0","NULL","MIME: application/shortcut"},
	{ "(*.rar) WinRAR Archive","52 61 72 21 1A 7 0 FFFFFFFFFFFFFFCF","52 61 72 21","MIME: application/rar"},
	{"(*.zip) WinRAR Zip Archive","50 4B 3 4 A 0 0 0","NULL","MIME: application/zip"},
	{"(*.zip) WinRAR Zip Archive","50 4B 3 4 14 0 2 0","NULL","MIME: application/zip"},
	{ "(*.class) Java Class Application File","FFFFFFFFFFFFFFCA FFFFFFFFFFFFFFFE FFFFFFFFFFFFFFBA FFFFFFFFFFFFFFBE 0 0 0 33",
		"FFFFFFFFFFFFFFCA FFFFFFFFFFFFFFFE FFFFFFFFFFFFFFBA FFFFFFFFFFFFFFBE","MIME: application/java-vm"},
	{"(*.exe, *.dll) Microsoft Windows Executable/Application","4D 5A FFFFFFFFFFFFFF90 0 3 0 0 0","NULL","application/win32"},
	{ "(*.msi) Microsoft Windows Application Installer","FFFFFFFFFFFFFFD0 FFFFFFFFFFFFFFCF 11 FFFFFFFFFFFFFFE0","NULL","application/win32"},
	
	{"(*.sln) Microsoft Visual Studio Solution","FFFFFFFFFFFFFFEF FFFFFFFFFFFFFFBB FFFFFFFFFFFFFFBF D A 4D 69 63","NULL","MIME: application/msvs"},
	{"(*.suo, *.db) Microsoft Visual Studio Solution User Options | Database File",
		"FFFFFFFFFFFFFFD0 FFFFFFFFFFFFFFCF 11 FFFFFFFFFFFFFFE0 FFFFFFFFFFFFFFA1 FFFFFFFFFFFFFFB1 1A FFFFFFFFFFFFFFE1",
		"NULL","MIME: application/msvs"},
	{"(*.pdb) Microsoft Program Debug Database","4D 69 63 72 6F 73 6F 66","NULL", "MIME: application/win32"},
	
	{"(*.one Microsoft OneNote Section)","FFFFFFFFFFFFFFE4 52 5C 7B FFFFFFFFFFFFFF8C FFFFFFFFFFFFFFD8 FFFFFFFFFFFFFFA7 4D",
		"NULL","MIME: application/msonenote"},
	{"(*.psd) Adobe Photoshop Image File","38 42 50 53 0 1 0 0","NULL","MIME: application/psd"},
	{"(*.chm) Microsoft Windows Compiled HTML Help File","49 54 53 46 3 0 0 0","NULL","MIME: application/win32"},
	
	// linux exclusive formats
	{"(*.o, *.*) Linux/Unix Executable and Linking File Format","NULL","7F 45 4C 46","application/linux"},
	{"(*.*) Linux/Unix Binary File","NULL","2F4 2F 20 54","application/linux"},
	
	
	
	//general formats
	{"(*.xml, *xspf) Extensible Markup Language File","3C 3F 78 6D 6C 20 76 65","3C 72 65 73","MIME: application/xml" },
	{"(*.xml, *xspf) Extensible Markup Language File","3C 3F 78 6D 6C 20 76 65","3C 6D 65 6E","MIME: application/xml" },
	{"(*.xml, *xspf) Extensible Markup Language File","3C 3F 78 6D 6C 20 76 65","52 49 46 46","MIME: application/xml" },
	{"(*.bin) Native Binary File","FFFFFFFFFFFFFFCF 77 54 FFFFFFFFFFFFFFAB 3 0 0 0","NULL","MIME: application/bin"},
	
	{"(*.bin, *.img) Alcohol 120% CD Image",
		"0 FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF FFFFFFFFFFFFFFFF",
		"NULL","MIME: application/img"},
	{"(*.cso ) Sony Playstation Portable CSO Image","43 49 53 4F 0 0 0 0","NULL","MIME: application/cso"},
	{"(*.torrent) Torrent File","64 38 3A 61 6E 6E 6F 75","NULL","MIME: application/torrent"},
	{"(*.jar, *.apk) Executable Java Archive File","50 4B 3 4 14 0 8 8","NULL","MIME: application/java-vm"},
	{"(*.jar) Java Archive File","50 4B 3 4 14 0 8 0","NULL","MIME: application/java-vm"},
	{"(*.jar) Java Archive File","50 4B 3 4 14 0 0 0","NULL","MIME: application/java-vm"},
	{"(*.properties) Eclipse Project Properties Configuration File","23 20 54 68 69 73 20 66","NULL","MIME: application/eclipse"},
	{"(*.lib, *.a, *.coff) Linux Executable Library |Unix Archiver Files | Microsoft Program Library Common Object File Format",
		"21 3C 61 72 63 68 3E A","NULL","MIME: application/*"},
	{"(*.dex) Dalvik Executable File | Android Executable File","NULL","64 65 78 A","application/android"},
		
	{"(*.pdf) Adobe Portable Document Format","25 50 44 46 2D 31 2E 33","NULL","MIME: unidentified/undefined"},
	{"(*.pdf) Adobe Portable Document Format","25 50 44 46 2D 31 2E 34","NULL","MIME: unidentified/undefined"},
	{"(*.pdf) Adobe Portable Document Format","25 50 44 46 2D 31 2E 35","NULL","MIME: unidentified/undefined"},
	{"(*.pdf) Adobe Portable Document Format","NULL","25 50 44 46","MIME: unidentified/undefined"},
	
	
	
	
	
	
	};

	
	
	private void createMagicNumberDatabase(SQLiteDatabase db){
		for(int x = 0; x < mime.length; x++){
			try{
				if(this.insert(Tables.magic_number, new ModelMagicNumber(mime[x][0],mime[x][1],mime[x][2],mime[x][3]), db)){
					Log.i(TAG, "Successful in insertion "+ mime[x][0]+ " " + mime[x][1] +" "+mime[x][2] + " " + mime[x][3]);
				}else{
					Log.w(TAG, "Failure in insertion "+ mime[x][0]+ " " + mime[x][1] +" "+mime[x][2] + " " + mime[x][3]);
				}
				
			}catch(Exception e){
				if(LOGCAT){
					StackTraceElement[] st = e.getStackTrace();
					for(int y= 0; y <st.length; y++){
						Log.w(TAG, st[y].toString());
						
					}
				}
					
			}
		}
	}
    
    
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
 
        // creating required tables
    	
        db.execSQL(CREATE_SD_INFO);
        db.execSQL(CREATE_DIRS_INFO);
        db.execSQL(CREATE_FILES_INFO);
        db.execSQL(CREATE_ACCOUNTS_INFO);
        db.execSQL(CREATE_MAGIC_NUMBER);
        db.execSQL(CREATE_FD_SETTINGS);
        db.execSQL(CREATE_PREFERENCES);
        db.execSQL(CREATE_DIR_FILTER);
        db.execSQL(CREATE_FILE_FILTER);
        try{
        	// default insertions
        	/*
        	this.insert(Tables.accounts_info, new ModelAccountsInfo("idclxvii","6582ceacd9ae19c9147b72e12ea344a1"),db);
        	ModelAccountsInfo mai = 
        			(ModelAccountsInfo)this.select(Tables.accounts_info, ModelAccountsInfo.class,
        					new Object[][]{{"login","idclxvii"},{"password", "6582ceacd9ae19c9147b72e12ea344a1"}},db);
        	this.insert(Tables.preferences, new ModelPreferences(mai.getId(), 1,1,1,1,0),db);
        	*/
        	Object[][] res = new Object[Tables.values().length][];
        	this.result.toArray(res);
            for(int x = 0; x < Tables.values().length; x++){
            	for(int y = 0; y < res[x].length; y++){
            		try{
            			this.insert(Tables.values()[x], res[x][y],db);
            		}catch(Exception ee){
            			if(LOGCAT){
                			StackTraceElement[] st = ee.getStackTrace();
            				for(int z= 0; z <st.length; z++){
            					Log.w(TAG, st[z].toString());
            					
            				}
                		}
            		}
            		
            	}
    		}
            this.createMagicNumberDatabase(db);
        }catch(Exception e){
        	if(LOGCAT){
        		StackTraceElement[] st = e.getStackTrace();
				for(int y= 0; y < st.length; y++){
					Log.w(TAG, st[y].toString());
					
				}
        	}try{
        		this.createMagicNumberDatabase(db);
        	}catch(Exception ee){
        		if(LOGCAT){
            			Log.w(TAG, "EXCEPTION CAUGHT! FAILED CREATING MAGIC_NUMBER DATABASE!");
    				}
        	}
        	
        }
    	
        
    }
 
    private List<Object[]> result = new ArrayList<Object[]>();
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    	try{
    		//ModelAccountsInfo[] mai = (ModelAccountsInfo[]) this.selectAll(Tables.accounts_info, ModelAccountsInfo.class);
    		for(int x = 0; x < Tables.values().length; x++){
    			if(Tables.values()[x] != Tables.magic_number){
	    			Object[] res =  this.selectAll(Tables.values()[x], Class.forName("com.freetzi.idclxvii.sharpfixandroid.databasemodel."+Models.values()[x].toString()), db);
	    			//List<Object> r = new ArrayList<Object>();
	    			//r.add(res);
	    			this.result.add(res);
    			}else{
    				this.result.add(new Object[0]);
    			}
    			
    		}
    		
    	}catch(SQLiteException sqle){
    		if(LOGCAT){
    			StackTraceElement[] st = sqle.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.w(TAG, st[y].toString());
					
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

    	// on upgrade drop older tables
    	for(int x = TABLES.length-1; x > -1; x--){
  		  db.execSQL("DROP TABLE IF EXISTS "+TABLES[x]);
    	}
    	// create new tables
        onCreate(db);
    }
    
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    	try{
    		//ModelAccountsInfo[] mai = (ModelAccountsInfo[]) this.selectAll(Tables.accounts_info, ModelAccountsInfo.class);
    		for(int x = 0; x < Tables.values().length; x++){
    			if(Tables.values()[x] != Tables.magic_number){
	    			Object[] res =  this.selectAll(Tables.values()[x], Class.forName("com.freetzi.idclxvii.sharpfixandroid.databasemodel."+Models.values()[x].toString()), db);
	    			//List<Object> r = new ArrayList<Object>();
	    			//r.add(res);
	    			this.result.add(res);
    			}else{
    				this.result.add(new Object[0]);
    			}
    		}
    		
    	}catch(SQLiteException sqle){
    		if(LOGCAT){
    			StackTraceElement[] st = sqle.getStackTrace();
				for(int y= 0; y <st.length; y++){
					Log.w(TAG, st[y].toString());
					
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

    	// on upgrade drop older tables
    	for(int x = TABLES.length-1; x > -1; x--){
  		  db.execSQL("DROP TABLE IF EXISTS "+TABLES[x]);
    	}
    	// create new tables
        onCreate(db);
    
    }
    
    
    /*
     * Returns a list of Object containing the result set of the 
     * query "SELECT * FROM <Table_Name>;"
     * 
     * Parameters:
     * 	table - the name of the query's target table
     * 	cls - the table model where the result set bases the table fields
     * 	db - the SQLiteDatabase instance where this query is being done.
     * 		If db is null, the method uses the current instance (this.getReadableDatabase())
     */ 
    public Object[] selectAll(Tables table, Class<?> cls, SQLiteDatabase db) 
    		throws InstantiationException, IllegalAccessException,
    		NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
    	
    	// class manipulation
    	
    	List<Object> resultSet = new ArrayList<Object>();
     	Object cl =  cls.newInstance();
     	Method m = cls.getMethod("getFields");
     	String[] fields = (String[]) m.invoke(cl, null);
     	
    	String sql = "SELECT * FROM " +table.toString();
    	if(LOGCAT){
    		Log.d(TAG,"Select All SQL: " +sql);
    		
    	}
    	if(db != null){
    		
    	}else{
    		db = this.getReadableDatabase();
        }
    	Cursor c = db.rawQuery(sql, null);
    	if (c.moveToFirst()) {
            do {
            	cl = cls.newInstance();
           	 	for(String field : fields){
           	 		String methodName = "set"+field;
            		try{
            		 // try if the current method is of type Integer
            			Integer arg = c.getInt((c.getColumnIndex(field.toLowerCase())));
	            		m = cls.getMethod(methodName, Integer.class);
	            		m.invoke(cl,arg);
	        		}catch(Exception e){
	        			try{
            			// try if the current method is of type Long
	        				Long arg = c.getLong((c.getColumnIndex(field.toLowerCase())));
            				m = cls.getMethod(methodName, Long.class);
    	            		m.invoke(cl,arg);
            			}catch(Exception ee){
            				try{
            				// try if the current method is of type String
            					String arg = c.getString((c.getColumnIndex(field.toLowerCase())));
            					m = cls.getMethod(methodName, String.class);
        	            		m.invoke(cl,arg);
            				}catch(Exception eee){
            					if(LOGCAT){
            		    			StackTraceElement[] st = e.getStackTrace();
            						for(int y= 0; y <st.length; y++){
            							Log.w(TAG, st[y].toString());
            							
            						}
            		    		}
            				}
            			 }
            		 }
            	 }
            	 resultSet.add(cl);
             } while (c.moveToNext());
         }
    	c.close();
    	this.close();
        return resultSet.toArray();
    }

    public Object[] selectMulti(Tables table, Class<?> cls, Object[][] params, SQLiteDatabase db)
    		throws InstantiationException, IllegalAccessException,
    		NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
    	
    	String sql = "SELECT * FROM "+table.toString()+" WHERE ";   	
    	for(int x =0; x < params.length; x++){
    		if(x == params.length - 1){
    			sql+= (params[x][0].toString()+" = '" + params[x][1].toString()+"';");
        		
    		}else{
    			sql+= (params[x][0].toString()+" = '" + params[x][1].toString()+"' AND ");
        		
    		}
    	}
    	if(LOGCAT){
    		Log.d(TAG,"Select Multi SQL: " +sql);
    		
    	}
    	if(db != null){
    		
    	}else{
    		db = this.getReadableDatabase();
        }// class manipulation
		List<Object> resultSet = new ArrayList<Object>();
		Object cl =  cls.newInstance();
		Method m = cls.getMethod("getFields");
		String[] fields = (String[]) m.invoke(cl, null);
		Cursor c = db.rawQuery(sql, null);
		if (c.moveToFirst()) {
			do{
				cl = cls.newInstance();
				for(String field : fields){
					String methodName = "set"+field;
		    		try{
		    			// try if the current method is of type Integer
		        		Integer arg = c.getInt((c.getColumnIndex(field.toLowerCase())));
		        		m = cls.getMethod(methodName, Integer.class);
		        		m.invoke(cl,arg);
		    		}catch(Exception e){
		    			try{
		    				// try if the current method is of type Long
		    				Long arg = c.getLong((c.getColumnIndex(field.toLowerCase())));
		    				m = cls.getMethod(methodName, Long.class);
			        		m.invoke(cl,arg);
		    			}catch(Exception ee){
		    				try{
		    					// try if the current method is of type String
		    					String arg = c.getString((c.getColumnIndex(field.toLowerCase())));
		    					m = cls.getMethod(methodName, String.class);
				        		m.invoke(cl,arg);
		    				}catch(Exception eee){
		    					if(LOGCAT){
		    		    			StackTraceElement[] st = e.getStackTrace();
		    						for(int y= 0; y <st.length; y++){
		    							Log.w(TAG, st[y].toString());
		    							
		    						}
		    		    		}
		    				}
		    			}
		    		}
		    	}
		    	resultSet.add(cl);
		    }while (c.moveToNext());
		}
		c.close();
		this.close();
		return resultSet.toArray();	
    }
    
    public Object select(Tables table, Class<?> cls, Object[][] params, SQLiteDatabase db)
    		throws InstantiationException, IllegalAccessException,
    		NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
    
    	String sql = "SELECT * FROM "+table.toString()+" WHERE ";   	
    	for(int x =0; x < params.length; x++){
    		if(x == params.length - 1){
    			sql+= (params[x][0].toString()+" = '" + params[x][1].toString()+"';");
        	}else{
    			sql+= (params[x][0].toString()+" = '" + params[x][1].toString()+"' AND ");
        	}
    	}
    	if(LOGCAT){
    		Log.d(TAG,"Select SQL: " +sql);
    		
    	}
    	if(db != null){
    		
    	}else{
    		db = this.getReadableDatabase();
        }// class manipulation
		Object resultSet =  cls.newInstance();
		Method m = cls.getMethod("getFields");
		String[] fields = (String[]) m.invoke(resultSet, null);
		Cursor c = db.rawQuery(sql, null);

		if (c != null){
			c.moveToFirst();
			for(String field : fields){
	   			String methodName = "set"+field;
	   			try{
	   			// try if the current method is of type Integer
	       			Integer arg = c.getInt((c.getColumnIndex(field.toLowerCase())));
	       			m = cls.getMethod(methodName, Integer.class);
	       			m.invoke(resultSet,arg);
	   			}catch(Exception e){
	   				try{
	   			// try if the current method is of type Long
	   					Long arg = c.getLong((c.getColumnIndex(field.toLowerCase())));
	   					m = cls.getMethod(methodName, Long.class);
		       			m.invoke(resultSet,arg);
	   				}catch(Exception ee){
	   					try{
	   					// try if the current method is of type String
	   						String arg = c.getString((c.getColumnIndex(field.toLowerCase())));
	   						m = cls.getMethod(methodName, String.class);
	   		       			m.invoke(resultSet,arg);
	   					}catch(Exception eee){
	   						if(LOGCAT){
	   			    			StackTraceElement[] st = e.getStackTrace();
	   							for(int y= 0; y <st.length; y++){
	   								Log.w(TAG, st[y].toString());
	   								
	   							}
	   			    		}
	   					}
	   				}
	   			}
	   		}
		}
		c.close();
		this.close();
	   	return resultSet;	
    }
    
    public boolean insert(Tables table, Object params, SQLiteDatabase db)	
    		throws InstantiationException, IllegalAccessException,
    		NoSuchMethodException, IllegalArgumentException, InvocationTargetException, SQLiteConstraintException{
    	if(db != null){
    		
    	}else{
    		db = this.getWritableDatabase();
        }
    	// class manipulation
		Method m = params.getClass().getMethod("getFields");
		String[] fields = (String[]) m.invoke(params, null);
		ContentValues values = new ContentValues();
		for(String field : fields){
	   		String methodName = "get"+field;
	   		try{
	   			// try if the current method is of type Integer
	       		m = params.getClass().getMethod(methodName);
	       		values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
	       				(Integer)m.invoke(params));
	   		}catch(Exception e){
	   			try{
	   			// try if the current method is of type Long
	   				m = params.getClass().getMethod(methodName);
		       		values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
		       				(Long)m.invoke(params));
	   			}catch(Exception ee){
	   				try{
	   					// try if the current method is of type String
	   					m = params.getClass().getMethod(methodName);
	   		       		values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
	   		       				(String)m.invoke(params));
	   				}catch(Exception eee){
	   					if(LOGCAT){
	   		    			StackTraceElement[] st = e.getStackTrace();
	   						for(int y= 0; y <st.length; y++){
	   							Log.w(TAG, st[y].toString());
	   							
	   						}
	   		    		}
	   				}
	   			}
	   		}
	   	}
		if(LOGCAT){
    		Log.d(TAG,"INSERT INTO "+table.toString()+" " + values);
    		
    	}
		return ((db.insert(table.toString(), null, values) == -1) ? false : true);
		
    }

    public boolean update(Tables table, Object oldParams, Object newParams, SQLiteDatabase db)throws InstantiationException, IllegalAccessException,
	NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
    	
    	if(db != null){
    		
    	}else{
    		this.getWritableDatabase().close();
    		db = this.getWritableDatabase();
        }
		// class manipulation
		Method m = newParams.getClass().getMethod("getFields");
		String[] fields = (String[]) m.invoke(newParams, null);
	    ContentValues values = new ContentValues();
	    String whereSql = "";
	    List<String> whereParams = new ArrayList<String>();
	    
		for(String field : fields){
				String methodName = "get"+field;
				String res = null;
				try{
					// try if the current method is of type Integer
			   		m = newParams.getClass().getMethod(methodName);
			   		if((Integer)m.invoke(newParams) != null){
			   			values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
				   				(Integer)m.invoke(newParams));
				   	
				   	}
			   		m = oldParams.getClass().getMethod(methodName);
			   		res = Integer.toString((Integer) m.invoke(oldParams));
				}catch(Exception e){
					try{
					// try if the current method is of type Long
					m = newParams.getClass().getMethod(methodName);
		       		if((Long)m.invoke(newParams) != null){
		       			values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
			       				(Long)m.invoke(newParams));
			       		
				   	}
		       		m = oldParams.getClass().getMethod(methodName);
		       		res = Long.toString((Long) m.invoke(oldParams));
					}catch(Exception ee){
						try{
							// try if the current method is of type String
							m = newParams.getClass().getMethod(methodName);
				       		if((String)m.invoke(newParams) != null){
				       			values.put(new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")),
					       				(String)m.invoke(newParams));
					       		
				       		}
				       		m = oldParams.getClass().getMethod(methodName);
				       		res = (String) m.invoke(oldParams);
						}catch(Exception eee){
							if(LOGCAT){
				    			StackTraceElement[] st = e.getStackTrace();
								for(int y= 0; y <st.length; y++){
									Log.w(TAG, st[y].toString());
									
								}
				    		}
						}
					}
				}
				// String res = (String) m.invoke(oldParams);
				if (res != null){
					whereParams.add(res);
					whereSql += (new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")) +
						" = ? AND ");
				}
				
				
			}
		whereSql = whereSql.substring(0,whereSql.length()-5) + ";";
		String[] finalwhereParams = whereParams.toArray(new String[whereParams.size()]);
		if(LOGCAT){
    		Log.d(TAG,"UPDATE FROM " +table.toString() + " " + whereSql +", VALUES: " + values );
    		
    	}
		return ((db.update(table.toString(), values, whereSql,
				finalwhereParams)) > 0 ? true: false);
    }
    
    public boolean delete(Tables table, Object params, SQLiteDatabase db)throws InstantiationException, IllegalAccessException,
	NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
    	if(db != null){
    		
    	}else{
    		db = this.getWritableDatabase();
        }
		// class manipulation
		Method m = params.getClass().getMethod("getFields");
		String[] fields = (String[]) m.invoke(params, null);
	    String whereSql = "";
	    List<String> whereParams = new ArrayList<String>();
		for(String field : fields){
				String methodName = "get"+field;
				String res = null;
				try{
					// try if the current method is of type Integer
			   		m = params.getClass().getMethod(methodName);
			   		if((Integer)m.invoke(params) != null){
			     		m = params.getClass().getMethod(methodName);
			     		res = Integer.toString((Integer)m.invoke(params));
				   	}
				}catch(Exception e){
					try{
						// try if the current method is of type Long
						m = params.getClass().getMethod(methodName);
			       		if((Long)m.invoke(params) != null){
			           		m = params.getClass().getMethod(methodName);
			           		res = Long.toString((Long)m.invoke(params));
					   	}
			       		
					}catch(Exception ee){
						try{
								// try if the current method is of type String
								m = params.getClass().getMethod(methodName);
					       		if((String)m.invoke(params) != null){
						       	m = params.getClass().getMethod(methodName);
						       	res = (String)m.invoke(params);	
				       		}
						}catch(Exception eee){
							if(LOGCAT){
				    			StackTraceElement[] st = e.getStackTrace();
								for(int y= 0; y <st.length; y++){
									Log.w(TAG, st[y].toString());
									
								}
				    		}
						}
					}
				}
				//String res = (String) m.invoke(params);
				if (res != null){
					whereParams.add(res);
					whereSql += (new String(Character.toLowerCase(field.charAt(0)) + (field.length() > 1 ? field.substring(1) :"")) +
						" = ? AND ");
				}
			}
		whereSql = whereSql.substring(0,whereSql.length()-5) + ";";
		String[] finalwhereParams = whereParams.toArray(new String[whereParams.size()]);
		return ((db.delete(table.toString(), whereSql,
				finalwhereParams)) > 0 ? true: false);
	
    }
    
    public void closeConnection() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
    
}
