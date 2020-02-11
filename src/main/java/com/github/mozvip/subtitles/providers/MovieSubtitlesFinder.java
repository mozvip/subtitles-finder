package com.github.mozvip.subtitles.providers;

import com.github.mozvip.subtitles.utils.RemoteSubTitles;
import com.github.mozvip.subtitles.model.VideoSource;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public interface MovieSubtitlesFinder {
	
	RemoteSubTitles downloadMovieSubtitles(String movieName, int year, String release, VideoSource source, BigDecimal fps, Locale locale) throws InterruptedException, ExecutionException;
	

}
