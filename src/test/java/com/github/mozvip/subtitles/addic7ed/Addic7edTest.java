package com.github.mozvip.subtitles.addic7ed;

import java.io.IOException;
import java.util.Locale;

import com.github.mozvip.subtitles.RemoteSubTitles;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mozvip.subtitles.addic7ed.Addic7ed;

public class Addic7edTest {

	private static Addic7ed finder ;
	
	@BeforeClass
	public static void init() throws Exception {
		finder = new Addic7ed();
	}
	
	@Test
	public void testScreamQueens() throws IOException {
		final RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles != null && remoteSubTitles.getTitle().contains(".AVS."));
	}

	@Test
	public void testGot() throws IOException {
		final RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "ASAP", Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles != null && remoteSubTitles.getTitle().contains(".ASAP."));
	}

	@Test
	public void testAmericanCrime() throws IOException {
		final RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Shades of Blue", 1, 5, "AVS", Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles != null && remoteSubTitles.getTitle().contains(".AVS."));
	}

}
