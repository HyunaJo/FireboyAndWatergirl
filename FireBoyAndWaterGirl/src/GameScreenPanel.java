import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

// 서버 입장 이후 게임하는 화면 (대기, 게임, 채팅...)
public class GameScreenPanel extends JPanel{
	public GameScreenPanel(int roomId) {
		setSize(GameClientFrame.SCREEN_WIDTH,GameClientFrame.SCREEN_HEIGHT);
		System.out.println(GameClientFrame.SCREEN_WIDTH+","+GameClientFrame.SCREEN_HEIGHT);
		setLayout(null);
		setVisible(true);

		// 게임 대기 화면 (오른쪽)
		GameWaitPanel gameWaitPane = new GameWaitPanel();
		gameWaitPane.setBounds(0, 0, gameWaitPane.getWidth(), gameWaitPane.getHeight());
		System.out.println(gameWaitPane.getWidth()+","+ gameWaitPane.getHeight());
		add(gameWaitPane);
		
		// 게임 정보 화면(왼쪽) (서버 정보, 홈버튼, 채팅...)
		GameInfoPanel gameInfoPane = new GameInfoPanel();
		gameInfoPane.setBounds(gameWaitPane.getWidth(), 0, gameInfoPane.getWidth(), gameInfoPane.getHeight());
		gameInfoPane.setServerName(roomId);
		add(gameInfoPane);

		// 테스트 위해서 임의로 박아둠 //////////
		gameWaitPane.changePlayerNum(2); // 입장한 플레이어 수=1
		/////////////////////////////////
	}
}
