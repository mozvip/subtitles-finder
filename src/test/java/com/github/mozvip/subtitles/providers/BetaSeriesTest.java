package com.github.mozvip.subtitles.providers;

import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.SubTitleFinderFactory;
import com.github.mozvip.subtitles.model.VideoSource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class BetaSeriesTest {

	private static BetaSeries finder;

	@BeforeClass
	public static void init() throws Exception {
		Assert.assertTrue(System.getenv("BetaSeries_login") != null);
		Assert.assertTrue(System.getenv("BetaSeries_password") != null);
		finder = SubTitleFinderFactory.createInstance(BetaSeries.class);
	}

	@Test
	public void testDownloadSubtitle() throws ExecutionException {
		RemoteSubTitles subtitles = finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", VideoSource.HDTV, Locale.FRENCH);
        Assert.assertTrue(subtitles.getTitle().contains("AVS"));
	}

	@Test
	public void testGot() throws ExecutionException {
		RemoteSubTitles subtitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue(subtitles.getTitle().contains("-IMMER"));
	}

	@Test
	public void testGotEn() throws ExecutionException {
		RemoteSubTitles subtitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", VideoSource.HDTV, Locale.ENGLISH);
		Assert.assertTrue(subtitles.getTitle().contains("-IMMER"));
	}

}
