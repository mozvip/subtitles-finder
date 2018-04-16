package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.model.VideoSource;
import com.github.mozvip.subtitles.providers.SousTitresEU;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mozvip.subtitles.AbstractSubtitleFinderTest;

public class SousTitresEUTest extends AbstractSubtitleFinderTest {

	private static SousTitresEU finder ;
	
	@BeforeClass
	public static void init() throws Exception {
		finder = new SousTitresEU();
	}
	
	@Test
	public void testScreamQueens() throws ExecutionException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && StringUtils.containsIgnoreCase(remoteSubTitles.getTitle(), "Netflix"));
	}

	@Test
	public void testLeftovers() throws ExecutionException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("The Leftovers", 3, 1, "KILLERS", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && remoteSubTitles.getTitle().contains("KILLERS"));
	}

	@Test
	public void testGot() throws ExecutionException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && StringUtils.containsIgnoreCase(remoteSubTitles.getTitle(), ".imm."));
	}

	@Test
	public void testMovie() throws ExecutionException {
	    String release = "GECKOS";
        RemoteSubTitles remoteSubTitles = finder.downloadMovieSubtitles("Wind River", 2017, release, VideoSource.BLURAY, new BigDecimal(25.0f), Locale.FRENCH);
        Assert.assertTrue(remoteSubTitles != null && StringUtils.containsIgnoreCase(remoteSubTitles.getTitle(), release));
	}

	@Test
	public void testNotFound() throws Exception {
		RemoteSubTitles remoteSubTitles = finder.downloadMovieSubtitles("thismoviedoesnotexist", 2017, "FOV", VideoSource.HDTV, BigDecimal.valueOf(25.0), Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles == null);
	}

	@Test
	public void testShowNotFound() throws Exception {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("thisshowdoesnotexist", 1, 1, "IMMERSE", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles == null);
	}

	@Test
	public void testUnsupportedLanguage() throws Exception {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", VideoSource.HDTV, Locale.CHINESE);
		Assert.assertTrue( remoteSubTitles == null);
	}

}
