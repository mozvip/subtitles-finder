package com.github.mozvip.subtitles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import com.github.mozvip.subtitles.model.VideoSource;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mozvip.subtitles.model.Release;

public class SubTitlesZip {
	
	private final static Logger LOGGER = LoggerFactory.getLogger( SubTitlesZip.class );

	private SubTitlesZip() {}

	public static RemoteSubTitles selectBestSubtitlesFromZip(SubtitlesFinder finder, byte[] zipData, String release, VideoSource videoSource, Locale locale) throws IOException {
		return selectBestSubtitlesFromZip(finder, zipData, release, videoSource, locale, -1, -1);
	}

	public static List<String> listofFileNames(byte[] zipData) throws IOException {
		SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(zipData);
		List<String> filesNames = new ArrayList<>();
		try (ZipFile zip = new ZipFile(inMemoryByteChannel)) {
			Enumeration<ZipArchiveEntry> entries = zip.getEntries();
			while(entries.hasMoreElements()) {
				ZipArchiveEntry entry = entries.nextElement();
				if (entry.isDirectory() || entry.getSize() == 0) {
					continue;
				}
				filesNames.add( entry.getName());
			}
		}
		return filesNames;
	}

	public static RemoteSubTitles firstFromZipFile(SubtitlesFinder finder, byte[] zipData, int score) throws IOException {
		SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(zipData);
		try (ZipFile zip = new ZipFile(inMemoryByteChannel)) {
			ZipArchiveEntry selectedEntry = null;

			Enumeration<ZipArchiveEntry> entries = zip.getEntries();
			while(entries.hasMoreElements()) {
				ZipArchiveEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				if (entry.getName().endsWith(".srt")) {
					selectedEntry = entry;
					break;
				}
			}
			if ( selectedEntry != null ) {
				byte[] data = new byte[ (int) selectedEntry.getSize() ];
				IOUtils.readFully( zip.getInputStream(selectedEntry) , data );
				return new RemoteSubTitles(finder, selectedEntry.getName(), data, score );
			}
		}

		return null;
	}

	public static RemoteSubTitles selectBestSubtitlesFromZip(SubtitlesFinder finder, byte[] zipData, String release, VideoSource videoSource, Locale locale, int season, int episode) throws IOException {
		SeekableInMemoryByteChannel inMemoryByteChannel = new SeekableInMemoryByteChannel(zipData);
		ZipFile zip;
		try {
			zip = new ZipFile( inMemoryByteChannel );
		} catch (Exception e) {

			LOGGER.error("Downloaded file is not a zip file :\n{}",  new String(zipData) );
			
			return null;

		}
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
	
				int score = SubTitleEvaluator.evaluateSubtitleForRelease(finder, entry.getName(), locale, release, videoSource );
				
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
