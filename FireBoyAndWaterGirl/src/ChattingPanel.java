import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class ChattingPanel extends JPanel{
	private JScrollPane chattingHistoryScroll = new JScrollPane();
	private JTextPane chattingHistory;
	private JTextArea chattingInput;
	private String name = "파송송"; // player 닉네임
	
	private JPanel chattingInputPane;
	JLabel nameLabel;
	
	private Color sendBtnColor = new Color(170,170,170);
	private Color sendBtnPressedColor = new Color(217,217,217);
	
	public ChattingPanel() {
		setSize(245,405);
		setLayout(null);
//		setBackground(Color.CYAN);
		
		// 채팅 기록 보는 부분
		chattingHistoryScroll.setBounds(0, 0, 245, 309);
		chattingHistoryScroll.getViewport().setBackground(Color.WHITE);
		chattingHistoryScroll.setBorder(null);
		add(chattingHistoryScroll);
		
		chattingHistory = new JTextPane();
		chattingHistory.setEditable(false);
		chattingHistory.setFont(new Font("굴림체", Font.PLAIN, 12));
		chattingHistoryScroll.setViewportView(chattingHistory);
		
		// 채팅 작성해서 보내는 부분
		chattingInputPane = new JPanel();
		chattingInputPane.setBounds(0, 316, 245, 88);
		chattingInputPane.setLayout(null);
		chattingInputPane.setBackground(Color.WHITE);
		
		nameLabel = new JLabel("[ "+name+" ] : "); // 채팅 입력하는 창에 뜰 player 닉네임
		Font nameFont = new Font(nameLabel.getFont().getName(),Font.BOLD,12);
		nameLabel.setFont(nameFont);
		nameLabel.setBounds(3,3,245,15);
		chattingInputPane.add(nameLabel);
		
		chattingInput = new JTextArea();
		chattingInput.setLineWrap(true); // 자동 줄바꿈
//		chattingInput.setWrapStyleWord(true); // 단어 단위 줄바꿈
		JScrollPane chattingInputScroll = new JScrollPane(chattingInput);
		chattingInputScroll.setBounds(3,23,239,38);
		chattingInputScroll.getViewport().setBackground(Color.WHITE);
		chattingInputScroll.setBorder(null);
		
		chattingInput.setBounds(0,0,239,38);
		chattingInput.setBackground(Color.WHITE);
		chattingInput.setFont(new Font("굴림체", Font.PLAIN, 12));
		chattingInput.setCaretPosition(chattingInput.getDocument().getLength());
		chattingInputPane.add(chattingInputScroll);
		add(chattingInputPane);
		
		JButton sendBtn = new JButton("전송");
		sendBtn.setBounds(180,63,60,20);
		sendBtn.setBorderPainted(false);
		sendBtn.setFocusPainted(false);
		sendBtn.setBackground(new Color(217,217,217));
		sendBtn.setContentAreaFilled(false);
		sendBtn.setOpaque(true);
		chattingInputPane.add(sendBtn);
		
		sendBtn.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				sendBtn.setBackground(sendBtnColor);
				// 채팅 보내기
				String text = chattingInput.getText();
				//////////////////
				//// 채팅 보내는 함수
				//////////////////
				chattingInput.setText("");
			}
			
			public void mouseReleased(MouseEvent e) {
				sendBtn.setBackground(sendBtnPressedColor);
			}
		});
	}
}
