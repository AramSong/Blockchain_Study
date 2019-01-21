package wallet;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Wallet {

	List<String> privateKeyList_ = new ArrayList<String>();
	
	public Wallet(List<String> privateKeyList) {
		// TODO Auto-generated constructor stub
		
		privateKeyList_ = privateKeyList;
	}

	public Wallet() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getprivateKeyList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void Create(String string) {
		// TODO 지갑 텍스트 파일 생성
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(string);
			for(int i = 0 ; i < privateKeyList_.size(); i++) {
				pw.println(privateKeyList_.get(i));
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	public void Load(String string) throws IOException{
		// TODO 지갑 텍스트 파일 읽어오기
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(string));
			//int i = 1; 
			while(true) {
				String line = br.readLine();
				if(line == null) break;
				//System.out.println(i + ":" + line);
				//i++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
