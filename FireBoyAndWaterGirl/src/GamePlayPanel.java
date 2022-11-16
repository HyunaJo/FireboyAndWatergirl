import javax.swing.JPanel;

import MapObject.Block;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePlayPanel extends JPanel implements Runnable{
	   
	private final int WIDTH = 713;
	private final int HEIGHT = 544;
	
	private Map map;
	private ArrayList<Block> blocks = null;
	

	public KeyAdapter testKey;
	
	   //게임 제어를 위한 변수
	   int status;//게임의 상태
	   int cnt;//루프 제어용 컨트롤 변수
	   int delay;//루프 딜레이. 1/1000초 단위.
	   long pretime;//루프 간격을 조절하기 위한 시간 체크값
	   int keybuff;//키 버퍼값
	   Thread mainwork;
	   MoveThread moveThread = new MoveThread();
	   boolean isMovingRight = false;
	   boolean isMovingLeft = false;
	   boolean isJumping = false;
	   boolean isFalling = false;
	   boolean isLand = true;
	   
	   int resetTotalDistance = 100;
	   int jumpingTotalDistance = resetTotalDistance;
	   int jumpingDist = 3;
	   int fallingDist = 2;
	   int xmovingDist = 2;

	    // 이미지 파일 불러오는 툴킷.
	   Toolkit imageTool = Toolkit.getDefaultToolkit();
	   Image character = imageTool.getImage("src/static/image/character/water_girl_character.png");
	   Image mapImg = imageTool.getImage("src/static/image/background/game_play_background.png");
	  
	   // 이미지 버퍼
	   Image buffImg;
	   Graphics buffG;
	   
	   // 플레이어 위치값.
	    int xpos = 100;
	    int ypos = 100;

	    boolean roof=true;//스레드 루프 정보
	    
	    // 스레드 파트
	    public void run(){
	       try
	       {
	    	  System.out.println("run 함수에 들어옴"); 
	          while(roof){
	             
	             pretime=System.currentTimeMillis();

	             repaint();//화면 리페인트
	          
	             if(System.currentTimeMillis()-pretime<delay) Thread.sleep(delay-System.currentTimeMillis()+pretime);
	                //게임 루프를 처리하는데 걸린 시간을 체크해서 딜레이값에서 차감하여 딜레이를 일정하게 유지한다.
	                //루프 실행 시간이 딜레이 시간보다 크다면 게임 속도가 느려지게 된다.

	             if(status!=4) cnt++;
	          }
	       }
	       catch (Exception e)
	       {
	          e.printStackTrace();
	       }
	    }
	    
	    
	    public void systeminit(){//프로그램 초기화
	      status=0;
	      cnt=0;
	      delay=17;// 17/1000초 = 58 (프레임/초)
	      keybuff=0;
	      
	      //맵 설정
	      map = new Map("src/resource/map1.txt");
	      blocks = map.getBlocks();

	      mainwork=new Thread(this);
	      mainwork.start();
	   }
	    
	  
	    public GamePlayPanel(){
	        // 프레임의 대한 설정.
	        setSize(WIDTH,HEIGHT);
	        // 프레임의 x버튼 누르면 프로세스 종료.
	        systeminit();
	        moveThread.start();
	        
	        testKey = new KeyAdapter() {
	            @Override
	            public void keyPressed(KeyEvent e) {
	            	System.out.println("키가 눌림");
	                switch(e.getKeyCode()) {
	                    case KeyEvent.VK_UP:
	                    	if(!isJumping && !isFalling)
	                    		isJumping = true;
//	                        ypos-= 8;
	                        break;
	                    case KeyEvent.VK_DOWN:
	                        ypos+=8;
	                        break;
	                    case KeyEvent.VK_LEFT:
	                    	 System.out.println("left 키 눌림 ");
	                    	 if(isMovingRight)
	                    		 isMovingRight=false;
	                    	 isMovingLeft = true;
	                        break;
	                    case KeyEvent.VK_RIGHT:
	                    	 System.out.println("right 키 눌림"); 
	                    	 if(isMovingLeft)
	                    		 isMovingLeft=false;
	                    	 isMovingRight = true;
	                    	
//	                    	xpos+=8;
	                        break;
	                }
	            }
	            @Override
	            public void keyReleased(KeyEvent e) {
	            	switch(e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                    	 System.out.println("right 키 그만 누름"); 
                    	 isMovingRight = false;
                    	 break;
                    case KeyEvent.VK_LEFT:
                    	System.out.println("left 키 그만 누름"); 
                   	 	isMovingLeft = false;
                   	 	break;
                }
	            	
	            }
	        };
	        // 키 어댑터 ( 키 처리용 )
//	        addKeyListener();

	    }

	    @Override
	    public void paint(Graphics g) {
	    	buffImg = createImage(getWidth(),getHeight()); // 버퍼링용 이미지 ( 도화지 )
	        buffG = buffImg.getGraphics(); // 버퍼링용 이미지에 그래픽 객체를 얻어야 그릴 수 있다고 한다. ( 붓? )
	        update(g);
	    }

	    @Override
	    public void update(Graphics g) {
	        buffG.clearRect(0, 0, WIDTH, HEIGHT); // 백지화
	        buffG.drawImage(mapImg,0,0, this);
	        buffG.drawImage(character,xpos,ypos, this);
	        
	        for (Block block : blocks)
	        	buffG.drawImage(block.getImg(),block.getX(),block.getY(),this);
	       
	        g.drawImage(buffImg,0,0,this); // 화면g애 버퍼(buffG)에 그려진 이미지(buffImg)옮김. ( 도화지에 이미지를 출력 )
	        repaint();
	    }
	    
	    private class MoveThread extends Thread{
	    	public void run() {
	    		while(true) {
	    			setCharacterImg();
	    			if(isJumping)
	    				jumping();
	    			if(isFalling)
	    				falling();
	    			if(isMovingLeft||isMovingRight)
	    				xMoving();
	    			try {
						sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    	}
	    }
	    
	    public void setCharacterImg() {
	    	if(isMovingLeft) {
	    		character = imageTool.getImage("src/static/image/character/water girl_run left.gif");
	    	}
	    	else if(isMovingRight) {
	    		character = imageTool.getImage("src/static/image/character/water girl_run right.gif");
	    	}
	    	else {
	    		character = imageTool.getImage("src/static/image/character/water_girl_character.png");
	    	}
	    }
	    
	    public void resetTotalJumpDist() {
	    	jumpingTotalDistance = resetTotalDistance;
	    }
	    
	    public void jumping() {
	    	if(jumpingTotalDistance <= 0) {
	    		isJumping = false;
	    		isFalling = true;
	    		resetTotalJumpDist();
	    	}
	    	else {
	    		ypos -= jumpingDist;
	    		jumpingTotalDistance -= jumpingDist;
	    	}
	    }
	    
	    public void falling() {
	    	if(jumpingTotalDistance <= 0) {
	    		isFalling = false;
	    		resetTotalJumpDist();
	    	}
	    	else {
	    		ypos += fallingDist;
	    		jumpingTotalDistance -= fallingDist;
	    	}
	    }
	    
	    public void xMoving() {
	    	if(isMovingRight) {
	    		xpos += xmovingDist;
	    	}
	    	else if(isMovingLeft) {
	    		xpos -= xmovingDist;
	    	}
	    }
	    
//	    private class JumpThread extends Thread{
//	    	
//
//	    	
//	    	public void run() {
//	    		while(true) {
//	    			if(changeNum==jumpLimit) {
//	    				isJumping = false;
//	    				changeNum = 0;
//	    			}else {
//	    				if(isJumping) {
//	    					ypos-=changeLoc;
//	    					changeNum++;
//	    				}
//	    				else {
//	    					ypos+=changeLoc;
//	    					changeNum++;
//	    				}
//	    			}
//	    			if(isJumping==false && changeNum==jumpLimit) {
//	    				break;
//	    			}
//	    			try {
//						sleep(10);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//	    		}
//	    	}
//	    }
//	    
//	    private class XMoveThread extends Thread{
//	    	public void run() {
//	    		while(true) {
//	    			try {
//	    				if(isMovingRight) {
//	    					xpos+=3;
//	    				}
//	    				else if(isMovingLeft) {
//	    					xpos-=3;
//	    				}
//						sleep(20);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//	    		}
//	    	}
//	    }
	    
	    
}