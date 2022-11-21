import javax.swing.ImageIcon;
import javax.swing.JPanel;

import MapObject.Block;
import MapObject.Item;

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
   
   int resetTotalDistance = 80;
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
		
		//if(!this.threadFlag)
			//this.gameThread.interrupt();
	}
    
    int pWith = 57;//68;
    int pHeight = 51;//61;
    
    public static void removeItem(int i) { // 상대방이 먹은 item 없애기
    	items.remove(i);
    }
    
    public void playerItemGetCheck() {
		//플레이어가 무적이 아닐 때만 체크
			for(int i=0;i<items.size();i++) {//Item m : items
				
				Item m = items.get(i);
				// [경우1] 플레이어가 아이템의 오른쪽에 존재
				if (myXpos+pWith >= m.getX() && myXpos+pWith <= m.getX()+0.5*m.getWidth() 
				&& myYpos <= m.getY() && myYpos+pHeight >= m.getY()+m.getHeight()) {
					items.remove(m);
					//TODO:네트워크로 사라진 아이템 인덱스 보내줘야함!!
					//int roomId, String code, int itemIdx
					System.out.println("아이템 지우라고 보냈다.");
					ListenNetwork.SendObject(new ChatMsg(GameClientFrame.roomId,"550",i));
					break;
				}
				// [경우2] 플레이어가 아이템의 왼쪽에 존재
				else if (myXpos >= m.getX() + 0.5*m.getWidth() && myXpos <= m.getX() + m.getWidth()
				&& myYpos <= m.getY() && myYpos+pHeight >= m.getY()+m.getHeight()) {
					items.remove(m);
					//TODO:네트워크로 사라진 아이템 인덱스 보내줘야함!!
					System.out.println("아이템 지우라고 보냈다.");
					ListenNetwork.SendObject(new ChatMsg(GameClientFrame.roomId,"550",i));
					break;
				}
				else
					continue;

			}
	}
    
   // =======================================================================================
    // 스레드 파트
    public void run(){
       try
       {
    	  System.out.println("run 함수에 들어옴"); 
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
            @Override
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
       
        g.drawImage(buffImg,0,0,this); // 화면g애 버퍼(buffG)에 그려진 이미지(buffImg)옮김. (도화지에 이미지를 출력)
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
    			System.out.println(canMove());
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
    		myYpos -= jumpingDist;
    		jumpingTotalDistance -= jumpingDist;
    	}
    }
    
    public void falling() {
    	if(jumpingTotalDistance <= 0) {
    		isFalling = false;
    		resetTotalJumpDist();
    	}
    	else {
    		myYpos += fallingDist;
    		jumpingTotalDistance -= fallingDist;
    	}
    }
    
    public void xMoving() {
    	if(isMovingRight) {
    		myXpos += xmovingDist;
    	}
    	else if(isMovingLeft) {
    		myXpos -= xmovingDist;
    	}
//    	System.out.println(xpos+","+ypos);
    }
    
    public void setMoving(int x, int y, State type) {
    	System.out.println("setMoving이 불림");
    	opponentXpos = x;
    	opponentYpos = y;
    	opponentInfo.setState(type);
    }

//    public void checkEdgeWalls() {
//    	switch(myInfo.getState()) {
//    	case LEFT:
//    	case RIGHT:
//    		myWidth = RUN_IMG_WIDTH;
//    		myHeight = RUN_IMG_HEIGHT;
//    		break;
//    	case FRONT:
//    		myWidth = IMG_WIDTH;
//    		myHeight = IMG_HEIGHT;
//    		break;
//    	}
//    }
    
    public boolean canMove() {
    	switch(myInfo.getState()) {
    	case LEFT:
    	case RIGHT:
    		myWidth = RUN_IMG_WIDTH;
    		myHeight = RUN_IMG_HEIGHT;
    		break;
    	case FRONT:
    		myWidth = IMG_WIDTH;
    		myHeight = IMG_HEIGHT;
    		break;
    	}
    	
    	Rectangle characterRec = new Rectangle(myXpos,myYpos,myWidth-5,myHeight-5);
		for(int i=0;i<blocks.size();i++) {
			if(characterRec.intersects(blocks.get(i).getRectangle()))
				return false;
		}
		return true;
	}
}