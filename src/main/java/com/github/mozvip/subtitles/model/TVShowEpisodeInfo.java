package com.github.mozvip.subtitles.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TVShowEpisodeInfo extends VideoInfo {
	
	private int season;
	private int firstEpisode;
	private int lastEpisode;

	public TVShowEpisodeInfo(String name, int season, int firstEpisode, int lastEpisode, VideoQuality quality, String extraNameData ) {
		super(name, quality, extraNameData);
		this.season = season;
		this.firstEpisode = firstEpisode;
		this.lastEpisode = lastEpisode;
	}
	
	public TVShowEpisodeInfo(String name, int season, int episode, VideoQuality quality, String extraNameData ) {
		this(name, season, episode, episode, quality, extraNameData);
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

	public Collection<Integer> getEpisodes() {
		List<Integer> episodes = new ArrayList<Integer>();
		for (int i=firstEpisode; i<=lastEpisode; i++) {
			episodes.add( i );
		}
		return episodes;
	}

}
