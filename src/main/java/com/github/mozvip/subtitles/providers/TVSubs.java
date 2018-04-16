package com.github.mozvip.subtitles.providers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.github.mozvip.subtitles.*;
import com.github.mozvip.subtitles.model.VideoSource;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TVSubs extends SubtitlesFinder implements EpisodeSubtitlesFinder {

	private static final String ROOT_URL = "http://www.tvsubs.net/";

	private final static Logger LOGGER = LoggerFactory.getLogger( TVSubs.class );

	private Document tvShowsDocument = null;
	private Map<String, String> seriesMap = new HashMap<String, String>();

	public TVSubs() throws ExecutionException {
		tvShowsDocument = getDocument(ROOT_URL + "tvshows.html", null, 1, TimeUnit.DAYS);
		Elements tvLinks = tvShowsDocument.select("ul.list1 a");
		for (Element link : tvLinks) {
			String href = link.attr("href");
			href = href.substring( href.indexOf("-") + 1, href.lastIndexOf("-"));

			String seriesName = link.text().toLowerCase();
			seriesMap.put( seriesName, href );
		}
	}

	public String getSeriesId( String name ) {
		return seriesMap.get( name.toLowerCase() );
	}

	@Override
	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, VideoSource source,
			Locale locale) throws ExecutionException {

		String seriesId = getSeriesId( showName );
		if (seriesId == null) {
			LOGGER.warn( String.format("TV Show %s was not found", showName) );
			return null;
		}
		String seasonURL = ROOT_URL + "tvshow-" + seriesId + "-" + season + ".html";
		Document seasonDocument = getDocument(seasonURL, null, 1, TimeUnit.DAYS);

		Elements elements = seasonDocument.select( String.format( "ul.list1 li:contains(%02d)", episode) );

		String resultURL = null;
		String refererURL = null;

		for ( Element element : elements ) {

			Element imageForLanguage = element.select(String.format("img[src*=%s.gif]", locale.getLanguage())).first();
			if (imageForLanguage == null) {
				break;
			}

			Element linkForLanguage = imageForLanguage.parent();
			String subTitlesURL = linkForLanguage.absUrl("href");

			if (subTitlesURL.contains("subtitle-")) {

				refererURL = subTitlesURL;
				String subtitleId = RegExp.extract( subTitlesURL, ".*subtitle-(\\d+)\\.html");
				resultURL = String.format("%sdownload-%s.html", ROOT_URL, subtitleId);

			} else {

				Document subTitlesPage = getDocument( subTitlesURL, seasonURL, 1, TimeUnit.DAYS );
				Elements downloads = subTitlesPage.select("ul.list1 li" );
				if (downloads != null && downloads.size() > 0) {
					int currentScore = -1;
					for ( Element download: downloads ) {
						String subTitleName = download.select("a").text();

						int score = SubTitleEvaluator.evaluateSubtitleForRelease(this, subTitleName, locale, release, source);

						if (score > currentScore) {
							currentScore = score;

							String subTitleURL = download.select("a[href*=subtitle]").first().absUrl("href");
							String subtitleId = RegExp.extract( subTitleURL, ".*subtitle-(\\d+)\\.html");

							refererURL = subTitleURL;
							resultURL = String.format("%sdownload-%s.html", ROOT_URL, subtitleId);
						}
					}
				}
			}

		}

		RemoteSubTitles subTitles = null;
		if (resultURL != null) {
			Document downloadStagingPage = getDocument(resultURL, refererURL, 1, TimeUnit.DAYS);

			String url = "";

			String[] javascriptScript = downloadStagingPage.select("script").first().childNode(0).toString().split("\n");
			for (String string : javascriptScript) {
				string = string.trim();
				if (string.length() == 0) {
					continue;
				}

				String value = RegExp.extract(string,".*var s\\d= '([^']+)'.*");
				if (value != null) {
					url += value;
				}
			}

			byte[] bytes = getBytes(ROOT_URL + url, refererURL);
			try {
				subTitles = SubTitlesZip.selectBestSubtitlesFromZip(this, bytes, release, source, locale );
			} catch (IOException e) {
				throw new ExecutionException(e);
			}
		}

		return subTitles;
	}

}
