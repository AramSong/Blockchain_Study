
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

/* String ��ü�� String��ü�� ���ϴ� (+)������ �޸� �Ҵ�� �޸� ������ �߻�����
 * ���ϴ� ������ �������ٸ� ���������� ���� �ʴ�. 
 * �׷��� ���°��� String Builder
 * StringBuilder�� String�� ���ڿ��� ���� �� ���ο� ��ü�� �����ϴ� ���� �ƴ϶� ������ �����Ϳ�
 * ���ϴ� ����� ����ϱ� ������ �ӵ��� ������ ��������� ���ϰ� ����.
 * �� ���ڿ��� ���ϴ� ��Ȳ�� �߻��� �� StringBuilder�� ����.
 */
/*
 * <StringBuilder StringBuffer>
 * StringBuilder: ���氡���� ���ڿ�������, synchronization�� �����������.
 * StringBuffer : thread-safe��� ������ó�� ���氡�������� multiple threadȯ�ῡ�� ������ Ŭ����.
 * */