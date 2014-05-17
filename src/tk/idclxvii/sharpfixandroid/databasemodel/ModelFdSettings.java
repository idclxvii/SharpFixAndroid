package tk.idclxvii.sharpfixandroid.databasemodel;


public class ModelFdSettings {

	// data structure:
	
	private Integer settingsId;
	private Integer accountId;
	private String ruleName;
	private String designationPath;
	private String fileType;
	
	public enum fields{
		Id, Account, Rule_name, Designation_path, File_type
	}
	
	public String[] getFields(){
		String[] f = new String[fields.values().length];
		for(int x=0; x < f.length; x++ ){
			f[x] = fields.values()[x].toString();
			
		}
		return f;
	}
	
	// constructors
	
	public ModelFdSettings(){
		
	}
	
	public ModelFdSettings(String ruleName){
		this.ruleName = ruleName;
	}
	
	public ModelFdSettings(Integer id, Integer accountId, String ruleName){
		this.settingsId = id;
		this.accountId = accountId;
		this.ruleName = ruleName;
	}
	
	public ModelFdSettings(Integer id, Integer accountId, String ruleName, String fileType){
		this.settingsId = id;
		this.accountId = accountId;
		this.ruleName = ruleName;
		this.fileType = fileType;
		
	}
	
	public ModelFdSettings(Integer accountId, String ruleName, String designationPath, String fileType){
		this.accountId = accountId;
		this.ruleName = ruleName;
		this.fileType = fileType;
		this.designationPath = designationPath;
	}
	
	public ModelFdSettings(Integer id, Integer accountId, String ruleName, String designationPath, String fileType){
		this.settingsId = id;
		this.accountId = accountId;
		this.ruleName = ruleName;
		this.fileType = fileType;
		this.designationPath = designationPath;
	}
    
	public ModelFdSettings(String ruleName, String fileType){
		this.ruleName = ruleName;
		this.fileType = fileType;
	}
	
	// setters:
	
	public void setId(Integer id){
		this.settingsId = id;
	}
	
	public void setAccount(Integer accountId){
		this.accountId = accountId;
	}
	
	public void setRule_name(String rule){
		this.ruleName = rule;
	}
	
	public void setDesignation_path(String path){
		this.designationPath = path;
	}
	
	public void setFile_type(String fileType){
		this.fileType = fileType;
	}
	
	// getters:
	
	public Integer getId(){
		return this.settingsId;
	}
	
	public Integer getAccount(){
		return this.accountId;
	}
	
	public String getRule_name(){
		return this.ruleName;
	}
	
	public String getDesignation_path(){
		return this.designationPath;
	}
	
	public String getFile_type(){
		return this.fileType;
	}
	
	
}
