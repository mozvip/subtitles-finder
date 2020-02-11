package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.github.mozvip.subtitles.model.VideoSource;
import com.github.mozvip.subtitles.utils.RemoteSubTitles;
import com.github.mozvip.subtitles.utils.SubTitleEvaluator;
import com.github.mozvip.subtitles.utils.SubTitlesZip;
import com.github.mozvip.subtitles.utils.SubtitlesFinder;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Podnapisi extends SubtitlesFinder implements EpisodeSubtitlesFinder, MovieSubtitlesFinder {

	public static final String ROOT_URL = "https://www.podnapisi.net";

	private final static Logger LOGGER = LoggerFactory.getLogger(Podnapisi.class);

	private String buildEpisodeSearchURL(String name, int season, int episode, Locale locale) {
		name = name.trim().replace(' ', '+');
		String baseUrl = String.format(
				ROOT_URL + "/subtitles/search/advanced?keywords=%s&seasons=%d&episodes=%d&language=%s",
				name, season, episode, locale.getLanguage());

		// TODO: retrieve FPS with mediainfo ? (&fps=25 or &fps=23.976)
		return baseUrl;
	}

	private String buildMovieSearchURL(String name, int year, Locale locale) {
		name = name.trim().replace(' ', '+');
		String baseUrl = String.format(
				ROOT_URL + "/subtitles/search/advanced?keywords=%s&year=%d&language=%s", name, year,
				locale.getLanguage());

		// TODO: retrieve FPS with mediainfo ? (&fps=25 or &fps=23.976)
		return baseUrl;
	}

	@Override
	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, VideoSource source,
												   Locale locale) throws ExecutionException {

		String queryString = showName.toLowerCase();
		if (StringUtils.contains(queryString, "(")) {
			queryString = StringUtils.substringBefore(queryString, "(") + StringUtils.substringAfter(queryString, ")");
		}

		String url = buildEpisodeSearchURL(queryString, season, episode, locale);
		return extractSubtitles(release, source, locale, url);
	}

	@Override
	public RemoteSubTitles downloadMovieSubtitles(String movieName, int year, String release, VideoSource videoSource, BigDecimal fps, Locale locale)
			throws InterruptedException, ExecutionException {
		String queryString = movieName.toLowerCase();
		if (StringUtils.contains(queryString, "(")) {
			queryString = StringUtils.substringBefore(queryString, "(") + StringUtils.substringAfter(queryString, ")");
		}

		String url = buildMovieSearchURL(queryString, year, locale);
		return extractSubtitles(release, videoSource, locale, url);
	}

	private RemoteSubTitles extractSubtitles(String release, VideoSource source, Locale locale, String url) throws ExecutionException {
		Document document = getDocument(url, null, 1, TimeUnit.DAYS);

		String href = null;

		Elements rows = document.select("tr.subtitle-entry");
		int currentScore = -1;
		for (Element row : rows) {
			String releaseText = row.select("span.release").attr("title");
			String currentSubtitlesHref = row.select("a[href*=/download]").first().absUrl("href");

			int score = 0;
			if (StringUtils.isNotEmpty(releaseText)) {
				score = SubTitleEvaluator.evaluateSubtitleForRelease(this, releaseText, locale, release, source);
			} else {
				byte[] bytes = getBytes(currentSubtitlesHref, url);
				try {
                    List<String> strings = SubTitlesZip.listofFileNames(bytes);
                    for (String fileName : strings) {
                        score = SubTitleEvaluator.evaluateSubtitleForRelease(this, fileName, locale, release, source); // FIXME : only keep the best
                    }
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if (score > currentScore) {
				currentScore = score;
				href = currentSubtitlesHref;
			}
		}

		if (href != null) {
			byte[] bytes = getBytes(href, url);
			try {
				return SubTitlesZip.firstFromZipFile(this, bytes, currentScore);
			} catch (IOException e) {
				throw new ExecutionException(e);
			}
		}
		
		return null;
	}

}
