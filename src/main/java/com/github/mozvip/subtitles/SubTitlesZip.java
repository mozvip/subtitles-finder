package com.github.mozvip.subtitles;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubTitlesZip {
	
	private final static Logger LOGGER = LoggerFactory.getLogger( SubTitlesZip.class );
	
	public static int evaluateScore( String subtitleName, Locale locale, String release ) {
		
		if ((StringUtils.containsIgnoreCase(subtitleName, ".VF.") || StringUtils.containsIgnoreCase(subtitleName, ".FR.") || StringUtils.containsIgnoreCase(subtitleName, ".FR-") ) && !locale.getLanguage().equals("fr")) {
			return -1;
		}

		if ((StringUtils.containsIgnoreCase(subtitleName, ".EN.") || StringUtils.containsIgnoreCase(subtitleName, ".EN-") || StringUtils.containsIgnoreCase(subtitleName, ".VO-") || StringUtils.containsIgnoreCase(subtitleName, ".VO.") || StringUtils.containsIgnoreCase(subtitleName, ".VOsync.") ) && !locale.getLanguage().equals("en")) {
			return -1;
		}

		int score = 0;
		// test for release
		if ( release != null ) {
			Release releaseGroup = Release.firstMatch( release );
			if (releaseGroup != null && releaseGroup != Release.UNKNOWN) {
				if ( releaseGroup.match( subtitleName ) ) {
					score = 10;
				}
			}
		}

		if (StringUtils.endsWithIgnoreCase(subtitleName, ".srt")) {
			score += 1;
		}

		LOGGER.info("Evaluated " + subtitleName + " score=" + score);

		return score;
	}
	
	public static RemoteSubTitles selectBestSubtitles(SubtitlesFinder finder, byte[] zipData, String release, Locale locale) throws IOException {
		return selectBestSubtitles(finder, zipData, release, locale, -1, -1);
	}

	public static RemoteSubTitles selectBestSubtitles(SubtitlesFinder finder, byte[] zipData, String release, Locale locale, int season, int episode) throws IOException {
		SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(zipData);
		ZipFile zip = new ZipFile( inMemoryByteChannel );
		try {
	
			ZipArchiveEntry selectedEntry = null;
			int maxScore = -100;
			
			Enumeration<ZipArchiveEntry> entries = zip.getEntries();
			while(entries.hasMoreElements()) {
				ZipArchiveEntry entry = entries.nextElement();
				
				if (entry.isDirectory()) {
					continue;
				}
				
				if (entry.getName().endsWith(".zip")) {
					// FIXME: support nested zips
					continue;
				}
				
				if (season > 0 && !SubTitlesUtils.isMatch(entry.getName(), season, episode)) {
					continue;
				}
	
				int score = evaluateScore( entry.getName(), locale, release );
				
				if (score < 0) {
					continue;
				}
	
				if (selectedEntry == null || score > maxScore) {
					selectedEntry = entry;
				}
				
				if (score > maxScore) {
					maxScore = score;
				}
			}
			
			if ( selectedEntry != null ) {
				
				LOGGER.info("selecting " + selectedEntry.getName() + " (score=" + maxScore + ")");
	
				byte[] data = new byte[ (int) selectedEntry.getSize() ];
				IOUtils.readFully( zip.getInputStream(selectedEntry) , data );
				
				return new RemoteSubTitles(finder, selectedEntry.getName(), data, maxScore );
			}
		} finally {
			zip.close();
		}
		
		return null;
	}

}
