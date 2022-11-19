package MapObject;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Item{
	
	public static final int ITEM_WIDTH = 21;
	public static final int ITEM_HEIGHT = 21;
	
	Image img = resizeImage(new ImageIcon("src/static/image/elements/fire_gem.png"),ITEM_WIDTH,ITEM_HEIGHT).getImage();
	
	public ImageIcon resizeImage(ImageIcon icon, int width, int height) {
		
		 return new ImageIcon(icon.getImage().getScaledInstance(width,height,Image.SCALE_SMOOTH));
	}
	
	int x, y, w, h;
	
	
	public Item() {}
	
	public Item(int x, int y) {
		this.x= x;
		this.y= y;
		this.w = ITEM_WIDTH;
		this.h = ITEM_HEIGHT;
	
	}
	
	public Image getImg() {return img;}
	public int getX() {return x;}
	public int getY() {return y;}
	public int getWidth() {return w;}
	public int getHeight() {return h;}
	
}