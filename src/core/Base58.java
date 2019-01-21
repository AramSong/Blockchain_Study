package core;

import java.io.UnsupportedEncodingException;
//import java.math.BigInteger;
import java.util.Arrays;

/*
 * ��ó
 * https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Base58.java
 */
public class Base58 {
	public static final char[] code_string = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
	private static final char encoded_zero = code_string[0];
	private static final int[] indexed = new int[128];
	
	static {
		//indexed�迭�� ���� ��� -1�� �ʱ�ȭ.
		Arrays.fill(indexed, -1);
		//0~57���� code_String�� �ε���ȭ.
		for(int i = 0; i < code_string.length ; i++) {
			indexed[code_string[i]] = i; 
		}
	}
	//Encodes the given version and bytes as a base58 string. A check sum is appended.
	public static String encode(String input) throws UnsupportedEncodingException {
		
		/*<1byte version + data bytes + 4bytes check code(a truncated hash)>
		  1. �ּ� �迭�� ����.
		  byte[] addressBytes = new byte[1 + payload.length + 4];
		 
		  2.�ּ� �迭�� ù��° ����Ʈ�� ������ ��Ÿ��
		  addressBytes[0] = (byte)version; //��Ʈ������ ��� 0x00
		  
		  3.payload�� �ε��� 0���� payload���̸�ŭ addressBytes�� �ε��� 1���� payload���� ����.
		  System.arraycopy(payload,0,addressBytes,1,payload.length);
		  
		  4.payload ���� 4byte�� checksum
		  byte[] checksum = Sha256Hash.hashTwice(addressBytes,0,payload.length + 1);
		  System.arraycopy(checksum, 0, addressBytes,payload.length+1,4);
		  
		  5. ���� �����͸� Base58.encode
		  Base58.encode(addressBytes);
		 */
		byte[] data = input.getBytes("utf-8");
		
		if(data.length == 0) {
			return null;
		}
		//Count leading zeros. ����Ʈ �迭���� 0�ΰ��� ã�´�.
		int zeros = 0;
		while(zeros < data.length && data[zeros] == 0) {
			++zeros;
		}
		
		//Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
		//copyOf(int[] original, int newLength) : array�� padding�ϰų� truncating �� �� ���.
		data = Arrays.copyOf(data, data.length);	//modify it in-place
		char[] encoded = new char[data.length*2]; //upper bound
		
		int outputStart = encoded.length;
		for(int i = zeros; i < data.length;) {
			encoded[--outputStart] = code_string[divmod(data,i,256,58)];
			if(data[i] == 0) {
				++i; //optimization - skip leading zeros
			}
		}
	
		//Preserve exactly as many leading zeros in output as there were leading zeros in data.
		while(outputStart < encoded.length && encoded[outputStart] == encoded_zero) {
			++outputStart;
		}
		while(--zeros >= 0) {
			encoded[--outputStart] = encoded_zero;
		}
		//Return encoded string (including leading zeros).
		String test = new String(encoded,outputStart,encoded.length-outputStart);
		return new String(encoded,outputStart,encoded.length-outputStart);
	}
	/*
	 * Divides a number, represented as an array of bytes each containing a single digit
	 * in the specified base, by the given divisor. The given number is modified in-place
	 * to contain the quotient, and the return value is the remainder.
	 * 
	 * @param number		: the number to divide
	 * @param firstDigit	: the index within the array of the first non-zero digit
	 * 						  (this is used for optimization by skipping the leading zeros
	 * @param base			: in which the number's digits are represented(up to 256)
	 * @param divisor		: the number to divide by (up to 256)
	 * @return 				: the remainder of the division operation 
	 */
	private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
		// TODO : this is just long division which accounts for the base of the input digits
		int remainder =  0;
		for(int i = firstDigit ; i < number.length; i++) {
			/*byte�� �� ���� ��Ʈ�� sign bit�� ���.
			 * 8bit�� ǥ���Ǵ� 0xff(255)������ ��ġ(0~255)�� ���ؼ� (byte)������ 
			 * casting�ؼ� ���� ���� ���, &0xff������ ���ְԵǸ� 32bit ũ�� (int)������
			 * unsigned byte���� ���� �� �ִ�. 
			 */
			int digit = (int)number[i] & 0xFF;
			int tmp = remainder*base + digit;
			number[i] = (byte)(tmp/divisor);
			remainder = tmp % divisor;
		}
		return (byte)remainder;
	}
}
