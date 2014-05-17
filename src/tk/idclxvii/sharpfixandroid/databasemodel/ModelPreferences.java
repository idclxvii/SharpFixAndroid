package tk.idclxvii.sharpfixandroid.databasemodel;


public class ModelPreferences {

	
	//data structure:
	private Integer id;
	private Integer accountId;
	private Integer fddSwitch;
	private Integer fdSwitch;
	private Integer fddPref; // preference if older or newer file will be deleted in case of file duplication : 1 = newer, 0  = older
	private Integer autoLogin;
	private Integer fddFilterSwitch;
	private Integer fdFilterSwitch;
	
	public enum fields{
		Id, Account, Fdd_switch, Fd_switch,  Fdd_pref, Auto_login, Fdd_Filter_switch, Fd_Filter_switch
	}
	
	public String[] getFields(){
		String[] f = new String[fields.values().length];
		for(int x=0; x < f.length; x++ ){
			f[x] = fields.values()[x].toString();
			
		}
		return f;
	}
	
	// constructors:
	public ModelPreferences(){
		
	}
	
	public ModelPreferences(Integer id, Integer account){
		this.id = id;
		this.accountId = account;
	}
	
	public ModelPreferences(Integer account){
		this.accountId = account;
	}
	
	public ModelPreferences(Integer id, Integer account, Integer fddSw, Integer fdSw, Integer fddPref, Integer autoLogin,
			Integer fddFilterSwitch, Integer fdFilterSwitch){
		this.id = id;
		this.accountId = account;
		this.fddSwitch = fddSw;
		this.fdSwitch = fdSw;
		this.fdFilterSwitch = fdFilterSwitch;
		this.fddFilterSwitch = fddFilterSwitch;
		this.fddPref = fddPref;
		this.autoLogin = autoLogin;
	}
	
	public ModelPreferences(Integer account, Integer fddSw, Integer fdSw, Integer fddPref, Integer autoLogin,
			Integer fddFilterSwitch, Integer fdFilterSwitch){
		this.accountId = account;
		this.fddSwitch = fddSw;
		this.fdSwitch = fdSw;
		this.fdFilterSwitch = fdFilterSwitch;
		this.fddFilterSwitch = fddFilterSwitch;
		this.fddPref = fddPref;
		this.autoLogin = autoLogin;
	}
	
	// setters:
	public void setId(Integer id){
		this.id = id;
	}
	
	public void setAccount(Integer id){
		this.accountId = id;
	}
	
	public void setFdd_switch(Integer sw){
		this.fddSwitch = sw;
	}
    
	public void setFd_switch(Integer sw){
		this.fdSwitch = sw;
	}
	
	public void setFdd_Filter_switch(Integer sw){
		this.fddFilterSwitch = sw;
	}
	
	public void setFd_Filter_switch(Integer sw){
		this.fdFilterSwitch = sw;
	}
	
	public void setFdd_pref(Integer pref){
		this.fddPref = pref;
	}
	
	public void setAuto_login(Integer sw){
		this.autoLogin = sw;
	}
	// getters:
	public Integer getId(){
		return this.id;
	}
	
	public Integer getAccount(){
		return this.accountId;
	}
	
	public Integer getFdd_switch(){
		return this.fddSwitch;
	}
	
	public Integer getFd_switch(){
		return this.fdSwitch;
	}
	
	public Integer getFd_Filter_switch(){
		return this.fdFilterSwitch;
	}
	
	public Integer getFdd_Filter_switch(){
		return this.fddFilterSwitch;
	}
	
	
	
	public Integer getFdd_pref(){
		return this.fddPref;
	}
	
	public Integer getAuto_login(){
		return this.autoLogin;
	}
}
