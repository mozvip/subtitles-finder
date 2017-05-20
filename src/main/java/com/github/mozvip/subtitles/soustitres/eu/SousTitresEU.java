package com.github.mozvip.subtitles.soustitres.eu;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mozvip.subtitles.EpisodeSubtitlesFinder;
import com.github.mozvip.subtitles.MovieSubtitlesFinder;
import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.SubTitlesUtils;
import com.github.mozvip.subtitles.SubTitlesZip;
import com.github.mozvip.subtitles.SubtitlesFinder;

public class SousTitresEU extends SubtitlesFinder implements EpisodeSubtitlesFinder, MovieSubtitlesFinder {

	private final static Logger LOGGER = LoggerFactory.getLogger(SousTitresEU.class);

	private final static String ROOT_URL = "http://www.sous-titres.eu";

	@Override
	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release,
			Locale locale) throws IOException {

		String seriesName = showName.toLowerCase();

		Matcher matcher = Pattern.compile("(.*)\\s+\\((19|20\\d{2})\\)").matcher(seriesName);
		if (matcher.matches()) {
			seriesName = matcher.group(1);
		}

		String url = null;

		if (url == null) {
			seriesName = seriesName.replace(' ', '_');
			seriesName = seriesName.replaceAll("\\(\\):\\.", "");
			url = seriesName;

			url = ROOT_URL + "/series/" + url + ".html";
		}

		Document document = getDocument(url);
		if (document == null) {
			LOGGER.warn(String.format("Couldn't find show %s", showName));
			return null;
		}

		Elements nodes = document.select("a > span.episodenum");

		RemoteSubTitles bestSubTitles = null;

		for (Element node : nodes) {

			// gets parent node (TR)
			Element tableRow = node.parent();
			Elements flagImageNodes = tableRow.select("img");

			boolean hasLanguage = false;
			for (Element flagImageNode : flagImageNodes) {
				String lang = flagImageNode.attr("title");
				if (StringUtils.equalsIgnoreCase(lang, locale.getLanguage())) {
					hasLanguage = true;
					break;
				}
			}

			if (!hasLanguage) {
				continue;
			}

			String text = node.text();

			Element link = node.parent();
			String href = link.absUrl("href");

			if (SubTitlesUtils.isExactMatch(text, season, episode)) {

				byte[] bytes = getBytes(href, url);
				RemoteSubTitles currentRemoteSubTitles = SubTitlesZip.selectBestSubtitles(bytes, release, locale);
				if (currentRemoteSubTitles != null) {
					if (bestSubTitles == null || currentRemoteSubTitles.getScore() > bestSubTitles.getScore()) {
						bestSubTitles = currentRemoteSubTitles;
					}
				}

			} else if (SubTitlesUtils.isSeasonMatch(text, season)) {

				byte[] bytes = getBytes(href, url);
				RemoteSubTitles currentRemoteSubTitles = SubTitlesZip.selectBestSubtitles(bytes, release, locale,
						season, episode);
				if (currentRemoteSubTitles != null) {
					if (bestSubTitles == null || currentRemoteSubTitles.getScore() > bestSubTitles.getScore()) {
						bestSubTitles = currentRemoteSubTitles;
					}
				}

			}
		}

		return bestSubTitles;

	}

	@Override
	public RemoteSubTitles downloadMovieSubtitles(String movieName, int year, String release, BigDecimal fps,
			Locale locale) throws Exception {

		String url = String.format("%s/search.html?q=%s+%d", ROOT_URL, movieName, year);

		Document document = getDocument(url);
		if (document == null) {
			return null;
		}

		Elements nodes = document.select("a > span.episodenum");

		RemoteSubTitles bestSubTitles = null;

		for (Element node : nodes) {

			// gets parent node (TR)
			Element tableRow = node.parent();
			Elements flagImageNodes = tableRow.select("img");

			boolean hasLanguage = false;
			for (Element flagImageNode : flagImageNodes) {
				String lang = flagImageNode.attr("title");
				if (StringUtils.equalsIgnoreCase(lang, locale.getLanguage())) {
					hasLanguage = true;
					break;
				}
			}

			if (!hasLanguage) {
				continue;
			}

			Element link = node.parent();
			String href = link.absUrl("href");

			try {
				byte[] bytes = getBytes(href, url);			
				RemoteSubTitles currentRemoteSubTitles = SubTitlesZip.selectBestSubtitles(bytes, release, locale);
				if (currentRemoteSubTitles != null) {
					if (bestSubTitles == null || currentRemoteSubTitles.getScore() > bestSubTitles.getScore()) {
						bestSubTitles = currentRemoteSubTitles;
					}
				}
			} catch (IOException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}

		return bestSubTitles;
	}

}
