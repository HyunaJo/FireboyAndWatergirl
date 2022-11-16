import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

public class GamePlayPanel extends JPanel implements Runnable{
	   
	private final int WIDTH = 717;
	private final int HEIGHT = 563;
	public KeyAdapter testKey;
	
	   //게임 제어를 위한 변수
	   int status;//게임의 상태
	   int cnt;//루프 제어용 컨트롤 변수
	   int delay;//루프 딜레이. 1/1000초 단위.
	   long pretime;//루프 간격을 조절하기 위한 시간 체크값
	   int keybuff;//키 버퍼값
	   Thread mainwork;
	   JumpThread jumpThread = null;

	    // 이미지 파일 불러오는 툴킷.
	   Toolkit imageTool = Toolkit.getDefaultToolkit();
	   Image character = imageTool.getImage("src/static/image/character/water_girl_character.png");
	   Image map = imageTool.getImage("src/static/image/background/gamePlayBackground.jpg");
	  
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

	      mainwork=new Thread(this);
	      mainwork.start();
	   }
	    
	  
	    public GamePlayPanel(){
	        // 프레임의 대한 설정.
	        setSize(WIDTH,HEIGHT);
	        // 프레임의 x버튼 누르면 프로세스 종료.
	        systeminit();
	        
	        testKey = new KeyAdapter() {
	            @Override
	            public void keyPressed(KeyEvent e) {
	            	System.out.println("키가 눌림");
	                switch(e.getKeyCode()) {
	                    case KeyEvent.VK_UP:
	                    	if(jumpThread!=null) {
	                    		jumpThread = new JumpThread();
		                    	jumpThread.start();
	                    	}
//	                        ypos-= 8;
	                        break;
	                    case KeyEvent.VK_DOWN:
	                        ypos+=8;
	                        break;
	                    case KeyEvent.VK_LEFT:
	                    	 System.out.println("left 키 눌림 "); 
	                    	character = imageTool.getImage("src/static/image/character/water girl_run left.gif");
	                        xpos-=8;
	                        break;
	                    case KeyEvent.VK_RIGHT:
	                    	 System.out.println("right 키 눌림"); 
	                    	character = imageTool.getImage("src/static/image/character/water girl_run right.gif");
	                        xpos+=8;
	                        break;
	                }
	            }
	            @Override
	            public void keyReleased(KeyEvent e) {
	            	character = imageTool.getImage("src/static/image/character/water_girl_character.png");
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
	        buffG.drawImage(map,0,0, this);
	        buffG.drawImage(character,xpos,ypos, this);
	        g.drawImage(buffImg,0,0,this); // 화면g애 버퍼(buffG)에 그려진 이미지(buffImg)옮김. ( 도화지에 이미지를 출력 )
	        repaint();
	    }
	    
	    private class JumpThread extends Thread{
	    	private int jumpLimit = 50; // jumpLimit번 위로 움직이고 jumpLimit번 다시 아래로 움직임
	    	private int changeLoc = 5; // 한번 움직일 때 changeLoc만큼 움직이기
	    	private int changeNum = 0; // 현재 움직인 횟수
	    	private boolean isJumping = true; // 점프 중인가
	    	
	    	public void run() {
	    		while(true) {
	    			if(changeNum==50) {
	    				isJumping = false;
	    				changeNum = 0;
	    			}else {
	    				if(isJumping) {
	    					ypos-=changeLoc;
	    					System.out.println("ypos = "+ypos+" changeNum = "+changeNum);
	    					changeNum++;
	    				}
	    				else {
	    					ypos+=changeLoc;
	    					System.out.println(ypos);
	    					changeNum++;
	    				}
	    			}
	    			if(isJumping==false && changeNum==50) {
	    				break;
	    			}
	    			try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    	}
	    }
	    
	    
}