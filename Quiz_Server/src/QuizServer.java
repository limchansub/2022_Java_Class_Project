import java.net.*;
import java.io.* ;
public class QuizServer {

	public static void main(String[] args) throws IOException{
		
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(5555);
		}catch(IOException e) {
			System.err.print("다음 포트번호를 연결할수 없습니다: 5555");
			System.exit(1);
		}
		
		Socket clientSocket =null;
		try {
			clientSocket =  serverSocket.accept();
		}catch(IOException e) {
			System.err.print("accept() 실패");
			System.exit(1);
		}
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String inputLine, outputLine;
		
	}
}
