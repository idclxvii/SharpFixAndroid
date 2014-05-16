package com.freetzi.idclxvii.sharpfixandroid.databasemodel;


public class ModelSD {
	
	// data structure
	private Integer id = null;
	private String path;
	private Long lastMod;

	public enum fields{
		Id, Path, Last_mod
	}
	
	public String[] getFields(){
		String[] f = new String[fields.values().length];
		for(int x=0; x < f.length; x++ ){
			f[x] = fields.values()[x].toString();
			
		}
		return f;
	}
    
	public ModelSD(){
		
	}
	
    public ModelSD(String path, Long lastMod) {
        this.path = path;
        this.lastMod = lastMod;
    }
 
    public ModelSD(int id, String path, Long lastMod) {
        this.id = id;
        this.path = path;
        this.lastMod = lastMod;
    }
 
    // setters
    public void setId(Integer id) {
        this.id = id;
    }
 
    public void setPath(String path) {
        this.path = path;
    }
 
    public void setLast_mod(Long lastMod) {
        this.lastMod = lastMod;
    }
     
 
    // getters
    public Integer getId() {
        return this.id;
    }
 
    public String getPath() {
        return this.path;
    }
 
    public Long getLast_mod() {
        return this.lastMod;
    }

}
