package fr.mozvip.subtitles;

import java.util.Locale;

public interface MovieSubtitlesFinder {
	
	public RemoteSubTitles downloadMovieSubtitles(String movieName, int year, String release, float fps, Locale locale) throws Exception;
	

}
