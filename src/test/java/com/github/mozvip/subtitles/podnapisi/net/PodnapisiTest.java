package com.github.mozvip.subtitles.podnapisi.net;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import com.github.mozvip.subtitles.podnapisi.net.Podnapisi;

public class PodnapisiTest {

	private static Podnapisi finder = new Podnapisi();

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
