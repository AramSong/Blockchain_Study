## 자바 실습스터디 1주차

1. **개인키 생성**

   256비트 길이의 랜덤 생성된 숫자. 보통 64자리의 16진수로 표현.

   * **java security package**

     1. key generate에 사용되는 패키지. key pair을 만드는데 `KeyPairGenerator`가 쓰임.

     2. `getInstance()`르를 통해 인스턴스를  얻을 수 있다. 두 개의 파라미터를 사용하는데 하나는 알고리즘이고, 두번째 파라미터는 provider.

     3. key generator의 인스턴스를 받은 후, `initialize()`메소드를 통해 두개의 인자를 얻는다. key size, source of randomness.

     4. `generateKeyPair()`메소드를 통해 Key pair를 리턴. 이 key pair가 private key와 public key.

        `getPrivate()`와 `getPublic()`을 통해 얻을 수 있음

   * **Class SecureRandom**

     This class provides a cryptographically strong random number generator (RNG).

   * **Class KeyPairGenerator**

     used to generate pairs of public and private keys. Key Pair generators are constructed using the `getInstance()` methods.

| modifier and type         | method and description                                       |
| ------------------------- | ------------------------------------------------------------ |
| `static KeyPairGenerator` | `getInstance(String algorithm, Provider provider)`Returns a KeyPairGenerator object that generates public/private key pairs for the specified algorithm. |

* **Bouncy Castle**

  확장된 기능을 가진 자바암호라이브러리. 타원곡선 암호화 기술에 필요한 기술을 라이브러리화.

  타원 곡선 암호화 기술에 필요한 라이브러리는 구체적으로 `bcprov-jdk15on-160.jar`와 `bcpkix-jdkon-160.jar`이므로 두파일을 다운로드 해야함.

1. 해당 파일 설치

![1547450116724](C:\Users\USER\AppData\Roaming\Typora\typora-user-images\1547450116724.png)

2. 다운로드 한 뒤, 두 파일을 드래그해서 이클립스 내 프로젝트 이름에 드롭.

   ![1547450192432](C:\Users\USER\AppData\Roaming\Typora\typora-user-images\1547450192432.png)

   3. 빌드 패스(Build Path)에 추가.  두 파일 클릭 후 , 오른쪽버튼->Build path -> Add to Build Path

2.**공개키 생성**

- The ECDSA key is represented by a point on an elliptical curve. The X and Y coordinates of this point comprise the public key. They are concatenated together with "04" at the beginning to represent the public key.

- Public key sizes further depend on whether the "uncompressed" representation or the "compressed" representation is used. In the uncompressed form, the public key size is equal to **two times the field size (in bytes) + 1**, in the compressed form it is field size + 1. So if your curve is defined on [`secp256r1` (also called `NIST P-256` or `X9.62 prime256v1`)](https://archive.fo/dIVB7#selection-1915.1527-1915.1569), then the field size is 256 bits or 32 bytes. 

  And therefore the public key would be exactly **65 bytes (32*2 +1) **long in the uncompressed form and 33 bytes (32 +1) long in the compressed form.

- The uncompressed form consists of a 0x04 (in analogy to the [DER OCTET STRING tag](https://archive.fo/gzAu7#selection-2633.0-2641.14)) plus the concatenation of the binary representation of the X coordinate plus the binary representation of the y coordinate of the public point.

3.**Address**

 * SHA256(Public key)

   `java.security.MessageDigest`라이브러리를 사용.

   ```java
   MessageDigest md = MessageDigest.getInstance("SHA-256");
   md.update(msg.getBytes());	//String msg
   return md.digest();
   ```

   > <SHA256>
   >
   > http://needjarvis.tistory.com/251

 * RIPEMD(SHA256(Public Key))

   RACE Integrity Primitives Evaluation Message Digest. MD4(Message Digest4)를 기반으로 개발한 암호화 해시 알고리즘. 임의의 길이의 메시지에서 128비트/160비트 메시지 요약을 생성하며 , 32비트 연산에 최적화.

   1. Message를 입력(사용자 메시지 입력)
   2. 입력받은 메시지를 Original Message로 패딩. (입력받은 메시지를 패딩하여(448bits) 448 mod 512가 되도록 만들며 패딩의 첫비트는 1로 설정하며 나머지는 0으로 채워놓음.  
   3. 메시지 길이 확장 
   4. MD버퍼 초기화 
   5. 블록메시지 처리
   6. 해시값 출력

* Base58 

  * 특징

  ```
  * A set of 58 alpanumeric symbols consisting of easily distinguished uppercase and lowercase letters(헷갈리는 문자들은 제외. (예시) 0Ol|)
  
  * 첫 번째 바이트는 버전/어플리케이션 정보 명시. Bitcoin Address는 첫번째 바이트가 0x00.
  
  * 앞에서 해싱한 SHA256에서 4bytes(32bits)를 error checking code로 씀(checksum).
  ```

  * Encoding

  ```
  *Base58Check encoding is also used for encoding ECDSA private keys in the wallet import format.(same as Bitcoin Address)
  ```

  *  Base58 Symbol chart

  | Value | Character | Value | Character | Value | Character | Value | Character |
  | ----- | --------- | ----- | --------- | ----- | --------- | ----- | --------- |
  | 0     | 1         | 1     | 2         | 2     | 3         | 3     | 4         |
  | 4     | 5         | 5     | 6         | 6     | 7         | 7     | 8         |
  | 8     | 9         | 9     | A         | 10    | B         | 11    | C         |
  | 12    | D         | 13    | E         | 14    | F         | 15    | G         |
  | 16    | H         | 17    | J         | 18    | K         | 19    | L         |
  | 20    | M         | 21    | N         | 22    | P         | 23    | Q         |
  | 24    | R         | 25    | S         | 26    | T         | 27    | U         |
  | 28    | V         | 29    | W         | 30    | X         | 31    | Y         |
  | 32    | Z         | 33    | a         | 34    | b         | 35    | c         |
  | 36    | d         | 37    | e         | 38    | f         | 39    | g         |
  | 40    | h         | 41    | i         | 42    | j         | 43    | k         |
  | 44    | m         | 45    | n         | 46    | o         | 47    | p         |
  | 48    | q         | 49    | r         | 50    | s         | 51    | t         |
  | 52    | u         | 53    | v         | 54    | w         | 55    | x         |
  | 56    | y         | 57    | z         |       |           |       |           |

  * Algorithm ( 1byte_version + hash_or_other_data + 4bytes_check_code)

  ```java
  code_String = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
  x = convert_bytes_to_big_integer(hash_result)
      
  output_String = ""
      while(x  > 0 ){
          (x,remainder) = divide(x,58)
              output_String.append(code_String[remainder])
      }    
  	repeat(number_of_leading_zero_bytes_in_hash)
      {
          output_string.append(code_String[0])
      }
  
  output_String.reverse();
   
  ```


