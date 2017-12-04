package com.github.mozvip.subtitles.betaseries;

import java.io.IOException;
import java.util.Locale;

import com.github.mozvip.subtitles.SubTitleFinderFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mozvip.subtitles.RemoteSubTitles;

public class BetaSeriesTest {

	private static BetaSeries finder;

	@BeforeClass
	public static void init() throws Exception {
		Assert.assertTrue(System.getenv("BetaSeries.login") != null);
		Assert.assertTrue(System.getenv("BetaSeries.password") != null);
		finder = SubTitleFinderFactory.createInstance(BetaSeries.class);
	}

	@Test
	public void testDownloadSubtitle() throws IOException {
		RemoteSubTitles subtitles = finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", Locale.FRENCH);
		Assert.assertTrue(subtitles.getTitle().contains("AVS"));
	}

	@Test
	public void testGot() throws IOException {
		RemoteSubTitles subtitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", Locale.FRENCH);
		Assert.assertTrue(subtitles.getTitle().contains(".imm."));
	}

}
