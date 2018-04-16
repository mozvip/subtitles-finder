package com.github.mozvip.subtitles.model;

public class VideoInfo {
	
	private String name;
	private VideoQuality quality;
	private String release;
	private VideoSource source;
	
	private String extraNameData;

	public VideoInfo( String name, VideoQuality quality, VideoSource source, String extraNameData ) {
		super();
		this.name = name;
		this.quality = quality;
		this.source = source;
		this.extraNameData = extraNameData;
		parseExtraData();
	}

	protected void parseExtraData() {
		source = VideoSource.findMatch( extraNameData );
		Release release = Release.firstMatch(extraNameData);
		this.release = release != null ? release.name() : null;
		if (quality == null) {
			quality = VideoQuality.findMatch( extraNameData );
		}
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public VideoQuality getQuality() {
		return quality;
	}

	public String getRelease() {
		return release;
	}

	public VideoSource getSource() {
		return source;
	}

}
