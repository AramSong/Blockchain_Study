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


import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;


public class key_test{
	
//	static {
//		try {
//			System.loadLibrary("chilkat");
//		}catch(UnsatisfiedLinkError e) {
//			System.err.println("Native code library failed to load. \n" + e);
//			System.exit(1);
//		}
//	}
	
	public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, IOException {

//		  SecureRandom random = new SecureRandom();
//		  byte bytes[] = new byte[32]; //32byte = 256bit
//		  random.nextBytes(bytes);
//			
//		  StringBuilder sb = new StringBuilder();
//			for (byte b : bytes) {
//				sb.append(String.format("%02x", b));
//			}
	 
			
		  //KeyPairGenerator를 이용하여 Key Pair생성(private key, public key)
		  //EC : KeyS for the Elliptic Curve Algorithm
		  KeyPairGenerator KeyGen = KeyPairGenerator.getInstance("EC");
		  //세부 알고리즘 설정  "secp256k1"
		  /*<secp256k1>
		   * secp256k1 refers to the parameters of 
		   * the elliptic curve used in Bitcoin's public-key cryptography, 
		   * and is defined in Standards for Efficient Cryptography (SEC). 
		   */
		  ECGenParameterSpec spec = new ECGenParameterSpec("secp256k1");
		  //랜덤으로 임의의 키 생성 
		  KeyGen.initialize(spec,new SecureRandom());
		  
		  //get Private key & Public Key
		  KeyPair kp = KeyGen.generateKeyPair();
		  PublicKey pub = kp.getPublic();
		  PrivateKey priv = kp.getPrivate();

		  
		  //private key
		  //ECPrivateKey : The interface to an elliptic curve(EC) private key;
		  ECPrivateKey epvt = (ECPrivateKey)priv;
		  //getS() : returns the private value S;
		  String sepvt = adjustTo64(epvt.getS().toString(16).toUpperCase());
		  
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
		
		  // 개인키, 공개키 출력

//		  System.out.println(sepvt.length());
		  //개인키
		  System.out.println("Private Key : " + sepvt);
		  /*
		   <Public key size>
		   * UnCompressed version : two times the field size (in bytes) + 1. 65 bytes(32*2 + 1).
		   * 두개의 좌표쌍(x,y)는 520비트 길이의 하나의 공개키로 표현.(256비트(x) + 256비트(y) + 8비트(0x04))
		   * 04 + x좌표 + y좌표 
		   * 2자리(04) + 64자리(x) + 64자리(y) = 130자리의 16진수로 표현.
		   */
		  //공개키
		  System.out.println("Public Key : " + bcPub);
//		  System.out.println(bcPub.length());
//		  System.out.println("x size : " + xpoint.length());
//		  System.out.println("y size : " + ypoint.length());		  
		  
		  
		  
		  //Public key(bcPub) to Address
		  //1. SHA256(public key)
		  MessageDigest md = MessageDigest.getInstance("SHA-256");
		  md.update(bcPub.getBytes("utf-8"));
		  //byte배열.
		  byte sha_bytes[]  = new byte[32];	//32byte = 256bit;
		  sha_bytes = md.digest();
		  
		  //byte배열을 16진수(Hex)로 변경
		  StringBuilder sb = new StringBuilder();
			for (byte b : sha_bytes) {
				sb.append(String.format("%02x", b));
			}
		  System.out.println("1.SHA256(PublicKey)              : " + sb.toString().toUpperCase());

		  String str_pub = sb.toString();
		  
		  
//		  MessageDigest sha = MessageDigest.getInstance("SHA-256");
//		  byte[] s1 = sha.digest(bcPub.getBytes("UTF-8"));
//		  StringBuilder str_s1 = new StringBuilder();
//			  for(byte b : s1) {
//				  str_s1.append(String.format("%02x",b));
//			  }
//		  System.out.println("  sha1: " + str_s1.toString().toUpperCase());
//		  

		  //2.RIPEMD(SHA256(public key))
		 byte[] r = str_pub.getBytes();
   		 RIPEMD160Digest d = new RIPEMD160Digest();
		 d.update(r,0,r.length);
		 byte[] o = new byte[d.getDigestSize()];
		 d.doFinal(o, 0);
		 
		 Hex.encode(o);
		 StringBuilder sb2 = new StringBuilder();
			for (byte b : o) {
				sb2.append(String.format("%02x", b));
			}
		  System.out.println("2.RIPEMD(SHA256(Publickey)) ->(Payload)      : " + String.format("%10s", sb2.toString().toUpperCase()));
	
		
		 //3.Add prefix to the data(version data)
		 byte[] r2 = new byte[o.length + 1];
		 //r2[0] = 0;
		 System.arraycopy(o,0,r2,1,o.length);
		 StringBuilder s = new StringBuilder();
		 for(byte b : r2) {
			 s.append(String.format("%02x",b));
		 }
		 System.out.println("3.Add prefix to the data(version + Payload): " +String.format("%10s", s.toString().toUpperCase()));
		  
		 //4.SHA256 - 1 
		  MessageDigest md2 = MessageDigest.getInstance("SHA-256");
		  md2.update((s.toString()).getBytes("utf-8"));
		  //byte배열.
		  byte sha_bytes2[]  = new byte[32];	//32byte = 256bit;
		  sha_bytes2 = md2.digest();
		  
		  //byte배열을 16진수(Hex)로 변경
		  StringBuilder s3 = new StringBuilder();
			for (byte b : sha_bytes2) {
				s3.append(String.format("%02x", b));
			}
		  System.out.println("4.SHA256(3)                      : " + s3.toString().toUpperCase());
		 
		  //5.SHA256-2
		  MessageDigest md3 = MessageDigest.getInstance("SHA-256");
		  md3.update((s3.toString()).getBytes("utf-8"));
		  
		  byte sha_bytes3[] = new byte[32];
		  sha_bytes3 = md3.digest();
		  
		  StringBuilder s4 = new StringBuilder();
		  for(byte b : sha_bytes3) {
			  s4.append(String.format("%02x", b));
		  }
		  System.out.println("5.SHA256(4)                      : " + s4.toString().toUpperCase());
		  
		  //6.First four bytes of 6	  
		  //Four bytes : serve as the error-checking code, or checksum. The checksum is concatenated to the end.
		  System.out.println("6.First four bytes of 6(checksum): " + s4.toString().substring(0,8).toUpperCase());
		  String str6 = s4.toString().substring(0,8).toUpperCase();
		  
		  //7.Adding 7 at the end of 3
		  String str_add = s.toString() + str6;
		  System.out.println("7.Adding 6 at the end of 3       : " + str_add.toUpperCase());
		  //System.out.println(str_add.length());
		  //System.out.println("005F6BFFB13823A79444E427CF73145FE8280D8DDBFDD07DCD".length());
		  
		  //8. Base58 encoding of 7
		  String Address = Base58.encode(str_add).substring(34, 68);
		  System.out.println("8.Base58Check Encoded Payload    : " + Address);
		  //System.out.println(Address.length());
		  
		  //System.out.println("19hYa7VVx7qte7GUpKzqhfWGAwvKTobq88".length());
		  //System.out.println("1K3pg1JFPtW7NvKNA77YCVghZRq2s1LwVF".length());
		  
	}
	
	
	//함수 
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
}