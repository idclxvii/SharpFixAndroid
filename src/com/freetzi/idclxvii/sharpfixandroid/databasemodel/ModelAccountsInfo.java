package com.freetzi.idclxvii.sharpfixandroid.databasemodel;


public class ModelAccountsInfo {
	
	// data structure
	private Integer id = null;
	private String login;
	private String password;

	public enum fields{
		Id, Login, Password
	}
	

	public String[] getFields(){
		String[] f = new String[fields.values().length];
		for(int x=0; x < f.length; x++ ){
			f[x] = fields.values()[x].toString();
			
		}
		return f;
	}
	
	public ModelAccountsInfo() {
		
	}
	
    public ModelAccountsInfo(String login, String password) {
        this.login = login;
        this.password = password;
    }
 
    public ModelAccountsInfo(Integer id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }
 
    // setters
    public void setId(Integer id) {
        this.id = id;
    }
 
    public void setLogin(String login) {
        this.login = login;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
     
 
    // getters
    public Integer getId() {
        return this.id;
    }
 
    public String getLogin() {
        return this.login;
    }
 
    public String getPassword() {
        return this.password;
    }

}
