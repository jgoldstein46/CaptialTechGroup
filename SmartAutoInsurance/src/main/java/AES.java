/**
 * Reference: https://www.baeldung.com/java-aes-encryption-decryption
 * **/
 
import java.security.*;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

public class AES {
	
	public AES(){};
	
	public SecretKey generateSecret(int n) throws NoSuchAlgorithmException{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(n);
	    SecretKey secretKey = keyGenerator.generateKey();
	    return secretKey;
	}

	
	public String encryp(String plaintext, SecretKey secretKey , IvParameterSpec initializationVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	    cipher.init(Cipher.ENCRYPT_MODE, secretKey, initializationVector);
	    byte[] cipherText = cipher.doFinal(plaintext.getBytes());
	    //Base64.getEncoder().encodeToString(cipherText);
	    return Base64.getEncoder().encodeToString(cipherText);
		
	}
	
	public IvParameterSpec generateIv() {
	    byte[] initializationVector = new byte[16];
	    SecureRandom secureRandom = new SecureRandom(); 
	    secureRandom.nextBytes(initializationVector); 
	    return new IvParameterSpec(initializationVector);
	}
	
	public String decrypt(String cipherText, SecretKey secretKey, IvParameterSpec initializationVector) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");  
		cipher.init(Cipher.DECRYPT_MODE, secretKey, initializationVector);
		byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(cipherText)); 
		return new String(plaintext); 
		
		
	}
	
}

