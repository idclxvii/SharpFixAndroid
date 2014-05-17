package tk.idclxvii.sharpfixandroid.databasemodel;


public class ModelFileFilter {

	// data structure:
	private Integer id;
	private Integer accountId;
	private String fileName;
	
	
	public enum fields{
		Id, Account, File
	}
	
	public String[] getFields(){
		String[] f = new String[fields.values().length];
		for(int x=0; x < f.length; x++ ){
			f[x] = fields.values()[x].toString();
			
		}
		return f;
	}
	
	// constructors:
	public ModelFileFilter(){
		
	}
	
	public ModelFileFilter(Integer id, Integer account, String fileName){
		this.id = id;
		this.accountId = account;
		this.fileName = fileName;
	}
	
	public ModelFileFilter(String fileName){
		this.fileName = fileName;
	}
	
	// setters:
	public void setId(Integer id){
		this.id = id;
	}
	
	public void setAccount(Integer account){
		this.accountId = account;
	}
	
	public void setFile(String fileName){
		this.fileName = fileName;
	}
	
	// getters:
    public Integer getId(){
    	return this.id;
    }
    
    public Integer getAccount(){
    	return this.accountId;
    }
    
    public String getFile(){
    	return this.fileName;
    }
}
