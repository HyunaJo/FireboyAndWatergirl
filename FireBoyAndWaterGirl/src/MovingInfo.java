import java.io.Serializable;

enum State {LEFT, RIGHT, FRONT}

public class MovingInfo implements Serializable{
	
	private String code;
	private int posX;
	private int posY;
	private int characterNum;
	private State type;
	
	public MovingInfo(String code, int posX, int posY, int characterNum, State type) {
		this.code = code;
		this.posX = posX;
		this.posY = posY;
		this.characterNum = characterNum;
		this.type = type;	
	}
}
