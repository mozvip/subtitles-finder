package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.model.VideoSource;
import com.github.mozvip.subtitles.providers.Podnapisi;
import org.junit.Assert;
import org.junit.Test;

public class PodnapisiTest {

	private static Podnapisi finder = new Podnapisi();

	@Test
	public void testDownloadSubtitle() throws ExecutionException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && remoteSubTitles.getTitle().contains("AVS"));
	}

	@Test
	public void testGot() throws ExecutionException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null);
	}

	@Test
	public void testAmericanCrime() throws ExecutionException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("American Crime", 1, 1, "LOL", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && remoteSubTitles.getTitle().contains("LOL"));
	}

	@Test
	public void testMovie() throws Exception {
		RemoteSubTitles remoteSubTitles = finder.downloadMovieSubtitles("Girl Lost", 2018, "AMZN", VideoSource.WEB_DL, BigDecimal.valueOf(23.976), Locale.ENGLISH);
		Assert.assertTrue(remoteSubTitles != null);
	}


}
