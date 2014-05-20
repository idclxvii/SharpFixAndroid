package tk.idclxvii.sharpfixandroid.databasemodel;


public class ModelFileFilter {

	// data structure:
	private Integer id;
	private Integer accountId;
	private String fileName;
	private String filter; //filter type: fdd or fd
	private String ruleName; // rule name
	
	public enum fields{
		Id, Account, File, Rule, Filter
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
	
	public ModelFileFilter(Integer id, Integer account,String ruleName, String fileName, String filter){
		this.id = id;
		this.accountId = account;
		this.fileName = fileName;
		this.filter = filter;
		this.ruleName = ruleName;
	}
	
	public ModelFileFilter(Integer account,String ruleName, String fileName, String filter){
		this.accountId = account;
		this.fileName = fileName;
		this.filter = filter;
		this.ruleName = ruleName;
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
	
	public void setFilter(String filter){
		this.filter = filter;
	}
	
	public void setRule(String ruleName){
		this.ruleName = ruleName;
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
    
    public String getFilter(){
    	return this.filter;
    }
    
    public String getRule(){
    	return this.ruleName;
    }
}
