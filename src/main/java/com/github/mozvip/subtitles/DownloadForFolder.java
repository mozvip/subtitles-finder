package com.github.mozvip.subtitles;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.MatchResult;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mozvip.subtitles.opensubtitles.OpenSubtitlesHasher;

public class DownloadForFolder {
	
	private final static Logger logger = LoggerFactory.getLogger( DownloadForFolder.class);
	
	public static List<Path> getContents(Path folder, Filter<Path> filter, boolean recursive) throws IOException {
		List<Path> results = new ArrayList<>();
		List<Path> folderResults = new ArrayList<>();
		if (Files.isReadable( folder )) {
			try (DirectoryStream<Path> ds = filter != null ? Files.newDirectoryStream(folder, filter) : Files.newDirectoryStream(folder)) {
				for (Path p : ds) {
					folderResults.add( p );
				}
			}
			for (Path p : folderResults) {
				if (Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS) && recursive) {
					results.addAll( getContents( p, filter, recursive) );
				} else {
					results.add( p );
				}
			}
		}
		return results;
	}

	public static void main(String[] args) throws IOException {
		
		Reflections reflections = new Reflections("com.github.mozvip.subtitles");
		Set<Class<? extends FileHashSubtitlesFinder>> fileHashSubtitlesFinders = reflections.getSubTypesOf(FileHashSubtitlesFinder.class);
		Set<Class<? extends MovieSubtitlesFinder>> movieSubtitlesFinders = reflections.getSubTypesOf(MovieSubtitlesFinder.class);

		Locale locale = Locale.FRENCH;
		
		List<Path> contents = getContents(Paths.get("h:/"), VideoFileFilter.getInstance(), true);
		for (Path path : contents) {
			try {
				logger.info("Searching for subtitles for {}", path.toAbsolutePath().toString());
				
				String fileHash = OpenSubtitlesHasher.computeHash(path);
				long videoByteSize = Files.size( path );
				
				RemoteSubTitles subTitles = null;
				
				for (Class<? extends FileHashSubtitlesFinder> finderClass : fileHashSubtitlesFinders) {
					subTitles = finderClass.newInstance().downloadSubtitlesForFileHash(fileHash, videoByteSize, locale);
				}
				
				if (subTitles == null) {
				
					Scanner sc = new Scanner(path.getFileName().toString());
					String token = sc.findInLine("(.*)[\\.\\s-_](19\\d{2}|20\\d{2})[\\.\\s-_](.*)[\\.]\\w+");
					if ( token != null) {
						MatchResult match = sc.match();
						
						String movieName = match.group(1).replaceAll("\\.", " ");
						int year = Integer.parseInt( match.group(2));
						String release = match.group(3);
						float fps = 25.0f;
						
						for (Class<? extends MovieSubtitlesFinder> finderClass : movieSubtitlesFinders) {
							subTitles = finderClass.newInstance().downloadMovieSubtitles(movieName, year, release, fps, locale);
						}
					}
					
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
