package com.github.mozvip.subtitles;

public class RemoteSubTitles {

	byte[] data;
	private int score;
	private String title;
	private SubtitlesFinder finder;

	public RemoteSubTitles( SubtitlesFinder finder, String title, byte[] data, int score ) {
		this.finder = finder;
		this.title = title;
		this.data = data;
		this.score = score;
	}

	public SubtitlesFinder getFinder() {
		return finder;
	}
	
	public String getTitle() {
		return title;
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

}
