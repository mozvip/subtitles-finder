package com.github.mozvip.subtitles.usub.net;

import java.io.IOException;
import java.util.Locale;

import com.github.mozvip.subtitles.RemoteSubTitles;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mozvip.subtitles.AbstractSubtitleFinderTest;
import com.github.mozvip.subtitles.usub.net.USub;

public class USubTest extends AbstractSubtitleFinderTest {
	
	private static USub finder ;
	
	@BeforeClass
	public static void init() throws Exception {
		finder = new USub();
	}
	
	@Test
	public void testModernFamily() throws IOException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Modern Family", 9, 4, "AMZN", Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && remoteSubTitles.getTitle().contains(".amzn."));
	}

	@Test
	public void testGot() throws IOException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && remoteSubTitles.getTitle().contains(".imm."));
	}

}
