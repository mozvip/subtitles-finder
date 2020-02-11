package com.github.mozvip.subtitles.providers;

import com.github.mozvip.subtitles.utils.RemoteSubTitles;
import com.github.mozvip.subtitles.model.VideoSource;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

public interface EpisodeSubtitlesFinder {

	RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, VideoSource source, Locale locale) throws InterruptedException, ExecutionException;

}
