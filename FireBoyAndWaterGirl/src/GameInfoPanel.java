import java.awt.Color;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameInfoPanel extends JPanel{
	private JLabel serverLabel = new JLabel();
	Image serverNameImg;
	
	private Image unclickedHomeImg = new ImageIcon("src/static/image/elements/unclickedHome.png").getImage().getScaledInstance(35,35,Image.SCALE_SMOOTH);
	private ImageIcon unclickedHomeIcon = new ImageIcon(unclickedHomeImg);
	private Image homeImg = new ImageIcon("src/static/image/elements/clickedHome.png").getImage().getScaledInstance(35,35,Image.SCALE_SMOOTH);
	private ImageIcon clickedHomeIcon = new ImageIcon(homeImg);
	private JButton homeBtn = new JButton(unclickedHomeIcon);
	
	private PlayerListPanel playerList = new PlayerListPanel();
	
	public GameInfoPanel() {
		setSize(269, 563);
		setLayout(null);
		setVisible(true);
		
		serverLabel.setBounds(10,10,201,40);
		
		// home 버튼
		homeBtn.setBorderPainted(false);
		homeBtn.setFocusPainted(false);
		homeBtn.setContentAreaFilled(false);
		homeBtn.setBounds(serverLabel.getWidth()+serverLabel.getX()+10, 10, 40, 40);
		add(homeBtn);
		
		homeBtn.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				homeBtn.setIcon(clickedHomeIcon);
				// 홈으로 이동 팝업창 띄우기
				int answer = JOptionPane.showConfirmDialog(null, "홈으로 이동하시겠습니까?", "",JOptionPane.YES_NO_OPTION );
				if(answer==JOptionPane.YES_OPTION){  //사용자가 yes를 눌렀을 경우
					GameClientFrame.isChanged = true; // 화면 변화가 필요함
					GameClientFrame.isHomeScreen = true; // 홈 화면으로 변화
					GameClientFrame.net.exitRoom();
					GameClientFrame.net.interrupt();
					GameClientFrame.net = null;
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				homeBtn.setIcon(unclickedHomeIcon);
			}
		});
		
		// 참가자 목록
		playerList.setBounds(13,55, playerList.getWidth(), playerList.getHeight());
		add(playerList);
		
		///// 테스트용으로 참가자 정보 박아둔것 /////
		playerList.addPlayerLabel(1,"FireBoy : 파송송");
		playerList.addPlayerLabel(2,"WaterGirl : 새하얀돌덩이");
		///////////////////////////////////
		
		// 채팅 기록 보는 부분
		ChattingPanel chattingPane = new ChattingPanel();
		chattingPane.setBounds(13,147,chattingPane.getWidth(),chattingPane.getHeight());
		add(chattingPane);
	}
	
	public void setServerName(int serverNum) { // 서버 이름 이미지 붙이기
		String serverNameImgPath="";
		switch(serverNum) {
		case 1:
			serverNameImgPath = "server1.png";
			break;
		case 2:
			serverNameImgPath = "server2.png";
			break;
		case 3:
			serverNameImgPath = "server3.png";
			break;
		}
		
		serverNameImg = new ImageIcon("src/static/image/text/"+serverNameImgPath).getImage().getScaledInstance(serverLabel.getWidth(), serverLabel.getHeight(), Image.SCALE_SMOOTH);		
		ImageIcon serverNameIcon = new ImageIcon(serverNameImg);
		serverLabel.setIcon(serverNameIcon);
		add(serverLabel);
	}
}
