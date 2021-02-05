import static org.junit.Assert.*;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.Test;


public class TestCases {

	@Test
	public void testAES() throws Exception{
		AES aes = new AES();
		SecretKey secret = aes.generateSecret(128);
		IvParameterSpec initializationVector = aes.generateIv();
		String cipherText = aes.encryp("apple", secret, initializationVector);
		String plaintext = aes.decrypt(cipherText, secret, initializationVector);
		//System.out.println(cipherText);
		
		IvParameterSpec initializationVector1 = aes.generateIv();
		String cipherText1 = aes.encryp("https://www.google.com/", secret, initializationVector1);
		String plaintext1 = aes.decrypt(cipherText1, secret, initializationVector1);
		//System.out.println(cipherText1);
		
		assertEquals(plaintext, "apple");
		assertEquals(plaintext1, "https://www.google.com/");
		
	}
	
	
	@Test
	public void testStoreGetDataFromBlockchain() throws Exception{
		User user = new User("");
		
		//encrypt data
		AES aes = new AES();
		SecretKey secret_violatedRules= aes.generateSecret(128);
		IvParameterSpec initializationVector1 = aes.generateIv();
		String cipher_violatedRules = aes.encryp("Here are voilated rules.", secret_violatedRules,
				initializationVector1);
		
		
		SecretKey secret_evidence= aes.generateSecret(128);
		IvParameterSpec initializationVector2 = aes.generateIv();
		String cipher_evidence = aes.encryp("https://www.google.com/", secret_evidence, initializationVector2);
		
		user.connectToRemoteNode("http://172.16.145.164:8543");
		user.loadCredential("password", "/Users/js4488/keystore/UTC--2018-12-03T18-59-35.267788741Z--059bb1d4b9fbca8145be2c6d3d5f3b062a85badf");
		user.loadContract("0x8c3647b6788489Fadae75CE789E62E4F5816e3a8");
		
		user.storeEventDataToBlockchain("01", "95.5", cipher_violatedRules, cipher_evidence, "02" +
				"/01/2020 09:00AM");
		assertEquals(user.getViolatedRulesCipherFromBlockchain("01"),cipher_violatedRules);
		assertEquals(user.getEvidenceCipherFromBlockchain("01"),cipher_evidence);
		assertEquals(user.getRiskScoreFromBlockchain("01"), "95.5");
	}
}
