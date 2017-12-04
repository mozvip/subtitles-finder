package com.github.mozvip.subtitles.soustitres.eu;

import java.io.IOException;
import java.util.Locale;

import com.github.mozvip.subtitles.RemoteSubTitles;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mozvip.subtitles.AbstractSubtitleFinderTest;
import com.github.mozvip.subtitles.soustitres.eu.SousTitresEU;

public class SousTitresEUTest extends AbstractSubtitleFinderTest {

	private static SousTitresEU finder ;
	
	@BeforeClass
	public static void init() throws Exception {
		finder = new SousTitresEU();
	}
	
	@Test
	public void testScreamQueens() throws IOException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && StringUtils.containsIgnoreCase(remoteSubTitles.getTitle(), "Netflix"));
	}

	@Test
	public void testLeftovers() throws IOException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("The Leftovers", 3, 1, "KILLERS", Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && remoteSubTitles.getTitle().contains("KILLERS"));
	}

	@Test
	public void testGot() throws IOException {
		RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "IMMERSE", Locale.FRENCH);
		Assert.assertTrue(remoteSubTitles != null && StringUtils.containsIgnoreCase(remoteSubTitles.getTitle(), ".imm."));
	}

}
