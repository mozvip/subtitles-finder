package com.github.mozvip.subtitles.addic7ed;

import java.io.IOException;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mozvip.subtitles.addic7ed.Addic7ed;

public class Addic7edTest {

	private static Addic7ed finder ;
	
	@BeforeClass
	public static void init() throws Exception {
		finder = new Addic7ed();
	}
	
	@Test
	public void testDownloadSubtitle() throws IOException {
		finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", Locale.FRENCH);
	}

	@Test
	public void testGot() throws IOException {
		finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", Locale.FRENCH);
	}

	@Test
	public void testAmericanCrime() throws IOException {
		finder.downloadEpisodeSubtitle("American Crime", 1, 1, "LOL", Locale.FRENCH);
	}

}
