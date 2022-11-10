import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class GameRoom extends Thread{
	@SuppressWarnings("unchecked")

	private ServerSocket socket;
	private int roomId; // 게임방 id
	private Vector UserVec; // 참여자 리스트
	private String ownerName; // 방장
	
	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	public GameRoom(ServerSocket socket, int roomId) {
		this.socket = socket;
		this.roomId = roomId;
		UserVec = new Vector();
		System.out.println("["+roomId+"] UserVec.size()="+UserVec.size());
	}
	
	public int getRoomId() {
		return roomId;
	}
	
	public ServerSocket getSocket() {
		return this.socket;
	}
	
	public Vector getUserVec() {
		return this.UserVec;
	}
	
	public boolean enterRoom() {
		System.out.println("enterRoom");
		if(UserVec.size()==2) {
			System.out.println("[방 "+roomId+"] 2명이 이미 참가한 방입니다.");
			return false;
		}
		else {
			System.out.println("[방 "+roomId+"] 방을 참가합니다.");
			return true;
		}
	}
	
	
}
