import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameAudio {
	private Clip clip;
	private String audio;

	public GameAudio(String audio) {
		this.audio = audio;
		this.loadAudio(audio);
		this.clip.loop(-1);
	}

	private void loadAudio(String pathName) {
		try {
			this.clip = AudioSystem.getClip(); // 비어있는 오디오 클립 만들기
			File audioFile = new File(pathName); // 오디오 파일의 경로명
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); // 오디오 파일로부터
			this.clip.open(audioStream); // 재생할 오디오 스트림 열기
		} catch (LineUnavailableException var4) {
			var4.printStackTrace();
		} catch (UnsupportedAudioFileException var5) {
			var5.printStackTrace();
		} catch (IOException var6) {
			var6.printStackTrace();
		}

	}

	public void audioPause() {
		this.clip.stop(); // 오디오 재생 중단
	}

	public void audioStart() {
		this.clip.start(); // 오디오 재생 시작
	}
}
