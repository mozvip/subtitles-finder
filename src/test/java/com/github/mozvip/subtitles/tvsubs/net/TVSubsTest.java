package com.github.mozvip.subtitles.tvsubs.net;

import java.io.IOException;
import java.util.Locale;

import com.github.mozvip.subtitles.RemoteSubTitles;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TVSubsTest {

	static TVSubs tvs;

	@BeforeClass
	public final static void init() throws IOException {
		tvs = new TVSubs();
	}

	@Test
	public void testDownloadEpisodeSubtitle() throws IOException {
		RemoteSubTitles remoteSubTitles = tvs.downloadEpisodeSubtitle("Game Of Thrones", 6, 4, "FLEET", Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles.getTitle().contains(".FLEET."));
	}

}
