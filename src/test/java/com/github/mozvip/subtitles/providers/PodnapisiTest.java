package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.github.mozvip.subtitles.model.VideoSource;
import com.github.mozvip.subtitles.providers.Podnapisi;
import org.junit.Test;

public class PodnapisiTest {

	private static Podnapisi finder = new Podnapisi();

	@Test
	public void testDownloadSubtitle() throws ExecutionException {
		finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", VideoSource.HDTV, Locale.FRENCH);
	}

	@Test
	public void testGot() throws ExecutionException {
		finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", VideoSource.HDTV, Locale.FRENCH);
	}

	@Test
	public void testAmericanCrime() throws ExecutionException {
		finder.downloadEpisodeSubtitle("American Crime", 1, 1, "LOL", VideoSource.HDTV, Locale.FRENCH);
	}

}
