package crypto;


import java.util.List;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.util.Arrays;
import core.Base58;



public class KeyGenerator  {
	
	//KeyPair 생성 (Private Key, Public Key)
	public static KeyPair Key_Generator() {
		KeyPairGenerator KeyGen = null;
		try {
			KeyGen = KeyPairGenerator.getInstance("EC");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ECGenParameterSpec spec = new ECGenParameterSpec("secp256k1");
		
		try {
			KeyGen.initialize(spec,new SecureRandom());
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get Private key & Public Key
		KeyPair kp = KeyGen.generateKeyPair(); 
		
		return kp;		
	}
	
	

	public static String getPrivateKey(KeyPair kp) {
		//Private key 생성
		
		PrivateKey priv = kp.getPrivate();
		//ECPrivateKey : The interface to an elliptic curve(EC) private key;
		ECPrivateKey epvt = (ECPrivateKey)priv;
		//getS() : returns the private value S;
		String sepvt = adjustTo64(epvt.getS().toString(16).toUpperCase());
	    return sepvt;
	}
	
	public static String getPublicKey(KeyPair kp) {
		//Public key 생성. 
		PublicKey pub = kp.getPublic();
		//public key
		ECPublicKey epub =(ECPublicKey)pub;
		//Returns the Elliptic Curve public point.
		ECPoint pt = epub.getW();
				  
		//이 점의 x좌표와 y좌표는 Public key를 구성함.
		String xpoint = adjustTo64(pt.getAffineX().toString(16)).toUpperCase();
		String ypoint = adjustTo64(pt.getAffineY().toString(16)).toUpperCase();
		//concatenated together with "04" at the beginning to represent the public key.
		//Uncompressed: 0x04|x|y
		//compressed : 02 or 03
		String bcPub = "04" + xpoint + ypoint;
		
		return bcPub;
	}
	


	public static String getAddress(String publickey) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		  
		  //Public key(bcPub) to Address
		  //1. SHA256(public key)
		  MessageDigest md = MessageDigest.getInstance("SHA-256");
		  md.update(publickey.getBytes("utf-8"));
		  //byte배열.
		  byte sha_bytes[]  = new byte[32];	//32byte = 256bit;
		  sha_bytes = md.digest();
		  
		  //byte배열을 16진수(Hex)로 변경
		  StringBuilder sb = build_String(sha_bytes);
		  String str_pub = sb.toString();	
		
		  //2.RIPEMD(SHA256(public key))
		  byte[] r = str_pub.getBytes();
	   	  RIPEMD160Digest d = new RIPEMD160Digest();
		  d.update(r,0,r.length);
	      byte[] o = new byte[d.getDigestSize()];
		  d.doFinal(o, 0);
			 
		  Hex.encode(o);
		  StringBuilder sb2 = build_String(o);
		  
		  //3.Add prefix to the data(version data)
		  byte[] r2 = new byte[o.length + 1];
		  System.arraycopy(o,0,r2,1,o.length);
		  StringBuilder s = build_String(r2);
		  
		  //4.SHA256 - 1 
		  MessageDigest md2 = MessageDigest.getInstance("SHA-256");
		  md2.update((s.toString()).getBytes("utf-8"));
		  //byte배열.
		  byte sha_bytes2[]  = new byte[32];	//32byte = 256bit;
		  sha_bytes2 = md2.digest();
		  
		  //byte배열을 16진수(Hex)로 변경
		  StringBuilder s3 = build_String(sha_bytes2);
		  
	      //5.SHA256-2
		  MessageDigest md3 = MessageDigest.getInstance("SHA-256");
		  md3.update((s3.toString()).getBytes("utf-8"));
			  
		  byte sha_bytes3[] = new byte[32];
		  sha_bytes3 = md3.digest();
			  
		  StringBuilder s4 = build_String(sha_bytes3);
		  //6.First four bytes of 6	  
		  //Four bytes : serve as the error-checking code, or checksum. The checksum is concatenated to the end.
		  String str6 = s4.toString().substring(0,8).toUpperCase();
		  
		  //7.Adding 7 at the end of 3
		  String str_add = s.toString() + str6;
		  
		  //8. Base58 encoding of 7
		  String Address = Base58.encode(str_add).substring(34, 68);
		
		  return Address;
	}
	
	// 생성함수
	private static String adjustTo64(String string) {
		//pads the hex string with leading 0s so the total length is 64 characters.
		switch(string.length()) {
		case 62: return "00" + string;
		case 63: return "0" + string;
		case 64: return string;
		default : 
			throw new IllegalArgumentException("not a valid key : " + string);
		}
		
	}
	
	private static StringBuilder build_String(byte[] byte_array) {
		  StringBuilder str_builder = new StringBuilder();
		  for(byte b : byte_array) {
				  str_builder.append(String.format("%02x", b));
		  }
		return str_builder;
	}

	
}
