package MapObject;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Switch{ // state 9
	Image img = new ImageIcon("src/static/image/elements/switch.png").getImage();
	int x, y, w, h;
	private int state;
	Rectangle switchkRec;
	
	public static final int SWITHCH_WIDTH = 30;
	public static final int SWITHCH_HEIGHT = 12;
	
	public Switch() {}
	public ImageIcon resizeImage(ImageIcon icon, int width, int height) {
		
		 return new ImageIcon(icon.getImage().getScaledInstance(width,height,Image.SCALE_SMOOTH));
	}
	
	public Switch(int x, int y, int state) {
		this.x= x;
		this.y= y;
		this.state = state;
		this.w = SWITHCH_WIDTH;
		this.h = SWITHCH_HEIGHT;
		this.switchkRec = new Rectangle(x,y,w,h);
		setImg();
		//this.setBounds(x,y, w, h);
	}
	public void setImg() {
		if (state == 9) {
			img = resizeImage(new ImageIcon("src/static/image/elements/switch.png"),SWITHCH_WIDTH,SWITHCH_HEIGHT).getImage();
		}
		else { 
			//img = resizeImage(new ImageIcon("src/static/image/elements/fire_obstacle.png"),SWITHCH_WIDTH,SWITHCH_HEIGHT).getImage();
		}
	}
	
	public Image getImg() {return img;}
	public int getX() {return x;}
	public int getY() {return y;}
	public int getWidth() {return w;}
	public int getHeight() {return h;}
	public Rectangle getRectangle() {return switchkRec;}
	
}