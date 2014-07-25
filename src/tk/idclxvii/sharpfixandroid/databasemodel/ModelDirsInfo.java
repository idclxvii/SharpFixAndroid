package tk.idclxvii.sharpfixandroid.databasemodel;


public class ModelDirsInfo {
	
	// data structure
	private Integer id = null;
	private String path;
	private Long lastMod;

	public enum fields{
		Sd_id, Path, Last_mod
	}
	
	public String[] getFields(){
		String[] f = new String[fields.values().length];
		for(int x=0; x < f.length; x++ ){
			f[x] = fields.values()[x].toString();
			
		}
		return f;
	}
    
	public ModelDirsInfo(){
		
	}
	
    public ModelDirsInfo(String path) {
        this.path = path;
    }
 
    public ModelDirsInfo(Integer id, String path ) {
        this.id = id;
        this.path = path;
    }
    
    public ModelDirsInfo(Integer id, String path, Long lastMod) {
        this.id = id;
        this.path = path;
        this.lastMod = lastMod;
    }
 
    // setters
    public void setSd_id(Integer id) {
        this.id = id;
    }
 
    public void setPath(String path) {
        this.path = path;
    }
 
    public void setLast_mod(Long lastMod) {
        this.lastMod = lastMod;
    }
     
 
    // getters
    public Integer getSd_id() {
        return this.id;
    }
 
    public String getPath() {
        return this.path;
    }
 
    public Long getLast_mod() {
        return this.lastMod;
    }

}
