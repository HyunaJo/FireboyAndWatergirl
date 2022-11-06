import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerListPanel extends JPanel{
	private Color backgroundColor = new Color(79,135,85);
	private JLabel playerListLabel = new JLabel("참여자 목록");
	
	public PlayerListPanel() {
		setSize(245,86);
		setLayout(null);
		setBackground(backgroundColor);
		
		// 제목 label "참여자 목록"
		Font playerListTitleFont = new Font(playerListLabel.getFont().getName(),Font.BOLD,17);
		playerListLabel.setFont(playerListTitleFont);
		playerListLabel.setForeground(Color.WHITE);
		playerListLabel.setBounds(5,10,188,17);
		add(playerListLabel);
	}
	
	public void addPlayerLabel(int idx, String name) {
		JLabel playerNameLabel = new JLabel(name);
		playerNameLabel.setForeground(Color.WHITE);
		
		switch(idx) {
		case 1:
			playerNameLabel.setBounds(7,40,188,15);
			break;
		case 2:
			playerNameLabel.setBounds(7,60,188,15);
			break;
		}
		add(playerNameLabel);
	}
}
