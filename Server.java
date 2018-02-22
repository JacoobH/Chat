package Chat;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.net.*;
import java.util.*;
public class Server extends JFrame implements Runnable{

	public static final int PORT=6060;
	ServerSocket server;
	Vector<Connection> connection=new Vector<>();
	
	JList<String> list=new JList<>();
	DefaultListModel<String> listModel=new DefaultListModel<>();
	JButton send=new JButton("发送");
	JTextField msg=new JTextField(20);
	JPanel panel=new JPanel();
	Server(){
		init();
		serverListen();
	}
	void init() {
		setTitle("服务器");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100,100,400,300);
		list.setModel(listModel);
		add(new JScrollPane(list),BorderLayout.CENTER);
		panel.add(msg);
		panel.add(send);
		add(panel,BorderLayout.SOUTH);
		send.addActionListener((e)->{
			if(msg.getText()!=null) {
				display(msg.getText());
			}
		});
		setVisible(true);
	}
	void display(String msg) {
		SwingUtilities.invokeLater(()->{
			listModel.addElement(msg);
		});
		broadCast(msg);
	}
	//广播
	void broadCast(String msg){
		try {
			for(Connection c:connection) {
				c.sendMsg(msg);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	//服务器监听
	void serverListen() {
		try {
			server=new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		display("————开始监听————\n监听端口："+PORT);
		//开始线程
		new Thread(this).start();
	}
	public void run() {
		try {
			while(true) {
				Socket client=server.accept();
				display("一位用户连接");
				connection.add(new Connection(client,this));
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->{
			new Server();
		});

	}

}




class Connection extends Thread{
	protected Socket client;
	protected BufferedReader in;
	protected PrintWriter out;
	Server server;
	Connection(Socket client,Server server){
		this.client=client;
		this.server=server;
		try {
			in=new BufferedReader(new InputStreamReader(client.getInputStream()));
			out=new PrintWriter(client.getOutputStream());
		}catch(IOException e) {
			e.printStackTrace();
		}
		this.start();
	}
	//接收信息
	String getMsg() throws IOException{
		try {

			String msg=in.readLine();
			return msg;

		}catch(IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	//发送信息
	void sendMsg(String msg) throws IOException {
		out.println(msg);
		out.flush();
	}
	public void run() {
		try {
			while(true) {
				String msg=getMsg();
				server.display(msg);
				if(msg==null)	break;
			}
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
