package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.model.VideoSource;
import com.github.mozvip.subtitles.providers.TVSubs;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TVSubsTest {

	static TVSubs tvs;

	@BeforeClass
	public final static void init() throws ExecutionException {
		tvs = new TVSubs();
	}

	@Test
	public void testDownloadEpisodeSubtitle() throws ExecutionException {
		RemoteSubTitles remoteSubTitles = tvs.downloadEpisodeSubtitle("Game Of Thrones", 6, 4, "FLEET", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles.getTitle().contains(".FLEET."));
	}

	@Test
	public void testDetroitersBR() throws ExecutionException {
		RemoteSubTitles remoteSubTitles = tvs.downloadEpisodeSubtitle("Detroiters", 1, 6, "MOROSE", VideoSource.WEB_DL, Locale.forLanguageTag("br"));
		Assert.assertTrue( remoteSubTitles.getTitle().contains("MOROSE"));
	}

}
