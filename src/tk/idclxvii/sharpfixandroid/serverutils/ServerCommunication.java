package tk.idclxvii.sharpfixandroid.serverutils;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.*;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import android.util.Log;

public class ServerCommunication {

	private  final  String USER_AGENT = "ERROR:c0d3s1x";
	
	
	private KeyGenerator keyGen;
	private SecretKey AES_KEY;
	private String uuid;
	
	public ServerCommunication(){
		try{
			this.uuid = UUID.randomUUID().toString();
			this.AES_KEY = this.initializeAESKey();
		}catch(NoSuchAlgorithmException nsae){
			
		}
		
		
	}
	
	public byte[] getEmail()throws Exception{
		/*
		AESDecrypt(httpPost("http://idclxvii.tk/sharpfixandroid/key authentication/getEmail.php",
				"uuid="+ this.uuid + "&key="+RSAEncrypt(byteArrayToHexString(this.AES_KEY.getEncoded()))));
		*/
		return AESDecrypt(httpPost("http://idclxvii.tk/sharpfixandroid/key authentication/getEmail.php",
				"uuid="+ this.uuid + "&key="+RSAEncrypt(byteArrayToHexString(this.AES_KEY.getEncoded()))));
	}
	
	private SecretKey initializeAESKey()throws NoSuchAlgorithmException{
		this.keyGen = KeyGenerator.getInstance("AES");
		this.keyGen.init(128);
		return keyGen.generateKey();
	}
	
	public String AESEncrypt(final String plaintext) throws GeneralSecurityException {
	    //SecretKeySpec sks = new SecretKeySpec(KEY, "AES");
	    Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, this.AES_KEY, cipher.getParameters());
	    byte[] encrypted =  cipher.doFinal(plaintext.getBytes());
	    //encrypted = Base64.encode(encrypted);
	    
	    return byteArrayToHexString(encrypted);
	   //return new String(encrypted);
	}
	
	public byte[] AESDecrypt(final String ciphertext) throws GeneralSecurityException {
		
		
	    //SecretKeySpec sks = new SecretKeySpec(KEY, "AES");
	    Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.DECRYPT_MODE, this.AES_KEY, cipher.getParameters());
	    byte[] decrypted =  cipher.doFinal(hexStringToByteArray(ciphertext));
	    //encrypted = Base64.encode(encrypted);
	    
	    return decrypted; //String(decrypted);//byteArrayToHexString(encrypted);
	   //return new String(encrypted);
	}
	
	public String RSAEncrypt( String data ) throws Exception{
 
 
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");
		
		
		 byte[] keyBytes =  hexStringToByteArray(acquirePublicKey());// Base64.decode( this.publicKeyBase64Encoded);
		 Log.e("ServerComm", "Picked up server public key:" + new String(keyBytes) );
		 PublicKey publickey  = strToPublicKey(new String(keyBytes));
		 //System.out.println(publickey);
		 cipher.init( Cipher.ENCRYPT_MODE , publickey );
		 
	     // Base 64 encode the encrypted data 
	     byte[] encryptedBytes =  cipher.doFinal(data.getBytes())/* cipher.doFinal(data.getBytes())*/;
	    //  encryptedBytes = Base64.encode(encryptedBytes);
	     
	     return byteArrayToHexString(encryptedBytes);
         //return new String(encryptedBytes);
 
 
	}
 
 
	public static PublicKey strToPublicKey(String s){
 
		PublicKey pbKey = null;
        try {
 
        	BufferedReader br   = new BufferedReader( new StringReader(s) );
            PEMParser pr        = new PEMParser(br);
            Object obj = pr.readObject(); 
            //PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            KeyPair kp;
            if (obj instanceof PEMEncryptedKeyPair) {
          //     System.out.println("Encrypted key - we will use provided password");
               //kp = converter.getKeyPair(((PEMEncryptedKeyPair) obj).decryptKeyPair(decProv));
           } else {
            //   System.out.println("Unencrypted key - no password needed");
              // kp = converter.getKeyPair((PEMKeyPair) object);
               pbKey = converter.getPublicKey((SubjectPublicKeyInfo)obj);
           }
            /*
            if( obj instanceof PublicKey )
            {
            	pbKey = (PublicKey) pr.readPemObject();
            }
            else if( obj instanceof KeyPair ) 
            {
            	KeyPair kp = (KeyPair) pr.readObject();
            	pbKey = kp.getPublic();
            }
            */
            pr.close();
 
        }
        catch( Exception e )
        {
        	System.out.println("CIPHER" + e.getMessage() );
        }
 
        return pbKey;
    }
	
	
	private String acquirePublicKey() throws Exception {
		
		
		String url = "http://idclxvii.tk/sharpfixandroid/key authentication/getPublicKey.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		String urlParameters = "uuid="+ this.uuid; //"sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		/*
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
		 */
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		// System.out.println(response.toString());
		
		return response.toString();
		
		
 
	}
	
	public byte[] hexStringToByteArray(String s) {
	    byte[] b = new byte[s.length() / 2];
	    for (int i = 0; i < b.length; i++) {
	        int index = i * 2;
	        int v = Integer.parseInt(s.substring(index, index + 2), 16);
	        b[i] = (byte) v;
	    }
	    return b;
	}

	public String byteArrayToHexString(byte[] b) {
	    StringBuilder sb = new StringBuilder(b.length * 2);
	    for (int i = 0; i < b.length; i++) {
	        int v = b[i] & 0xff;
	        if (v < 16) {
	            sb.append('0');
	        }
	        sb.append(Integer.toHexString(v));
	    }
	    return sb.toString().toUpperCase(Locale.getDefault());
	}
	
	private String httpPost(String url, String urlParams) throws Exception {
		
		
		// String url = "http://idclxvii.tk/sharpfixandroid/test2/test.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		//String urlParameters = ""; //"sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParams);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		/*
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParams);
		System.out.println("Response Code : " + responseCode);
		 */
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		// System.out.println(response.toString());
		
		return response.toString();
		
		
 
	}
	/*
	public String upload(String selectedPath) throws IOException {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;

        String pathToOurFile = selectedPath;
        String urlServer = "http://idclxvii.tk/sharpfixandroid/upload.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(
                    pathToOurFile));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            
            
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream
                    .writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                            + pathToOurFile + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);
           
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            
            
            BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	 
			//print result
			// System.out.println(response.toString());
			
			return response.toString();
        } catch (Exception ex) {
        	return "NO RESPONSE CODE";
        }
	}
	*/
	
}

