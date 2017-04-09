package com.github.mozvip.subtitles;

import java.math.BigDecimal;
import java.util.Locale;

public interface MovieSubtitlesFinder {
	
	public RemoteSubTitles downloadMovieSubtitles(String movieName, int year, String release, BigDecimal fps, Locale locale) throws Exception;
	

}
