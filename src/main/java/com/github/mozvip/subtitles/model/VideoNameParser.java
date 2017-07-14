package com.github.mozvip.subtitles.model;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import com.github.mozvip.subtitles.RegExp;

public class VideoNameParser {

	private final static String NAME_REGEXP = "[é!&,'\\[\\]\\w\\s\\.\\d-:½]+";
	public final static String SEPARATOR_REGEXP = "[\\s\\.]+";
	
	private static String[] filters = new String[] {
			"(.*)" + SEPARATOR_REGEXP + "READNFO" + SEPARATOR_REGEXP + "(.*)",
			"(.*)" + SEPARATOR_REGEXP + "FANSUB" + SEPARATOR_REGEXP + "(.*)",
			"(.*)" + SEPARATOR_REGEXP + "UNRATED" + SEPARATOR_REGEXP + "(.*)",
			"(.*)" + SEPARATOR_REGEXP + "FASTSUB" + SEPARATOR_REGEXP + "(.*)",
			"(.*)" + SEPARATOR_REGEXP + "REPACK" + SEPARATOR_REGEXP + "(.*)",
			"(.*)" + SEPARATOR_REGEXP + "Theatrical\\s+Cut" + SEPARATOR_REGEXP + "(.*)"
	};
	
	public static String clean( String title, String[] filtersRegExps ) {
		
		title = title.replace('_', ' ');
		title = title.replaceAll("\\s+", " ");

		boolean mustContinue = true;
		while (mustContinue) {
			mustContinue = false;
			for (String filter : filtersRegExps) {
				String[] groups = RegExp.parseGroups( title,  filter );
				if (groups != null) {
					title = StringUtils.join( groups, " ");
					mustContinue = true;
				}
			}
		}
	
		return title;
	}
	
	protected static VideoInfo getVideoInfo( String title ) {

		title = clean( title, filters );

		String[] groups = RegExp.parseGroups(title, "(" + NAME_REGEXP + ")" + SEPARATOR_REGEXP + "(S\\d{2}E\\d{2}E\\d{2})(.*)");
		if (groups != null) {
			String name = getName( groups[0] ); 
			String seasonEpisode = groups[1];
			
			String[] episodeDetails = RegExp.parseGroups( seasonEpisode, "S(\\d{2})E(\\d{2})E(\\d{2})");
			return new TVShowEpisodeInfo( name, Integer.parseInt( episodeDetails[0]), Integer.parseInt( episodeDetails[1]), Integer.parseInt( episodeDetails[2]), groups[2] );
		} 

		groups = RegExp.parseGroups(title, "(" + NAME_REGEXP + ")" + SEPARATOR_REGEXP + "(S\\d{2}E\\d{2})(.*)");
		if (groups != null) {
			String name = getName( groups[0] ); 
			String seasonEpisode = groups[1];

			String[] episodeDetails = RegExp.parseGroups( seasonEpisode, "S(\\d{2})E(\\d{2})");
			return new TVShowEpisodeInfo( name, Integer.parseInt( episodeDetails[0]), Integer.parseInt( episodeDetails[1]), groups[2] );
		}
		
		groups = RegExp.parseGroups(title, "(" + NAME_REGEXP + ")" + SEPARATOR_REGEXP + "(\\d{1}X\\d{2})(.*)");
		if (groups != null) {
			String name = getName( groups[0] ); 
			String seasonEpisode = groups[1];
			
			String[] episodeDetails = RegExp.parseGroups( seasonEpisode, "(\\d{1})X(\\d{2})");
			return new TVShowEpisodeInfo( name, Integer.parseInt( episodeDetails[0]), Integer.parseInt( episodeDetails[1]), groups[2] );
		} 
		
		groups = RegExp.parseGroups(title, "(" + NAME_REGEXP + ")" + SEPARATOR_REGEXP + "S(\\d{1,2})\\s+-\\s+(\\d{1,2})\\s+(.*)");
		if (groups != null) {
			String name = getName( groups[0] ); 
			return new TVShowEpisodeInfo( name, Integer.parseInt( groups[1]), Integer.parseInt( groups[2]), groups[3] );
		} 

		groups = RegExp.parseGroups(title, "(" + NAME_REGEXP + ")" + SEPARATOR_REGEXP + "(\\d{1})(\\d{2})\\.(.*)");
		if (groups != null) {
			String name = getName( groups[0] );
			return new TVShowEpisodeInfo( name, Integer.parseInt( groups[1]), Integer.parseInt( groups[2]), groups[3] );
		}

		groups = RegExp.parseGroups(title, "(" + NAME_REGEXP + ")" + SEPARATOR_REGEXP + "\\(?(19\\d{2}|20\\d{2})\\)?(.*)");
		if (groups != null) {
			String name = getName( groups[0] );
			return new MovieInfo( name, Integer.parseInt( groups[1]), groups[2] );
		}

		groups = RegExp.parseGroups(title, "(" + NAME_REGEXP + ")" + SEPARATOR_REGEXP + "(\\d{4}[\\.\\s]{1})(.*)");
		if (groups != null) {
			String name = getName( groups[0] );
			return new MovieInfo( name, Integer.parseInt( groups[1].substring(0,  4)), groups[2] );
		}

		groups = RegExp.parseGroups(title, "(" + NAME_REGEXP + ")" + SEPARATOR_REGEXP + "(\\d{3})\\D{1}(.*)");
		if (groups != null) {
			String name = getName( groups[0] ); 
			String seasonEpisode = groups[1];

			String[] episodeDetails = RegExp.parseGroups( seasonEpisode, "(\\d{1})(\\d{2})");
			return new TVShowEpisodeInfo( name, Integer.parseInt( episodeDetails[0]), Integer.parseInt( episodeDetails[1]), groups[2] );
		}
		
		groups = RegExp.parseGroups(title, "(" + NAME_REGEXP + ")" + SEPARATOR_REGEXP + "(.*)" + SEPARATOR_REGEXP + "(\\d{4})$");
		if (groups != null) {
			String name = getName( groups[0] );
			return new MovieInfo( name, Integer.parseInt( groups[2]), groups[1] );
		}		

		return null;

	}
	
