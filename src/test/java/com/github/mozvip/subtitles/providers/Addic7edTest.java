package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.model.TVShowEpisodeInfo;
import com.github.mozvip.subtitles.model.VideoInfo;
import com.github.mozvip.subtitles.model.VideoNameParser;
import com.github.mozvip.subtitles.model.VideoSource;
import com.github.mozvip.subtitles.providers.Addic7ed;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class Addic7edTest {

	private static Addic7ed finder ;
	
	@BeforeClass
	public static void init() throws Exception {
		finder = new Addic7ed();
	}
	
	@Test
	public void testScreamQueens() throws ExecutionException {
		final RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Scream Queens (2015)", 2, 1, "AVS", VideoSource.WEB_DL, Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles != null && remoteSubTitles.getTitle().contains(".AVS."));
	}

	@Test
	public void testGot() throws ExecutionException {
		final RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Game of Thrones", 2, 1, "ASAP", VideoSource.HDTV,  Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles != null && remoteSubTitles.getTitle().contains(".ASAP."));
	}

	@Test
	public void testShadesOfBlue() throws ExecutionException {
		final RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle("Shades of Blue", 1, 5, "AVS", VideoSource.HDTV, Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles != null && remoteSubTitles.getTitle().contains(".AVS."));
	}

	@Test
	public void testWalking() throws ExecutionException {

		TVShowEpisodeInfo info = (TVShowEpisodeInfo) VideoNameParser.getVideoInfo("The.Walking.Dead.S08E01.REPACK.CONVERT.1080p.WEB.h264-TBS[rarbg].mkv");

		final RemoteSubTitles remoteSubTitles = finder.downloadEpisodeSubtitle(info.getName(), info.getSeason(), info.getFirstEpisode(), info.getRelease(),info.getSource(), Locale.FRENCH);
		Assert.assertTrue( remoteSubTitles != null && remoteSubTitles.getTitle().contains("WEB"));
	}

}
