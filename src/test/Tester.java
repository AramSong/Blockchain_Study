package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import crypto.KeyGenerator;
import wallet.Wallet;




public class Tester {
		public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		    
			KeyGenerator KeyGen = new KeyGenerator();
			List<String> privateKeyList = new ArrayList<String>();
			List<String> publicKeyList = new ArrayList<String>();
			List<String> AddressList = new ArrayList<String>();
			
			//1. Private key 100�� ���� �� list�� �ֱ�.
			for(int i = 0 ; i < 100 ; i++) {
				KeyPair kp = KeyGenerator.Key_Generator();
				privateKeyList.add(KeyGenerator.getPrivateKey(kp));
				publicKeyList.add(KeyGenerator.getPublicKey(kp));
			}
			//1-1. �ּ� ����
			for(int i = 0 ; i < 100; i++) {
				AddressList.add(KeyGenerator.getAddress(publicKeyList.get(i)));
			}
			
			//2. ������ private key list ����
			Wallet wallet = new Wallet(privateKeyList);
			wallet.Create("test");
			//3. ������ ����� Private Key list ���
			Wallet loadedwallet = new Wallet();
			loadedwallet.Load("test");
			//4.Private Key,  Public key , Address ���
			for(int i = 0; i < 100; i++) {
				//System.out.print("PrivateKey: " + privateKeyList.get(i) + "|");
				System.out.print("PublicKey: " + publicKeyList.get(i) + "|");
				System.out.println("Address: " + AddressList.get(i));
				
			}
			
		}
}
