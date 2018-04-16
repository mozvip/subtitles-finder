package com.github.mozvip.subtitles;

import com.github.mozvip.subtitles.model.VideoSource;

import java.math.BigDecimal;
import java.util.Locale;

public interface MovieSubtitlesFinder {
	
	public RemoteSubTitles downloadMovieSubtitles(String movieName, int year, String release, VideoSource source, BigDecimal fps, Locale locale) throws Exception;
	

}
