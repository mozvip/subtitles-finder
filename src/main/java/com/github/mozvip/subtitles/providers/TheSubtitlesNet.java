package com.github.mozvip.subtitles.providers;

import com.github.mozvip.subtitles.utils.RemoteSubTitles;
import com.github.mozvip.subtitles.utils.SubtitlesFinder;
import com.github.mozvip.subtitles.model.VideoSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TheSubtitlesNet extends SubtitlesFinder implements EpisodeSubtitlesFinder, MovieSubtitlesFinder {

    private static final String ROOT_URL = "http://thesubtitles.net/";

    private final static Logger LOGGER = LoggerFactory.getLogger( TheSubtitlesNet.class );


    @Override
    public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, VideoSource source, Locale locale) throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public RemoteSubTitles downloadMovieSubtitles(String movieName, int year, String release, VideoSource source, BigDecimal fps, Locale locale) throws InterruptedException, ExecutionException {

        return null;
    }
}
