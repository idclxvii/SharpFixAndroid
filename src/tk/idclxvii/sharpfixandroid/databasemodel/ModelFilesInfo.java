package tk.idclxvii.sharpfixandroid.databasemodel;

import java.io.*;


public class ModelFilesInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6197558917651171921L;
	
	// data structure:
	private String path;
	private String dir;
	private Long lastMod;
	private String crc32;
	private String md5;
	private String sha1;
	private String size;
	
	public enum fields{
		Path, Dir, Last_mod, Crc32, Md5, Sha1, Size
	}
	
	public String[] getFields(){
		String[] f = new String[fields.values().length];
		for(int x=0; x < f.length; x++ ){
			f[x] = fields.values()[x].toString();
			
		}
		return f;
	}
	// constructors
	public ModelFilesInfo(){
		
	}
	
	public ModelFilesInfo(String path){
		this.path = path;
	}
	public ModelFilesInfo(String path, String dir){
		this.path = path;
		this.dir = dir;
	}
	
	public ModelFilesInfo(String path, String dir, Long lastMod, String crc32, String md5, String sha1, String size){
		this.path = path;
		this.dir = dir;
		this.lastMod = lastMod;
		this.crc32 = crc32;
		this.md5 = md5;
		this.sha1 = sha1;
		this.size = size;
	}
	
	// setters
	public void setPath(String path){
		this.path = path;
	}
	
	public void setDir(String dir){
		this.dir = dir;
	}
	
	public void setLast_mod(Long lastMod){
		this.lastMod = lastMod;
	}
	
	public void setCrc32(String crc32){
		this.crc32 = crc32;
	}
	
	public void setMd5(String md5){
		this.md5 = md5;
	}
	
	public void setSha1(String sha1){
		this.sha1 = sha1;
	}
	
	public void setSize(String size){
		this.size = size;
	}
	
	// getters;
	
	public String getPath(){
		return this.path;
	}
	
	public String getDir(){
		return this.dir;
	}
	
	public Long getLast_mod(){
		return this.lastMod;
	}
	
	public String getCrc32(){
		return this.crc32;
	}
	
	public String getMd5(){
		return this.md5;
	}
	
	public String getSha1(){
		return this.sha1;
	}
	
	public String getSize(){
		return this.size;
	}
}
