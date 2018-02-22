package Chat;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.net.*;
public class Client extends JFrame implements Runnable{
	Socket client;
	public static final int PORT=6060;
	BufferedReader in;
	PrintWriter out;
	
	JButton connect=new JButton("连接");
	JList<String> list=new JList<>();
	DefaultListModel<String> listModel=new DefaultListModel<>();
	JPanel panel=new JPanel();
	JTextField msg=new JTextField(20);
	JButton send=new JButton("发送");
	Client(){
		init();
	}
	void init(){
		setTitle("客户端");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(500,100,400,300);
		add(connect,BorderLayout.NORTH);
		list.setModel(listModel);
		add(new JScrollPane(list),BorderLayout.CENTER);
		panel.add(msg);
		panel.add(send);
		add(panel,BorderLayout.SOUTH);
		//连接事件监听
		connect.addActionListener((e)->{
			link();
		});
		//发送信息事件监听
		send.addActionListener((e)->{
			if(msg.getText()!=null) {
				sendMsg(msg.getText());
			}
		});
		setVisible(true);
	}
	void display(String msg) {
		SwingUtilities.invokeLater(()->{
			listModel.addElement(msg);
		});
	}
	void link() {
		try {
			client=new Socket("127.0.0.1",PORT);
		}catch(IOException e) {
			e.printStackTrace();
		}
		display("连接成功");
		try {
			in=new BufferedReader(new InputStreamReader(client.getInputStream()));
			out=new PrintWriter(client.getOutputStream());
		}catch(IOException e) {
			e.printStackTrace();
		}
		new Thread(this).start();
	}
	String getMsg() {
		try {
			String msg=in.readLine();
			return msg;
		}catch(IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	void sendMsg(String msg) {
		out.println(msg);
		out.flush();
	}
	public void run() {
		while(true) {
			String msg=getMsg();
			if(msg!=null) {
				display(msg);
			}
		}
	}
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->{
			new Client();
		});
	}
}