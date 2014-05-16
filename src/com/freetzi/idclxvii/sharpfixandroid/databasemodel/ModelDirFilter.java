package com.freetzi.idclxvii.sharpfixandroid.databasemodel;


public class ModelDirFilter {

	
	// data structure:
	private Integer id;
	private Integer accountId;
	private String dir;
	
	
	public enum fields{
		Id, Account, Dir
	}
	
	public String[] getFields(){
		String[] f = new String[fields.values().length];
		for(int x=0; x < f.length; x++ ){
			f[x] = fields.values()[x].toString();
			
		}
		return f;
	}
	
	// constructors:
    public ModelDirFilter(){
    	
    }
    
    public ModelDirFilter(String dir){
    	this.dir = dir;
    }
    
    public ModelDirFilter(Integer id, Integer account, String dir){
    	this.dir = dir;
    	this.id = id;
    	this.accountId = account;
    }
    
    // setters:
    public void setId(Integer id){
    	this.id = id;
    }
    
    public void setAccount(Integer account){
    	this.accountId = account;
    }
    
    public void setDir(String dir){
    	this.dir = dir;
    }
    
    // getters:
    public Integer getId(){
    	return this.id;
    }
    
    public Integer getAccount(){
    	return this.accountId;
    }
    
    public String getDir(){
    	return this.dir;
    }
    
    
    
}
