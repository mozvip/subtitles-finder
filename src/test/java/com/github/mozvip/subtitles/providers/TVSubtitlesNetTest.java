package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.model.VideoSource;
import com.github.mozvip.subtitles.providers.TVSubtitlesNet;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TVSubtitlesNetTest {

	private static TVSubtitlesNet finder ;
	
	@BeforeClass
	public static void init() throws ExecutionException {
		finder = new TVSubtitlesNet();
	}
	
	@Test
	public void testScreamQueens() throws ExecutionException {
		RemoteSubTitles subTitles = finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", VideoSource.HDTV, Locale.GERMAN);
		Assert.assertNotNull(subTitles);
	}

	@Test
	public void testGot() throws ExecutionException {
		RemoteSubTitles subTitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertNotNull(subTitles);
	}

	@Test
	public void testFeud() throws ExecutionException {
		RemoteSubTitles subTitles = finder.downloadEpisodeSubtitle("Feud", 1, 4, "FLEET", VideoSource.HDTV, Locale.forLanguageTag("hu"));
		Assert.assertNotNull(subTitles);
	}

}
