package com.github.mozvip.subtitles;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class SubTitlesUtils {

	private SubTitlesUtils() {}
	
	public static List<String> getSeasonEpisodeMatchList( int season, int episode ) {
		
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setMinimumIntegerDigits(2);
		
		List<String> allMatches = new ArrayList<String>();
		allMatches.add( "" + season + "×" + nf.format( episode ) );
		allMatches.add( "" + season + "x" + nf.format( episode ) );
		allMatches.add( String.format("S%02dE%02d", season, episode ));
		allMatches.add( "" +  nf.format( season ) + "x" + nf.format( episode ) );
		
		return allMatches;
	}
	
	public static List<String> getSeasonMatchList( int season ) {
		
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setMinimumIntegerDigits(2);
		
		List<String> allMatches = new ArrayList<String>();
		allMatches.add( ".S" + season + "." );
		allMatches.add( ".S0" + season + "." );
		allMatches.add( "S" + season );
		
		return allMatches;
	}

	public static boolean isMatch( String fileName, int season, int episode ) {
		List<String> matches = getSeasonEpisodeMatchList( season, episode );
		return matches.stream().anyMatch(element -> fileName.contains(element));
	}
	
	public static boolean isExactMatch( String fileName, int season, int episode ) {
		List<String> matches = getSeasonEpisodeMatchList( season, episode );
		return matches.contains(fileName);
	}

	public static boolean isSeasonMatch( String fileName, int season ) {
		List<String> matches = getSeasonMatchList( season );
		return matches.contains(fileName);
	}

}
