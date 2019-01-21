
import java.security.SecureRandom;


public class test {

	public static void main(String arg[]) {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[32];		//256bit
		random.nextBytes(bytes);
		

		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		
		System.out.print("Key : ");
		System.out.println(sb.toString());
	}
}

/* String 객체와 String객체를 더하는 (+)행위는 메모리 할당과 메모리 해제를 발생시켜
 * 더하는 연산이 많아진다면 성능적으로 좋지 않다. 
 * 그래서 나온것이 String Builder
 * StringBuilder는 String과 문자열을 더할 때 새로운 객체를 생성하는 것이 아니라 기존의 데이터에
 * 더하는 방식을 사용하기 때문에 속도가 빠르며 상대적으로 부하가 적다.
 * 긴 문자열을 더하는 상황이 발생할 때 StringBuilder를 쓴다.
 */
/*
 * <StringBuilder StringBuffer>
 * StringBuilder: 변경가능한 문자열이지만, synchronization이 적용되지않음.
 * StringBuffer : thread-safe라는 말에서처럼 변경가능하지만 multiple thread환결에서 안전한 클래스.
 * */