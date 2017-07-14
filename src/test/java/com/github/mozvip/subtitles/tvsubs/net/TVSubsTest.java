package com.github.mozvip.subtitles.tvsubs.net;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

public class TVSubsTest {
	
	@Test
	public void testDownloadEpisodeSubtitle() throws IOException {
		TVSubs tvs = new TVSubs();

		tvs.downloadEpisodeSubtitle("Game Of Thrones", 6, 4, "FLEET", Locale.FRENCH);
	}

}
