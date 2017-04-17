package com.github.mozvip.subtitles.isubtitles;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Test;

public class ISubtitlesNetTest {
	
	private ISubtitlesNet test = new ISubtitlesNet();

	@Test
	public void test() throws Exception {
		test.downloadMovieSubtitles("Aquarius", 2016, "ETHD", BigDecimal.valueOf(25.0), Locale.FRENCH);
	}

}
