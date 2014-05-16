package com.freetzi.idclxvii.sharpfixandroid.databasemodel;


public class ModelMagicNumber {


	// data structure:
	private Integer id;
	private String fileType;
	private String Signature8bytes;
	private String Signature4bytes;
	private String mime; 
	
	public enum fields{
		Id,File_type, Signature_8_bytes, Signature_4_bytes, Mime
	}
	
	public String[] getFields(){
		String[] f = new String[fields.values().length];
		for(int x=0; x < f.length; x++ ){
			f[x] = fields.values()[x].toString();
			
		}
		return f;
	}
	
	// constructors:
	
	public ModelMagicNumber(){
		
	}
	
	public ModelMagicNumber(String fileType){
		this.fileType = fileType;
	}

	public ModelMagicNumber(String fileType, String sig8, String sig4, String mime){
		this.fileType = fileType;
		this.Signature8bytes = sig8;
		this.Signature4bytes = sig4;
		this.mime = mime;
	}
	
	// setters:
	public void setId(Integer id){
		this.id = id;
	}
	
	public void setFile_type(String fileType){
		this.fileType = fileType;
	}
	
	public void setSignature_8_bytes(String signature){
		this.Signature8bytes = signature;
	}
	
	public void setSignature_4_bytes(String signature){
		this.Signature4bytes = signature;
	}
	
	public void setMime(String mime){
		this.mime = mime;
	}
	
	
	
	// getters:
	public Integer getId(){
		return this.id;
	}	public String getFile_type(){
		return this.fileType;
	}
	
	public String getSignature_8_bytes(){
		return this.Signature8bytes;
	}
	
	public String getSignature_4_bytes(){
		return this.Signature4bytes;
	}
	
	public String getMime(){
		return this.mime;
	}
	
	
	
}
