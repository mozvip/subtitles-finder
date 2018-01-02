package com.github.mozvip.subtitles.model;

public class MovieInfo extends VideoInfo {
	
	private int year;

	public MovieInfo( String name, int year, VideoQuality quality, VideoSource source, String extraNameData ) {
		super( name, quality, source, extraNameData );
		this.year = year;
	}

	public int getYear() {
		return year;
	}

}
