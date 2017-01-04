package com.github.mozvip.subtitles;

import java.io.IOException;
import java.util.Locale;

public interface EpisodeSubtitlesFinder {

	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, Locale locale) throws IOException;

}
