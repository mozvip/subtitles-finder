package com.github.mozvip.subtitles.tvsubtitles.net;

import java.io.IOException;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mozvip.subtitles.tvsubtitles.net.TVSubtitlesNet;

public class TVSubtitlesNetTest {

	private static TVSubtitlesNet finder ;
	
	@BeforeClass
	public static void init() throws Exception {
		finder = new TVSubtitlesNet();
	}
	
	@Test
	public void testDownloadSubtitle() throws IOException {
		finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", Locale.FRENCH);
	}

	@Test
	public void testGot() throws IOException {
		finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", Locale.FRENCH);
	}

}
