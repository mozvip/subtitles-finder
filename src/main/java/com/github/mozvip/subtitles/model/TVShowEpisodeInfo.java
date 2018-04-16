package com.github.mozvip.subtitles.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TVShowEpisodeInfo extends VideoInfo {
	
	private int season;
	private int firstEpisode;
	private int lastEpisode;

	public TVShowEpisodeInfo(String name, int season, int firstEpisode, int lastEpisode, VideoQuality quality, VideoSource source, String extraNameData ) {
		super(name, quality, source, extraNameData);
		this.season = season;
		this.firstEpisode = firstEpisode;
		this.lastEpisode = lastEpisode;
	}

	public int getSeason() {
		return season;
	}
	public void setSeason(int season) {
		this.season = season;
	}
	public int getFirstEpisode() {
		return firstEpisode;
	}
	public int getLastEpisode() {
		return lastEpisode;
	}

}
