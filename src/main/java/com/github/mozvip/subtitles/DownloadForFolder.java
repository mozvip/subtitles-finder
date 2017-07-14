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
		Set<Class<? extends EpisodeSubtitlesFinder>> episodeSubtitlesFinders= reflections.getSubTypesOf(EpisodeSubtitlesFinder.class);

		Locale locale = Locale.FRENCH;
		
		List<Path> contents = getContents(Paths.get("\\\\DLINK-4T\\Volume_1\\series\\Broadchurch\\Season 03"), VideoFileFilter.getInstance(), true);
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
					
					VideoInfo videoInfo = VideoNameParser.getVideoInfo(path);
					
					if (videoInfo == null) {
						continue;
					}
					
					if (videoInfo instanceof TVShowEpisodeInfo) {
						
						TVShowEpisodeInfo episodeInfo = (TVShowEpisodeInfo) videoInfo;
						
						for (Class<? extends EpisodeSubtitlesFinder> finderClass : episodeSubtitlesFinders) {
							try {
								subTitles = SubTitleFinderFactory.createEpisodeSubtitlesFinder(finderClass).downloadEpisodeSubtitle(episodeInfo.getName(), episodeInfo.getSeason(), episodeInfo.getFirstEpisode(), episodeInfo.getRelease(), locale);
								if (subTitles != null) {
									break;
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					} else if (videoInfo instanceof MovieInfo) {
						
						MovieInfo movieInfo = (MovieInfo) videoInfo;
						
						BigDecimal fps = BigDecimal.valueOf( 25.0d );
						
						for (Class<? extends MovieSubtitlesFinder> finderClass : movieSubtitlesFinders) {
							subTitles = SubTitleFinderFactory.createMovieSubtitlesFinder( finderClass).downloadMovieSubtitles(movieInfo.getName(), movieInfo.getYear(), movieInfo.getRelease(), fps, locale);
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
