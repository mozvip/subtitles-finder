package fr.mozvip.subtitles.tvsubtitles.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.mozvip.subtitles.EpisodeSubtitlesFinder;
import fr.mozvip.subtitles.RemoteSubTitles;
import fr.mozvip.subtitles.SubTitlesUtils;
import fr.mozvip.subtitles.SubTitlesZip;
import fr.mozvip.subtitles.SubtitlesFinder;

public class TVSubtitlesNet extends SubtitlesFinder implements EpisodeSubtitlesFinder {
	
	private final static Logger LOGGER = LoggerFactory.getLogger( TVSubtitlesNet.class );

	private static Map<String, String> seriesMap;

	public TVSubtitlesNet() throws IOException {
		init();
	}

	private void init() throws IOException {
		seriesMap = new HashMap<String, String>();
		Document tvShowsDocument = getDocument( "http://www.tvsubtitles.net/tvshows.html" );
		Elements tvLinks = tvShowsDocument.select("#table5 a");
		for ( Element link : tvLinks ) {
			String href = link.attr("href");
			href = href.substring( href.indexOf("-") + 1, href.lastIndexOf("-"));
			String seriesName = link.text().toLowerCase();
			if (StringUtils.isNotBlank( seriesName )) {
				seriesMap.put( seriesName, href );
			}
		}
	}
	
	@Override
	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, Locale locale) throws IOException {
		
		showName = extractNameFromShowName(showName).toLowerCase();
		if (!seriesMap.containsKey( showName )) {
			LOGGER.warn("Show " + showName + " not found");
			return null;
		}
		
		String url = "http://www.tvsubtitles.net/tvshow-" + seriesMap.get( showName ) + "-" + season + ".html";
		
		Document document = getDocument( url );
		Elements rows = document.select("table#table5 tr");

		int maxScore = -100;
		Element selectedSubtitle = null;
		String subTitlesURL = null;

		for (Element row : rows) {
			if (row.select("td").isEmpty()) {
				continue;
			}
			String episodeStr = row.select("td").first().text();
			if (!SubTitlesUtils.isExactMatch(episodeStr, season, episode)) {
				continue;
			}
			subTitlesURL = row.select("a").first().absUrl("href");
			document = getDocument( subTitlesURL );
			
			Elements subtitles = document.select( String.format( "a:has(h5 img[src*=%s])", locale.getLanguage() ));
			if (subtitles.isEmpty()) {
				continue;
			}
			for ( Element subtitle : subtitles ) {
				int score = SubTitlesZip.evaluateScore( subtitle.select("h5").first().text(), locale, release );
				if (score > maxScore) {
					selectedSubtitle = subtitle;
					maxScore = score;
				}
			}
			break;
		}
		
		if (selectedSubtitle != null) {
			String href = selectedSubtitle.absUrl("href");
			document = getDocument( href, url );
			String downloadURL = document.select("a:has(h3)").first().absUrl("href");
			
			byte[] bytes = getBytes( downloadURL, href );
			return SubTitlesZip.selectBestSubtitles( bytes, release, locale );
		}

		return null;
	}

}
