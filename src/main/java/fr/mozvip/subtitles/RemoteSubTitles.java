package fr.mozvip.subtitles;

public class RemoteSubTitles {

	byte[] data;
	private int score;
	
	public RemoteSubTitles() {
	}
	
	public RemoteSubTitles( byte[] data, int score ) {
		this.data = data;
		this.score = score;
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
