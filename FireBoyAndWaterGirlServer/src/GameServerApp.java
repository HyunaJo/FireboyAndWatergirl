
public class GameServerApp {
	private static GameServerFrame gameServer1 = new GameServerFrame(10000);
	private static GameServerFrame gameServer2 = new GameServerFrame(20000);
	private static GameServerFrame gameServer3 = new GameServerFrame(30000);
	
	public static void main(String[] args) {
		gameServer1.setLocation(0,100);
		gameServer2.setLocation(400,100);
		gameServer3.setLocation(800,100);
	}
}
