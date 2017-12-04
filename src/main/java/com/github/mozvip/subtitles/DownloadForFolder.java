package com.github.mozvip.subtitles;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mozvip.subtitles.model.MovieInfo;
import com.github.mozvip.subtitles.model.TVShowEpisodeInfo;
import com.github.mozvip.subtitles.model.VideoInfo;
import com.github.mozvip.subtitles.model.VideoNameParser;
import com.github.mozvip.subtitles.opensubtitles.OpenSubtitlesHasher;

public class DownloadForFolder {
	
	private final static Logger LOGGER = LoggerFactory.getLogger( DownloadForFolder.class);

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

		if (args.length != 2) {
			System.err.println("Usage : path language");
			System.exit(-1);
		}

		SubtitleDownloader downloader = new SubtitleDownloader();

		Locale locale = Locale.forLanguageTag(args[1]);
		
		List<Path> contents = getContents(Paths.get(args[0]), VideoFileFilter.getInstance(), true);
		for (Path path : contents) {
			try {
				if (downloader.findSubtitlesFor(path, locale))
					continue;
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
	}

}
