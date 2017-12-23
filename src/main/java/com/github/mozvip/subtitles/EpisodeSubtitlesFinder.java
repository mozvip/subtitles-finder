package com.github.mozvip.subtitles;

import com.github.mozvip.subtitles.model.VideoSource;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public interface EpisodeSubtitlesFinder {

	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, VideoSource source, Locale locale) throws ExecutionException;

}
