import java.io.*;
import java.net.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


import java.sql.*;

public class Messenger extends Thread {

	
	protected JTextField tf;
	protected JTextArea chat;
	protected JPanel loginPanel;
	DatagramSocket socket;
	DatagramPacket packet;
	InetAddress address =null;
	final int myPort;
	int otherPort;
	protected String senderName;
	protected String userName;
	Connection con = DBConnection();
	public Messenger(String name,int port) throws IOException{
		userName = name;
		myPort = port;
		
		MainFrame m = new MainFrame();
		m.showMF1();
		address = InetAddress.getByName("192.168.56.1");
		socket = new DatagramSocket(myPort);
	}
	
	public void process() {
		
		Statement stmt;
		int chatId =0;
		String s;
		while(true) {
			try {
				byte[] buf =new byte[256];
				packet = new DatagramPacket(buf,buf.length);
				socket.receive(packet); 
				
				stmt = con.createStatement();
				String checkingStr = "SELECT sender,id FROM userchatTable WHERE receiver='" +senderName  + "';";
				ResultSet result = stmt.executeQuery(checkingStr);
				boolean flag = false;
				while(result.next()) {
					if(userName.equals(result.getString("sender"))) {
						flag = true;
						chatId = result.getInt("id");
					}
				}
				if(!flag) {
					stmt.executeUpdate("insert into userchatTable(sender,receiver) values ('"+userName+"','"+senderName+"');");
					result = stmt.executeQuery("SELECT id FROM userchatTable WHERE receiver='" +senderName  + "' and  sender = '"+userName+"';");
					result.next();
					chatId = result.getInt("id");
				}
				s =new String(buf);
				
				stmt.executeUpdate("insert into chatTable(id,chat) values ('"+chatId+"','"+senderName+":"+s+"\n"+"');");
				
				chat.append(senderName+":"+s + "\n");
				System.out.println("받기 성공");
			}
			catch(IOException ioException){
				System.out.println("받기 실패");
				ioException.printStackTrace();
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
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
	class MainFrame{
		public FriendFrame friendF;
		
		public MainFrame() {
			friendF = new FriendFrame();
			
		}
		public void showMF1() {
			friendF.setVisible(true);
		}
		//친구목록 프레임
		class FriendFrame extends JFrame implements ActionListener {
			public AddFriends dialog;
			JPanel p1 = new JPanel();//채팅목록 친구목록 버튼 
			JPanel p2 = new JPanel();//상단 화면
			JPanel p3 = new JPanel();
			JPanel p4 = new JPanel();
			JPanel p5 = new JPanel();
			JTextArea ta1 = new JTextArea();
			JButton b1 = new JButton("친구목록");
			JButton b2 = new JButton("채팅목록");
			JButton b3 = new JButton("+"); //친구추가
			JLabel l1 = new JLabel("친구추가");
			JLabel l2 = new JLabel("친구목록");
			
			public FriendFrame(){
				setTitle(userName);
				dialog = new AddFriends(friendF, "친구추가");
				Color cyan = new Color(0,255,255);
				Color c = new Color(200,255,255);
				Color c1 = new Color(200,250,255);
				
				Statement stmt;
				
				try {
					stmt = con.createStatement();
					String checkingStr = "SELECT friendID FROM friendTable where name='"+userName+"';";
					ResultSet result = stmt.executeQuery(checkingStr);
					ResultSet result2;
					boolean flag = false;
					while(result.next()) {
						int friendId = result.getInt("friendID");
						checkingStr = "SELECT name,status,port FROM userTable where id='"+friendId+"';";
						result2 = stmt.executeQuery(checkingStr);
						result2.next();
						String fn =result2.getString("name");
						int fport = result2.getInt("port");
						String fs = result2.getString("status");
						createNewChat(fn,fport);
						createNewFriend(fn,fs);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				b1.setBackground(c);
				b2.setBackground(c);
				b3.setBackground(c);
				p1.setBackground(cyan);
				p2.setBackground(cyan);
				p4.setBackground(c);
				p5.setBackground(c1);
				
				b1.addActionListener(this);
				b2.addActionListener(this);
				b3.addActionListener(this);
				b1.setPreferredSize(new Dimension(158, 30));
				b2.setPreferredSize(new Dimension(158, 30));
				
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setSize(340, 480);
				setLocationRelativeTo(null);
				setResizable(false);
				
				p1.setSize(340,80);
				p1.setLayout(new FlowLayout(FlowLayout.CENTER));
				p1.add(b1);
				p1.add(b2);
				
				p2.setSize(340,80);
				p2.setLayout(new FlowLayout(FlowLayout.RIGHT));
				
				p2.add(l2);
				p2.add(Box.createHorizontalStrut(150));
				p2.add(l1);
				p2.add(b3);
			
				
				p3.setSize(340,480);
				p3.setLayout(new BorderLayout());
				p3.add(p2,BorderLayout.PAGE_START);
				p3.add(p4,BorderLayout.CENTER);
				p3.add(p1,BorderLayout.PAGE_END);
				
				add(p3);
				setVisible(true);
			}

	

			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource() == b1 ){
					l2.setText("친구목록");
					p3.remove(p5);
					p3.add(p4,BorderLayout.CENTER);
					
					this.repaint();
					
				}else if(e.getSource() == b2 ){
					l2.setText("채팅목록");
					p3.remove(p4);
					p3.add(p5,BorderLayout.CENTER);
					this.repaint();
					
					
				}else if(e.getSource() == b3 ){
					dialog.setVisible(true);
				}
				
				
			}
			
			public void createNewFriend(String n,String s) {
				JButton bt = new JButton();
				Color cyan = new Color(0,250,255);
				
				bt.setPreferredSize(new Dimension(340, 40));
				bt.setText(n);
				bt.setBackground(cyan);
				bt.addActionListener(new ActionListener() {

					@Override

					public void actionPerformed(ActionEvent e) {
						Info info = new Info(bt.getText(),s);
					}

					});
				bt.setHorizontalAlignment(SwingConstants.LEFT);
				p4.add(bt);
				
			}
			public void createNewChat(String n, int port) {
				JButton bt = new JButton();
				Color cyan = new Color(0,200,230);
				
				bt.setBackground(cyan);
				bt.setPreferredSize(new Dimension(340, 40));
				bt.setText(n);
				bt.addActionListener(new ActionListener() {

					@Override

					public void actionPerformed(ActionEvent e) {
						Chat chatF  = new Chat(n,port);
					}

					});
				bt.setHorizontalAlignment(SwingConstants.LEFT);
				p5.add(bt);
				
			}
			public void reset() {
				this.revalidate();
			}
			//친구추가창
			class AddFriends extends JDialog implements ActionListener{
				JTextField txt = new JTextField(10);
				JButton b = new JButton("+");
				
				public AddFriends(JFrame frame, String title){
					super(frame,title);
					
					setLocationRelativeTo(null);
					setLayout(new FlowLayout(FlowLayout.CENTER));
					//b.setPreferredSize(new Dimension(40, 40));
					setSize(200,70);
					b.addActionListener(this);
					add(txt);
					add(b);
					
					setVisible(false);
				}

				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					Statement stmt;
					String name = txt.getText();
					try {
						stmt = con.createStatement();
						String checkingStr = "SELECT id,name,port,status FROM userTable;";
						ResultSet result = stmt.executeQuery(checkingStr);
						boolean flag = false;
						while(result.next()) {
							if(name.equals(result.getString("name")) && !(name.equals(userName)) ) {
								flag = true;
								senderName = name;
								int id = result.getInt("id");
								String status = result.getString("status");
								int port = result.getInt("port");
								createNewFriend(name,status);
								createNewChat(name,port);
								stmt.executeUpdate("insert into friendTable(name,friendID) values ('"+userName+"','"+id+"');");
								break;
							}
							else {
								flag =false;
							}
							
						}
						if(!flag) {
							flag = false;
							JDialog addmsg = new JDialog(this,"친구 추가 실패");

							addmsg.add(new Label("존재하지 않는 이름입니다"));
							addmsg.pack();
							addmsg.setLocationRelativeTo(null);
							addmsg.setVisible(true);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						
					}
					reset();
				}
			}
			
		
		}

	}

	
	class Info extends JFrame{
		
		public Info(String n,String s){
			super(n);
			setLocationRelativeTo(null);
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			Color cyan = new Color(0,255,255);
			Color c = new Color(200,255,255);
			
			
			
			setSize(250,80);
			
			JLabel name = new JLabel(n+":");
			JLabel status = new JLabel(s);
			name.setBackground(cyan);
			status.setBackground(c);
			p.add(name,BorderLayout.LINE_START);
			
			p.add(status,BorderLayout.LINE_END);
			
			add(p);
			setVisible(true);
			
		}
		
	}
	//채팅화면
	class Chat extends JFrame implements ActionListener{
		
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
	
		JButton b5 = new JButton("▶"); //메세지 전송
		
		
		public Chat(String name,int Port){
			
			
			otherPort = Port;
			tf = new JTextField(20);
			chat = new JTextArea();
			chat.setEditable(false);
			senderName = name;
			Statement stmt;
			int chatId= 0;
			try {
				stmt = con.createStatement();
				ResultSet result = stmt.executeQuery("SELECT id FROM userchatTable WHERE receiver='" +senderName+ "' and  sender = '"+userName+"';");
				result.next();
				chatId = result.getInt("id");
				result = stmt.executeQuery("SELECT chat FROM chatTable WHERE id='" +chatId+"';");
				while(result.next()) {
					if(result.getString("chat") != null) {
						String s = result.getString("chat");
						chat.append(s);
					}	
				}
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			Color cyan = new Color(0,255,255);
			Color c = new Color(200,255,255);
			Color c1 = new Color(200,250,255);
			b5.setBackground(c);
			p2.setBackground(cyan);
			chat.setBackground(c);
			setTitle(name);
			setSize(340, 480);
			setLocationRelativeTo(null);
			setResizable(false);
			
			
			p1.setLayout(new FlowLayout(FlowLayout.CENTER));
	
			p2.setLayout(new FlowLayout(FlowLayout.CENTER));
			p2.add(tf);
			p2.add(b5);
			
			p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
			p3.add(chat);
			p3.add(p2);
			p3.add(p1);
			
			add(p3);
			
			
			b5.addActionListener(this);
			setVisible(true);
		}
	
		
		public void actionPerformed(ActionEvent evt) {
			String s = tf.getText();
			byte[] buffer = s.getBytes();
			DatagramPacket packet;
			Statement stmt;
			int chatId = 0;
			packet = new DatagramPacket(buffer, buffer.length,address,otherPort);
			try {
				stmt = con.createStatement();
				String checkingStr = "SELECT sender,id FROM userchatTable WHERE receiver='" +senderName  + "';";
				ResultSet result = stmt.executeQuery(checkingStr);
				boolean flag = false;
				while(result.next()) {
					if(userName.equals(result.getString("sender"))) {
						flag = true;
						chatId = result.getInt("id");
					}
				}
				if(!flag) {
					stmt.executeUpdate("insert into userchatTable(sender,receiver) values ('"+userName+"','"+senderName+"');");
					result = stmt.executeQuery("SELECT id FROM userchatTable WHERE receiver='" +senderName  + "' and  sender = '"+userName+"';");
					result.next();
					chatId = result.getInt("id");
				}
				
				
				stmt.executeUpdate("insert into chatTable(id,chat) values ('"+chatId+"','"+userName+":"+s + "\n"+"');");
				chat.append( userName+":"+s + "\n");
				tf.selectAll();
				chat.setCaretPosition(chat.getDocument().getLength());
				
				socket.send(packet);
				
			}catch(IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
}

