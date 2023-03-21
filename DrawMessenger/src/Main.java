import java.io.IOException;
import java.sql.*;
import java.util.Iterator;

public class Main {
	static Messenger m[] = new Messenger[50];	
	static Connection con = DBConnection();
	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
		Statement stmt = con.createStatement();
		String checkingStr = "SELECT name,port FROM userTable;";
		String name;
		int port;
		ResultSet result = stmt.executeQuery(checkingStr);
		int index =0;
		while(result.next()) {
			name = result.getString("name");
			port = result.getInt("port");
			
			
			m[index++] = new Messenger(name,port);	
			
		}
		
		
			Thread thread1 = new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	
			    	m[0].process();
			    }
			});
			Thread thread2 = new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	
			    	m[1].process();
			    }
			});
			Thread thread3 = new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	
			    	m[2].process();
			    }
			});
			Thread thread4 = new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	
			    	m[3].process();
			    }
			});
			thread1.start();
			thread2.start();
			thread3.start();
			thread4.start();
		
		
		
		
		
	}
	public static Connection DBConnection() {
		String url = "jdbc:mariadb://localhost:3308/chatDB";
		String id ="root";
		String password = "1124";
		Connection con =null;
		
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			System.out.println("적재 성공");
			con = DriverManager.getConnection(url,id,password);
			System.out.println("데베 연결 성공");
		}catch(ClassNotFoundException e) {
			System.out.println("드라이브 찾을수 없음");
		}catch(SQLException e) {
			System.out.println("연결 실패");
		}
		
		return con;
	}
	
	
	

}
