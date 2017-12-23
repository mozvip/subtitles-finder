package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.github.mozvip.subtitles.model.VideoSource;
import com.github.mozvip.subtitles.providers.TVSubtitlesNet;
import org.junit.BeforeClass;
import org.junit.Test;

public class TVSubtitlesNetTest {

	private static TVSubtitlesNet finder ;
	
	@BeforeClass
	public static void init() throws ExecutionException {
		finder = new TVSubtitlesNet();
	}
	
	@Test
	public void testDownloadSubtitle() throws ExecutionException {
		finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", VideoSource.HDTV, Locale.FRENCH);
	}

	@Test
	public void testGot() throws ExecutionException {
		finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", VideoSource.HDTV, Locale.FRENCH);
	}

}
