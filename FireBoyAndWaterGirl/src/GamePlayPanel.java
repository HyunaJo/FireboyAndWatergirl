import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import MapObject.Block;
import MapObject.Item;
import MapObject.Obstacle;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePlayPanel extends JPanel implements Runnable{
	   
	private final int WIDTH = 713;
	private final int HEIGHT = 544;
	
	private final int IMG_WIDTH = 48;
	private final int IMG_HEIGHT = 59;
	private final int RUN_IMG_WIDTH = 68;
	private final int RUN_IMG_HEIGHT = 61;
	
	int myWidth, myHeight;
	
	private Map map;
	private ArrayList<Block> blocks = null;
	public static ArrayList<Item> items = null;
	public static ArrayList<Obstacle> obstacles = null;
	
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
   boolean isDie = false;
   boolean isOpponentDie = false;
   
   int resetTotalDistance = 90;
   int jumpingTotalDistance = resetTotalDistance;
   int jumpingDist = 6;
   int fallingDist = 6;
   int xmovingDist = 6;

    // 이미지 파일 불러오는 툴킷.
   Toolkit imageTool = Toolkit.getDefaultToolkit();
   PlayerInfo myInfo = new PlayerInfo();
   PlayerInfo opponentInfo = new PlayerInfo();
   
   Image character;
   Image opponent;
   Rectangle characterRec;
   
   Image mapImg = imageTool.getImage("src/static/image/background/game_play_background.png");
  
   // 이미지 버퍼
   Image buffImg;
   Graphics buffG;
   
   // 플레이어 위치값.
    int myXpos = 100;
    int myYpos = 100;
    
    int opponentXpos = 100;
    int opponentYpos = 100;

    boolean roof=true;//스레드 루프 정보

   // =================================================================================================
    public void gameControll() {
		//playerWallCrushCheck();
		//playerMonsterCrushCheck();
		//monsterWallCrushCheck();
		playerItemGetCheck();
		//checkBubbleMonster();
		playerObstacleCheck();
		//if(!this.threadFlag)
			//this.gameThread.interrupt();
	}
    
    int pWith = 57;//68;
    int pHeight = 51;//61;
    
    public static void removeItem(int i) { // 상대방이 먹은 item 없애기
    	items.remove(i);
    }
    
    public void playerItemGetCheck() {
			for(int i=0;i<items.size();i++) {//Item m : items
				
				Item m = items.get(i);
				if (!(m.getState()%2 == GameClientFrame.userNum%2)) continue;
				
				if(((m.getX()<=myXpos&&myXpos<=m.getX()+m.getWidth())||(m.getX()<=myXpos+myWidth&&myXpos+myWidth<=m.getX()+m.getWidth()))
						&&((myYpos<=m.getY()&&m.getY()<=myYpos+myHeight)||(myYpos<=m.getY()+m.getHeight()&&m.getY()+m.getHeight()<=myYpos+myHeight))) {
					items.remove(m);
					//TODO:네트워크로 사라진 아이템 인덱스 보내줘야함!!
					ListenNetwork.SendObject(new ChatMsg(GameClientFrame.roomId,"550",i));
					break;
				}
				else
					continue;

			}
	}
    
    public void playerObstacleCheck() {
		//플레이어가 무적이 아닐 때만 체크
			for(int i=0;i<obstacles.size();i++) {//Item m : items
				
				Obstacle o = obstacles.get(i);
			
				if (o.getState()%2 == GameClientFrame.userNum%2) {
					continue;
					
				}
				if(((o.getX()<=myXpos+myWidth&&myXpos+myWidth<=o.getX()+o.getWidth())||(o.getX()<=myXpos&&myXpos<=o.getX()+o.getWidth()))&&
						(o.getY()<=myYpos+myHeight+10&&myYpos+myHeight+10<=o.getY()+o.getHeight())) {
					System.out.println("1  Game Over!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					isDie = true;
					character = imageTool.getImage(myInfo.getDieImgPath());
					ListenNetwork.SendObject(new ChatMsg(GameClientFrame.userName,GameClientFrame.roomId,"600","GameOver")); // GameOver 전송
					break;
				}
					
				else {
//					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//					System.out.println("o.getY = "+o.getY());
//					System.out.println("myYpos = "+ (myYpos+pHeight));
					continue;
				}
			}
	}
   
    
   // =======================================================================================
    // 스레드 파트
    public void run(){
       try
       {
          while(roof){
             
             pretime=System.currentTimeMillis();
             gameControll();
             if(isDie || isOpponentDie) { // 죽은 경우 스레드 종료
            	 break;
             }
             
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
       
//       System.out.println("game 종료");
    }
    
    
    public void systeminit(){//프로그램 초기화
      status=0;
      cnt=0;
      delay=17;// 17/1000초 = 58 (프레임/초)
      keybuff=0;
      
      //맵 설정
      map = new Map("src/resource/map1.txt");
      blocks = map.getBlocks();
      items = map.getItems();
      obstacles = map.getObstacles();
      // 캐릭터 설정
      switch(GameClientFrame.userNum) {
      case 1:
    	  myInfo.setUserNum(1);
    	  myInfo.setCharacterImgPath("src/static/image/character/fire_boy_character.png");
    	  myInfo.setRunRightImgPath("src/static/image/character/fire boy_run right.gif");
    	  myInfo.setRunLeftImgPath("src/static/image/character/fire boy_run left.gif");
    	  myXpos = 384;
    	  myYpos = 452;
    	  
    	  opponentInfo.setUserNum(2);
    	  opponentInfo.setCharacterImgPath("src/static/image/character/water_girl_character.png");
    	  opponentInfo.setRunRightImgPath("src/static/image/character/water girl_run right.gif");
    	  opponentInfo.setRunLeftImgPath("src/static/image/character/water girl_run left.gif");
    	  opponentXpos = 288;
    	  opponentYpos = 452;
    	  break;
      case 2:
    	  myInfo.setUserNum(2);
    	  myInfo.setCharacterImgPath("src/static/image/character/water_girl_character.png");
    	  myInfo.setRunRightImgPath("src/static/image/character/water girl_run right.gif");
    	  myInfo.setRunLeftImgPath("src/static/image/character/water girl_run left.gif");
    	  myXpos = 288;
    	  myYpos = 452;
    	  
    	  opponentInfo.setUserNum(1);
    	  opponentInfo.setCharacterImgPath("src/static/image/character/fire_boy_character.png");
    	  opponentInfo.setRunRightImgPath("src/static/image/character/fire boy_run right.gif");
    	  opponentInfo.setRunLeftImgPath("src/static/image/character/fire boy_run left.gif");
    	  opponentXpos = 384;
    	  opponentYpos = 452;
    	  break;
      }
      character = imageTool.getImage(myInfo.getCharacterImgPath());
//	  System.out.println(new ImageIcon(character).getIconWidth()+","+new ImageIcon(character).getIconHeight());
      opponent =  imageTool.getImage(opponentInfo.getCharacterImgPath());
      mainwork=new Thread(this);
      mainwork.start();
   }
    
  
    public GamePlayPanel(){
        // 프레임의 대한 설정.
        setSize(WIDTH,HEIGHT);
        
        // 프레임의 x버튼 누르면 프로세스 종료.
        systeminit();
        
        MovingInfo obcm = new MovingInfo("400", GameClientFrame.roomId, myXpos, myYpos, GameClientFrame.userNum, State.FRONT); // gameRoom 입장 시도
		ListenNetwork.SendObject(obcm);
		
        moveThread.start();
        
        testKey = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
//            	System.out.println("키가 눌림");
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    	if(!isJumping && !isFalling)
                    		isJumping = true;
                        break;
                    case KeyEvent.VK_LEFT:
//                    	 System.out.println("left 키 눌림 ");
                    	 if(isMovingRight)
                    		 isMovingRight=false;
                    	 isMovingLeft = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                    	if(!isMovingRight)
                    		myXpos -= 10;
//                    	 System.out.println("right 키 눌림"); 
                    	 if(isMovingLeft)
                    		 isMovingLeft=false;
                    	 isMovingRight = true;
                    	 
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
            	switch(e.getKeyCode()) {
                case KeyEvent.VK_RIGHT:
//                	 System.out.println("right 키 그만 누름"); 
                	 myXpos += 10;
                	 isMovingRight = false;
                	 break;
                case KeyEvent.VK_LEFT:
//                	System.out.println("left 키 그만 누름"); 
                	
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
        if(characterRec!=null) {
        	g.setColor(Color.YELLOW);
        	g.drawRect(characterRec.x,characterRec.y,characterRec.width,characterRec.height);
        }
        
        for(int i=0;i<items.size();i++) {
        	g.setColor(Color.CYAN);
        	g.drawRect(items.get(i).getX(), items.get(i).getY(), items.get(i).getWidth(), items.get(i).getHeight());
        }
    }


    @Override
    public void update(Graphics g) {
        buffG.clearRect(0, 0, WIDTH, HEIGHT); // 백지화
        buffG.drawImage(mapImg,0,0, this);
        buffG.drawImage(character, myXpos, myYpos, this);
        
        if(!isOpponentDie) {
        	switch(opponentInfo.getState()) {
            case LEFT:
            	opponent = imageTool.getImage(opponentInfo.getRunLeftImgPath());
            	break;
            case RIGHT:
            	opponent = imageTool.getImage(opponentInfo.getRunRightImgPath());
            	break;
            case FRONT:
            	opponent = imageTool.getImage(opponentInfo.getCharacterImgPath());
            	break;
            }
        }
        
        buffG.drawImage(opponent, opponentXpos, opponentYpos, this);
        
        for (Block block : blocks)
        	buffG.drawImage(block.getImg(),block.getX(),block.getY(),this);
        
        for (Item item : items)
        	buffG.drawImage(item.getImg(),item.getX(),item.getY(),this);
        
        for (Obstacle obstacle : obstacles)
        	buffG.drawImage(obstacle.getImg(),obstacle.getX(),obstacle.getY(),this);
       
        g.drawImage(buffImg,0,0,this); // 화면g애 버퍼(buffG)에 그려진 이미지(buffImg)옮김. (도화지에 이미지를 출력)
        repaint();
    }
    
    private class MoveThread extends Thread{
    	public void run() {
    		while(true) {
    			if(isDie||isOpponentDie) // 죽은 경우 스레드 종료
    				break;
    			setCharacterImg();
    			if(isJumping)
    				jumping();
    			else {
    				falling();
    			}
    			if(isMovingLeft||isMovingRight)
    				xMoving();
    			MovingInfo obcm = new MovingInfo("400", GameClientFrame.roomId, myXpos, myYpos, GameClientFrame.userNum, myInfo.getState());
    			ListenNetwork.SendObject(obcm);
    			try {
					sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    }
    
    public void setCharacterImg() {
    	if(isMovingLeft) {
    		character = imageTool.getImage(myInfo.getRunLeftImgPath());
    		myInfo.setState(State.LEFT);
    	}
    	else if(isMovingRight) {
    		character = imageTool.getImage(myInfo.getRunRightImgPath());
    		myInfo.setState(State.RIGHT);
    	}
    	else {
    		character = imageTool.getImage(myInfo.getCharacterImgPath());
    		myInfo.setState(State.FRONT);
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
    		if(canMove(myXpos,myYpos-jumpingDist/2)) {
    			myYpos -= jumpingDist;
    			jumpingTotalDistance -= jumpingDist;
    		}
    		else {
    			resetTotalJumpDist();
    			isJumping = false;
    			isFalling = true;
    		}
    	}
    }
    
    public void falling() {
    	if(canMove(myXpos, myYpos + fallingDist)) {
    		myYpos += fallingDist;
    	}
    	else {
    		isFalling = false;
    	}
    }
    
    public void xMoving() {
    	if(isMovingRight) {
    		myXpos+=xmovingDist;
    		if(!canMove(myXpos, myYpos)) {
    			myXpos -= xmovingDist;
    		}
    	}
    	else if(isMovingLeft) {
    		myXpos-=xmovingDist;
    		if(!canMove(myXpos, myYpos)) {
    			myXpos += xmovingDist;
    		}
    	}
    }
    
    public void setMoving(int x, int y, State type) {
    	System.out.println("setMoving이 불림");
    	opponentXpos = x;
    	opponentYpos = y;
    	opponentInfo.setState(type);
    }
    
    public void setDieImage() {
    	System.out.println("opponent die Image");
    	opponent = imageTool.getImage(opponentInfo.getDieImgPath());
    	isOpponentDie = true;
    }
    
    public boolean canMove(int x, int y) { // 블럭, 장애물의 위=0,아래=1,좌=2,우=3, 어딘가=4
    	switch(myInfo.getState()) {
    	case LEFT:
    		if(GameClientFrame.userNum==1) { // fireboy
    			y += 10;
    			myWidth = RUN_IMG_WIDTH-45;
        		myHeight = RUN_IMG_HEIGHT-17;
    		}
    		else { // watergirl
    			y += 10;
    			myWidth = RUN_IMG_WIDTH-45;
        		myHeight = RUN_IMG_HEIGHT-17;
    		}
    		break;
    	case RIGHT:
    		if(GameClientFrame.userNum==1) { // fireboy
    			x += 23;
    			y += 10;
    			myWidth = RUN_IMG_WIDTH-45;
        		myHeight = RUN_IMG_HEIGHT-17;
    		}
    		else { // watergirl
    			x += 30;
    			y += 10;
    			myWidth = RUN_IMG_WIDTH-45;
        		myHeight = RUN_IMG_HEIGHT-17;
    		}
    		break;
    	case FRONT:
    		if(GameClientFrame.userNum==1) { // fireboy
    			x += 8;
    			y += 8;
        		myWidth = IMG_WIDTH-30;
        		myHeight = IMG_HEIGHT-14;
    		}
    		else { // watergirl
    			x += 10;
    			y += 8;
        		myWidth = IMG_WIDTH-30;
        		myHeight = IMG_HEIGHT-14;
    		}
    		break;
    	}
    	
    	characterRec = new Rectangle(x,y,myWidth,myHeight);
		for(int i=0;i<blocks.size();i++) {
			if(characterRec.intersects(blocks.get(i).getRectangle())) {
				return false;
			}
		}
		for(int i=0;i<obstacles.size();i++) {
			if(characterRec.intersects(obstacles.get(i).getRectangle())) {
				return false;
			}
		}
		return true;
	}
}