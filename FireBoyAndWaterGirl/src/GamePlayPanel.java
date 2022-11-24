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
	
	private final int FIRE_IMG_WIDTH = 48;
	private final int FIRE_IMG_HEIGHT = 59;
	private final int FIRE_RUN_IMG_WIDTH = 68;
	private final int FIRE_RUN_IMG_HEIGHT = 61;
	
	private final int WATER_IMG_WIDTH = 48;
	private final int WATER_IMG_HEIGHT = 59;
	private final int WATER_RUN_IMG_WIDTH = 68;
	private final int WATER_RUN_IMG_HEIGHT = 61;
	
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
   boolean isLand = true;
   Block touchedBlock = null;
   int touchedBlockSide = 0; // 위, 아래, 왼, 오
   
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
				
				// [경우1] 플레이어가 아이템의 오른쪽에 존재
				if (myXpos+pWith >= m.getX() && myXpos+pWith <= m.getX()+0.5*m.getWidth() 
				&& myYpos <= m.getY() && myYpos+pHeight >= m.getY()+m.getHeight()) {
					items.remove(m);
					//TODO:네트워크로 사라진 아이템 인덱스 보내줘야함!!
					ListenNetwork.SendObject(new ChatMsg(GameClientFrame.roomId,"550",i));
					break;
				}
				// [경우2] 플레이어가 아이템의 왼쪽에 존재
				else if (myXpos >= m.getX() + 0.5*m.getWidth() && myXpos <= m.getX() + m.getWidth()
				&& myYpos <= m.getY() && myYpos+pHeight >= m.getY()+m.getHeight()) {
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
				
				// 플레이어가 장애물 위에 올라갔는지 판단!
				if ((o.getX() <= myXpos && myXpos+IMG_WIDTH <= o.getX()+o.getWidth()) && (o.getY() <= myYpos+IMG_HEIGHT && myYpos+IMG_HEIGHT <= o.getY()+10)) { //&& o.getY() == myYpos + IMG_HEIGHT
					
					//TODO:네트워크로 죽었음(게임 오버)을 표시
					System.out.println("1  Game Over!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					//ListenNetwork.SendObject(new ChatMsg(GameClientFrame.roomId,"550",i));
					break;
				}
				else if (o.getX() <= myXpos && myXpos+RUN_IMG_WIDTH <= o.getX()+o.getWidth() ) {//&& o.getY() == myYpos + RUN_IMG_HEIGHT
					
					//TODO:네트워크로 죽었음(게임 오버)을 표시
					System.out.println("2 Game Over!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					//ListenNetwork.SendObject(new ChatMsg(GameClientFrame.roomId,"550",i));
					break;
				}
				else {
					
					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
                    case KeyEvent.VK_DOWN:
                    	myYpos+=8;
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
//        	System.out.println("aaaaaaaaaaaaaaaaaaaa");
        	g.setColor(Color.YELLOW);
        	g.drawRect(characterRec.x,characterRec.y,characterRec.width,characterRec.height);
        }
    }


    @Override
    public void update(Graphics g) {
        buffG.clearRect(0, 0, WIDTH, HEIGHT); // 백지화
        buffG.drawImage(mapImg,0,0, this);
        buffG.drawImage(character, myXpos, myYpos, this);
        
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
    			setCharacterImg();
    			canMove(myXpos,myYpos);
    			if(isJumping)
    				jumping();
    			else {
    				falling();
    			}
//    			if(isFalling)
//    				falling();
    			if(isMovingLeft||isMovingRight)
    				xMoving();
    			System.out.println("============="+myXpos+","+myYpos+"========================================================");
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
//    		touchedBlock = canMove(myXpos,myYpos-jumpingDist);
    		if(canMove(myXpos,myYpos-jumpingDist/2)==-1) {
    			myYpos -= jumpingDist;
    			jumpingTotalDistance -= jumpingDist;
    		}
    		else {
//    			if(isMovingRight||isMovingLeft)
//    				myYpos = touchedBlock.getY() + touchedBlock.getHeight() - 10;
//    			else
//    				myYpos = touchedBlock.getY() + touchedBlock.getHeight();
    			resetTotalJumpDist();
    			isJumping = false;
    			isFalling = true;
    		}
    	}
    }
    
    public void falling() {
    	if(canMove(myXpos, myYpos + fallingDist)==-1) {
    		myYpos += fallingDist;
    	}
    	else {
    		isFalling = false;
    	}
//    	if(jumpingTotalDistance <= 0) {
//		isFalling = false;
////		resetTotalJumpDist();
//    	}
//    		touchedBlock = canMove(myXpos, myYpos + fallingDist);
//    		if(touchedBlock==null) {
//			
//			
//		}
//		else {
//			if(isMovingRight||isMovingLeft)
//				myYpos = touchedBlock.getY() - 61;
//			else
//				myYpos = touchedBlock.getY() - 59;
//			isFalling = false;
//			isLand = true;
//			jumpingTotalDistance = 0;
//		}
    }
    
    public void xMoving() {
    	if(isMovingRight) {
    		myXpos+=xmovingDist;
    		if(canMove(myXpos, myYpos)!=-1) {
    			myXpos -= xmovingDist;
    		}
    	}
    	else if(isMovingLeft) {
    		myXpos-=xmovingDist;
    		if(canMove(myXpos, myYpos)!=-1) {
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
    
    public int canMove(int x, int y) { // 블럭, 장애물의 위=0,아래=1,좌=2,우=3, 어딘가=4
    	switch(myInfo.getState()) {
    	case LEFT:
    		if(GameClientFrame.userNum==1) { // fireboy
    			y += 10;
    			myWidth = FIRE_RUN_IMG_WIDTH-45;
        		myHeight = FIRE_RUN_IMG_HEIGHT-13;
    		}
    		else { // watergirl
    			y += 10;
    			myWidth = WATER_RUN_IMG_WIDTH-45;
        		myHeight = WATER_RUN_IMG_HEIGHT-13;
    		}
    		break;
    	case RIGHT:
    		if(GameClientFrame.userNum==1) { // fireboy
    			x += 23;
    			y += 10;
    			myWidth = FIRE_RUN_IMG_WIDTH-45;
        		myHeight = FIRE_RUN_IMG_HEIGHT-13;
    		}
    		else { // watergirl
    			x += 30;
    			y += 10;
    			myWidth = WATER_RUN_IMG_WIDTH-45;
        		myHeight = WATER_RUN_IMG_HEIGHT-13;
    		}
    		break;
    	case FRONT:
    		if(GameClientFrame.userNum==1) { // fireboy
    			x += 8;
    			y += 10;
        		myWidth = FIRE_IMG_WIDTH-30;
        		myHeight = FIRE_IMG_HEIGHT-10;
    		}
    		else { // watergirl
    			x += 10;
    			y += 10;
        		myWidth = WATER_IMG_WIDTH-30;
        		myHeight = WATER_IMG_HEIGHT-10;
    		}
    		break;
    	}
    	
    	characterRec = new Rectangle(x,y,myWidth,myHeight);
		for(int i=0;i<blocks.size();i++) {
			if(characterRec.intersects(blocks.get(i).getRectangle())) {
				Block block = blocks.get(i);
				if(isMovingRight) {
					if(block.getX()<characterRec.x+characterRec.width && characterRec.x+characterRec.width<block.getX()+block.getWidth())
						return 3;
				}
				return 4;
			}
		}
		for(int i=0;i<obstacles.size();i++) {
			if(characterRec.intersects(obstacles.get(i).getRectangle())) {
				Obstacle obstacle = obstacles.get(i);
				if(isMovingRight) {
					if(obstacle.getX()<characterRec.x+characterRec.width && characterRec.x+characterRec.width<obstacle.getX()+obstacle.getWidth())
						return 3;
				}
				return 4;
			}
		}
		return -1;
	}
}