package fr.mozvip.subtitles.usub.net;

import java.io.IOException;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.mozvip.subtitles.AbstractSubtitleFinderTest;

public class USubTest extends AbstractSubtitleFinderTest {
	
	private static USub finder ;
	
	@BeforeClass
	public static void init() throws Exception {
		finder = new USub();
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
