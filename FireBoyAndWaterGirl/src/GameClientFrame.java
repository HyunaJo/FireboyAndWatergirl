import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.event.*;

import java.util.Timer;
import java.util.TimerTask;
import java.awt.*;
import javax.swing.*;

public class GameClientFrame extends JFrame{
		
	public static final int SCREEN_WIDTH = 1000;
	public static final int SCREEN_HEIGHT = 600;
	
	public static ListenNetwork net = null;
	
	
	public static boolean isHomeScreen;
	public static boolean isWatingScreen;
	public static boolean isChanged;
	public static boolean isNextStage;
	
	
	public GameClientFrame() {
		
		init();
		setTitle("FireBoy and WaterGirl");
		setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
		setResizable(false);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		setContentPane(new GameIntroPanel());
		
		GameThread gth = new GameThread(); // 게임 화면 전환 스레드(게임 전반적 관리)
		gth.start();
		setVisible(true);
	}
	
	
class GameThread extends Thread{ // 게임 전반적 관리
		
		public void run() {
			
			while(true) {
				
				if(isChanged) { // 화면에 변화가 필요할 때
					
					isChanged = false;	
					selectScreen(); // 변경될 화면 선택
				}
				
				try {
					
					Thread.sleep(10);
			
					
				}catch(InterruptedException e) {
					
					return;
				}
			}
		}	
	}
	
	void init() {
		
		isHomeScreen = false; // 홈화면
		isWatingScreen = false; // 대기 화면
		isNextStage = false;
		isChanged = false; // 화면의 변화가 필요한지 여부
		
	}

	public void selectScreen() {

	
		if (isHomeScreen) { // 홈화면
			isHomeScreen = false;
			setContentPane(new GameIntroPanel());
			
		}
		else if (isWatingScreen) { // 대기화면
			isWatingScreen = false;
			setContentPane(new GameScreenPanel());
		
		}
		else if (isNextStage) {
			isNextStage = false;
		}

	}
}






