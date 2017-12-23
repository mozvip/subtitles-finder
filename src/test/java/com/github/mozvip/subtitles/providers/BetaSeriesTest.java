package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.github.mozvip.subtitles.SubTitleFinderFactory;
import com.github.mozvip.subtitles.model.VideoSource;
import com.github.mozvip.subtitles.providers.BetaSeries;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mozvip.subtitles.RemoteSubTitles;

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
		Assert.assertTrue(subtitles.getTitle().contains(".imm."));
	}

}
