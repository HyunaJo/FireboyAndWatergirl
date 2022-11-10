import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

//Server Message를 수신해서 화면에 표시
public class ListenNetwork extends Thread {
	String ip_addr = "127.0.0.1";
	int port_no;
	int roomId;

	private Socket socket;  // 연결소켓
	private InputStream is;
	private OutputStream os;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	
	public ListenNetwork(String userName,int port_no, int roomId) {
		this.port_no = port_no;
		this.roomId = roomId;
		try {
			socket = new Socket(ip_addr, port_no);
			is = socket.getInputStream();
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// SendMessage("/login " + UserName);
			System.out.println("userName = "+userName+" 선택 서버 = "+roomId+"번");
			ChatMsg obcm = new ChatMsg(userName, roomId, "100", "Hello");
			System.out.println(obcm.getUserName()+", "+obcm.getRoomId()+", "+obcm.getCode()+", "+obcm.getData());
			SendObject(obcm);
		
		
		} catch (NumberFormatException | IOException error) {
			// TODO Auto-generated catch block
			//error.printStackTrace();
			JOptionPane.showMessageDialog(null,"Server Can't connect");
		}
	}
	
	public void run() {
		
		while (true) {
			try {
				Object obcm = null;
				String msg = null;
				ChatMsg cm;
				try {
					obcm = ois.readObject();
					System.out.println("obcm read success");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				if (obcm == null) {
					System.out.println("obcm is null!");
					break;
				}
				if (obcm instanceof ChatMsg) {
					cm = (ChatMsg) obcm;
					msg = String.format("[%s] %s", cm.getUserName(), cm.getData());
					System.out.println(msg);
				} else
					continue;
//				switch (cm.getCode()) {
//				case "200": // chat message
//					AppendText(msg);
//					break;
//				case "300": // Image 첨부
//					AppendText("[" + cm.getId() + "]");
//					AppendImage(cm.img);
//					break;
//				}
			} catch (IOException e) {
				//AppendText("ois.readObject() error");
				try {

					ois.close();
					oos.close();
					socket.close();

					break;
				} catch (Exception ee) {
					break;
				} // catch문 끝
			} // 바깥 catch문끝

		}
	}
	
	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
		}
	}
}