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
	
	public GameClientFrame() {
		setTitle("FireBoy and WaterGirl");
		setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
		setResizable(false);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		setContentPane(new GameIntroPanel());
//		setContentPane(new GameScreenPanel());
		
		setVisible(true);
	}
}






