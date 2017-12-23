package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.model.VideoSource;
import com.github.mozvip.subtitles.providers.USub;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mozvip.subtitles.AbstractSubtitleFinderTest;

public class USubTest extends AbstractSubtitleFinderTest {
	
	private static USub finder ;
	
	@BeforeClass
	public static void init() throws Exception {
		finder = new USub();
	}
	
	@Test
	public void testModernFamily() throws ExecutionException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Modern Family", 9, 4, "AMZN", VideoSource.WEB_DL, Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && remoteSubTitles.getTitle().contains(".amzn."));
	}

	@Test
	public void testGot() throws ExecutionException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && remoteSubTitles.getTitle().contains(".imm."));
	}

}
