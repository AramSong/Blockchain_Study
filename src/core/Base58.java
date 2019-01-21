package core;

import java.io.UnsupportedEncodingException;
//import java.math.BigInteger;
import java.util.Arrays;

/*
 * 출처
 * https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Base58.java
 */
public class Base58 {
	public static final char[] code_string = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
	private static final char encoded_zero = code_string[0];
	private static final int[] indexed = new int[128];
	
	static {
		//indexed배열의 값을 모두 -1로 초기화.
		Arrays.fill(indexed, -1);
		//0~57까지 code_String을 인덱스화.
		for(int i = 0; i < code_string.length ; i++) {
			indexed[code_string[i]] = i; 
		}
	}
	//Encodes the given version and bytes as a base58 string. A check sum is appended.
	public static String encode(String input) throws UnsupportedEncodingException {
		
		/*<1byte version + data bytes + 4bytes check code(a truncated hash)>
		  1. 주소 배열을 생성.
		  byte[] addressBytes = new byte[1 + payload.length + 4];
		 
		  2.주소 배열의 첫번째 바이트는 버전을 나타냄
		  addressBytes[0] = (byte)version; //비트코인의 경우 0x00
		  
		  3.payload를 인덱스 0부터 payload길이만큼 addressBytes의 인덱스 1부터 payload값을 복사.
		  System.arraycopy(payload,0,addressBytes,1,payload.length);
		  
		  4.payload 다음 4byte는 checksum
		  byte[] checksum = Sha256Hash.hashTwice(addressBytes,0,payload.length + 1);
		  System.arraycopy(checksum, 0, addressBytes,payload.length+1,4);
		  
		  5. 위의 데이터를 Base58.encode
		  Base58.encode(addressBytes);
		 */
		byte[] data = input.getBytes("utf-8");
		
		if(data.length == 0) {
			return null;
		}
		//Count leading zeros. 바이트 배열에서 0인값을 찾는다.
		int zeros = 0;
		while(zeros < data.length && data[zeros] == 0) {
			++zeros;
		}
		
		//Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
		//copyOf(int[] original, int newLength) : array를 padding하거나 truncating 할 때 사용.
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
			/*byte는 맨 앞의 비트를 sign bit로 사용.
			 * 8bit로 표현되는 0xff(255)이하의 수치(0~255)에 한해서 (byte)형으로 
			 * casting해서 집어 넣을 경우, &0xff연산을 해주게되면 32bit 크기 (int)형으로
			 * unsigned byte값을 얻을 수 있다. 
			 */
			int digit = (int)number[i] & 0xFF;
			int tmp = remainder*base + digit;
			number[i] = (byte)(tmp/divisor);
			remainder = tmp % divisor;
		}
		return (byte)remainder;
	}
}
