package fr.mozvip.subtitles.betaseries;

import java.io.IOException;
import java.util.Locale;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.mozvip.subtitles.RemoteSubTitles;

public class BetaSeriesTest {

	private static BetaSeries finder ;
	
	@BeforeClass
	public static void init() throws Exception {
		finder = new BetaSeries("TEST_LOGIN", "TEST_PASSWORD");
	}
	
	@Test
	public void testDownloadSubtitle() throws IOException {
		RemoteSubTitles subtitles = finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", Locale.FRENCH);
		Assert.assertNotNull( subtitles );
	}

	@Test
	public void testGot() throws IOException {
		RemoteSubTitles subtitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", Locale.FRENCH);
		Assert.assertNotNull( subtitles );
	}

}