	public static VideoInfo getVideoInfo( Path path ) {
		String fileNameWithoutExtension = path.getFileName().toString();
		if (Files.isRegularFile( path )) {
			fileNameWithoutExtension = fileNameWithoutExtension.substring(0, fileNameWithoutExtension.lastIndexOf('.'));
		}
		VideoInfo info = getVideoInfo( fileNameWithoutExtension );
		if (info == null && Files.isRegularFile( path ) && path.getParent() != null) {
			// sometimes the parent folder is correctly named
			Path parent = path.getParent();
			if (parent.getFileName() != null) { // I don't understand why this happens 
				info = getVideoInfo( parent.getFileName().toString() );
			}
		}

		if ( info != null && info.getName() != null ) {
			info.setName( info.getName().trim() );
		}
		
		return info;
	}

	private static String getName(String string) {
		String name = string;
		name = name.replace('.', ' ');
		name = name.replace('_', ' ');
		
		name = RegExp.keepOnlyGroups(name, "(.*)" + SEPARATOR_REGEXP + "bluray(.*)");
		name = RegExp.keepOnlyGroups(name, "(.*)" + SEPARATOR_REGEXP + "french(.*)");
		name = RegExp.keepOnlyGroups(name, "(.*)" + SEPARATOR_REGEXP + "x264(.*)");
		return name;
	}

	public static VideoQuality getQuality( String title ) {
		VideoQuality quality = VideoQuality.findMatch( title );
		return quality != null ? quality : VideoQuality.SD;
	}
	
	public static MovieInfo getMovieInfo( String string ) {

		string = clean( string, filters );
		// this method is called when we know that the file is a Movie file
		VideoInfo info = getVideoInfo( string );
		if (info instanceof MovieInfo) {
			return (MovieInfo) info;
		}
		return null;

	}	

	public static MovieInfo getMovieInfo(Path path) {
		
		// this method is called when we know that the file is a Movie file

		VideoInfo info = getVideoInfo(path);
		if (info instanceof MovieInfo) {
			return (MovieInfo) info;
		}

		String fileNameWithoutExtension = path.getFileName().toString();
		if (Files.isRegularFile( path )) {
			fileNameWithoutExtension = fileNameWithoutExtension.substring(0, fileNameWithoutExtension.lastIndexOf('.'));			
		}
		String title = clean( fileNameWithoutExtension, filters );
		return new MovieInfo( title, null );
	}

}
