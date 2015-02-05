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
	
	private String email;
	// new switches
	/*
	        "sss_switch INTEGER NOT NULL, sss_hh INTEGER NOT NULL, sss_mm INTEGER NOT NULL, sss_ampm INTEGER NOT NULL, sss_update INTEGER NOT NULL,"+
            "sss_repeat INTEGER NOT NULL,  au_switch INTEGER NOT NULL" +	
	 */
	private Integer serviceSwitch; // 1 = on, 0 = off
	private Integer serviceHour; // 0-24 from hours
	private Integer serviceMin; // 0 - 59 from min
	private Integer serviceAMPM; // 0 = AM, 1 = PM
	private Integer serviceUpdateSwitch;  // 1 = on, 0 = off, database definition
	private Integer serviceRepeat; // 0 - 7, Sunday = 0, Mon = 1 and so on ... 7 = everyday
	private Integer auSwitch;  // 1 = on, 0 = off
	private Integer serviceNoti;
	
	
	public enum fields{
		Id, Account, Fdd_switch, Fd_switch,  Fdd_pref, Auto_login, Fdd_Filter_switch, Fd_Filter_switch,
		Sss_switch, Sss_hh, Sss_mm, Sss_ampm, Sss_update, Sss_repeat, Sss_noti, Au_switch, Email
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
			Integer fddFilterSwitch, Integer fdFilterSwitch, Integer srvcSw, Integer srvcHH, Integer srvcMM,
			Integer srvcAMPM, Integer srvcUSw, Integer srvcRepeat, Integer srvcNoti, Integer auSw){
		this.id = id;
		this.accountId = account;
		this.fddSwitch = fddSw;
		this.fdSwitch = fdSw;
		this.fdFilterSwitch = fdFilterSwitch;
		this.fddFilterSwitch = fddFilterSwitch;
		this.fddPref = fddPref;
		this.autoLogin = autoLogin;
		this.serviceSwitch = srvcSw;
		this.serviceHour = srvcHH;
		this.serviceMin = srvcMM;
		this.serviceAMPM = srvcAMPM;
		this.serviceUpdateSwitch = srvcUSw;
		this.serviceRepeat = srvcRepeat;
		this.auSwitch = auSw;
		this.serviceNoti = srvcNoti;
		
	}
	
	public ModelPreferences(Integer account, Integer fddSw, Integer fdSw, Integer fddPref, Integer autoLogin,
			Integer fddFilterSwitch, Integer fdFilterSwitch, Integer srvcSw, Integer srvcHH, Integer srvcMM,
			Integer srvcAMPM, Integer srvcUSw, Integer srvcRepeat, Integer srvcNoti, Integer auSw){
		this.accountId = account;
		this.fddSwitch = fddSw;
		this.fdSwitch = fdSw;
		this.fdFilterSwitch = fdFilterSwitch;
		this.fddFilterSwitch = fddFilterSwitch;
		this.fddPref = fddPref;
		this.autoLogin = autoLogin;
		this.serviceSwitch = srvcSw;
		this.serviceHour = srvcHH;
		this.serviceMin = srvcMM;
		this.serviceAMPM = srvcAMPM;
		this.serviceUpdateSwitch = srvcUSw;
		this.serviceRepeat = srvcRepeat;
		this.serviceNoti = srvcNoti;
		this.auSwitch = auSw;
		
	}
	
	public ModelPreferences(Integer account, Integer fddSw, Integer fdSw, Integer fddPref, Integer autoLogin,
			Integer fddFilterSwitch, Integer fdFilterSwitch, Integer srvcSw, Integer srvcHH, Integer srvcMM,
			Integer srvcAMPM, Integer srvcUSw, Integer srvcRepeat, Integer srvcNoti, Integer auSw, String email){
		this.accountId = account;
		this.fddSwitch = fddSw;
		this.fdSwitch = fdSw;
		this.fdFilterSwitch = fdFilterSwitch;
		this.fddFilterSwitch = fddFilterSwitch;
		this.fddPref = fddPref;
		this.autoLogin = autoLogin;
		this.serviceSwitch = srvcSw;
		this.serviceHour = srvcHH;
		this.serviceMin = srvcMM;
		this.serviceAMPM = srvcAMPM;
		this.serviceUpdateSwitch = srvcUSw;
		this.serviceRepeat = srvcRepeat;
		this.serviceNoti = srvcNoti;
		this.auSwitch = auSw;
		this.email = email;
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
	
	// Sss_switch, Sss_hh, Sss_mm, Sss_ampm, Sss_update, Sss_repeat, Au_switch
	
	public void setSss_switch(Integer sw){
		this.serviceSwitch = sw;
	}
	
	public void setSss_hh(Integer hh){
		this.serviceHour = hh;
	}
	
	public void setSss_mm(Integer mm){
		this.serviceMin = mm;
	}
	
	public void setSss_ampm(Integer ampm){
		this.serviceAMPM = ampm;
	}
	
	public void setSss_update(Integer sw){
		this.serviceUpdateSwitch = sw;
	}
	
	public void setSss_repeat(Integer repeat){
		this.serviceRepeat = repeat;
	}
	
	public void setSss_noti(Integer noti){
		this.serviceNoti = noti;
	}
	
	public void setAu_switch(Integer sw){
		this.auSwitch = sw;
	}
	
	public void setEmail(String e){
		this.email = e;
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
	
	// new getters
	
	// Sss_switch, Sss_hh, Sss_mm, Sss_ampm, Sss_update, Sss_repeat, Au_switch
	
	public Integer getSss_switch(){
		return this.serviceSwitch;
	}
	
	public Integer getSss_hh(){
		return this.serviceHour;
	}
	
	public Integer getSss_mm(){
		return this.serviceMin;
	}
	
	public Integer getSss_ampm(){
		return this.serviceAMPM;
	}
	
	public Integer getSss_update(){
		return this.serviceUpdateSwitch;
	}
	
	public Integer getSss_repeat(){
		return this.serviceRepeat;
	}
	
	public Integer getSss_noti(){
		return this.serviceNoti;
	}
	
	public Integer getAu_switch(){
		return this.auSwitch;
	}
	
	public String getEmail(){
		return this.email;
	}
	
}
