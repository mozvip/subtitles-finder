package com.github.mozvip.subtitles.podnapisi.net;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.mozvip.subtitles.EpisodeSubtitlesFinder;
import com.github.mozvip.subtitles.Release;
import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.SubTitlesZip;
import com.github.mozvip.subtitles.SubtitlesFinder;

public class Podnapisi extends SubtitlesFinder implements EpisodeSubtitlesFinder {

	private String buildSearchURL(String name, int season, int episode, Locale locale) {
		name = name.trim().replace(' ', '+');
		String baseUrl = String.format(
				"http://www.podnapisi.net/subtitles/search/advanced?keywords=%s&seasons=%d&episodes=%d&language=%s",
				name, season, episode, locale.getLanguage());

		// TODO: retrieve FPS with mediainfo ? (&fps=25 or &fps=23.976)
		return baseUrl;
	}

	@Override
	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release,
			Locale locale) throws IOException {

		String queryString = showName.toLowerCase();
		if (StringUtils.contains(queryString, "(")) {
			queryString = StringUtils.substringBefore(queryString, "(") + StringUtils.substringAfter(queryString, ")");
		}

		String url = buildSearchURL(queryString, season, episode, locale);
		Document document = getDocument(url);

		String href = null;

		Elements rows = document.select("tr.subtitle-entry");
		for (Element row : rows) {
			String releaseText = row.select("span.release").attr("title");
			String currentSubtitlesHref = row.select("a[href*=/download]").first().absUrl("href");

			if (release != null) {
				Release foundRelease = Release.firstMatch(releaseText);
				if (foundRelease != Release.UNKNOWN && foundRelease.match(release)) {
					href = currentSubtitlesHref;
					break;
				} else if (foundRelease == Release.UNKNOWN) {
					href = currentSubtitlesHref;
				}
			}
		}
		
		if (href != null) {
			byte[] bytes = getBytes(href, url);
			return SubTitlesZip.selectBestSubtitles(bytes, release, locale);
		}
		
		return null;
	}

}
