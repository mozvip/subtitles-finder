package com.github.mozvip.subtitles.providers;

import java.math.BigDecimal;
import java.util.Locale;

import com.github.mozvip.subtitles.RemoteSubTitles;
import org.junit.Assert;
import org.junit.Test;

public class ISubtitlesINTest {
	
	private ISubtitlesIN test = new ISubtitlesIN();

	@Test
	public void testAquarius() throws Exception {
		RemoteSubTitles remoteSubTitles = test.downloadMovieSubtitles("Aquarius", 2016, "FOXM", BigDecimal.valueOf(25.0), Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles != null);
	}

	@Test
	public void testPoldark() throws Exception {
		RemoteSubTitles remoteSubTitles = test.downloadMovieSubtitles("Poldark", 2015, "FOV", BigDecimal.valueOf(25.0), Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles != null);
	}

	@Test
	public void testNotFound() throws Exception {
		RemoteSubTitles remoteSubTitles = test.downloadMovieSubtitles("thismoviedoesnotexist", 2017, "FOV", BigDecimal.valueOf(25.0), Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles == null);
	}

}
