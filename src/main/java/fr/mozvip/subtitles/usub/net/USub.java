package fr.mozvip.subtitles.usub.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

public class USub extends SubtitlesFinder implements EpisodeSubtitlesFinder {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(USub.class);

	public USub() throws IOException {
		init();
	}
	
	private Document indexPage;
	private Map<String, String> urls;
	
	public String getSearchString( String string ) {
		return string.replaceAll("\\W", "").toUpperCase();
	}
	
	private void init() throws IOException {
		indexPage = getDocument( "http://www.u-sub.net/sous-titres/" );
		Elements elements = indexPage.select("a[href*=/sous-titres/][title]");
		
		urls = new HashMap<String, String>();
		for(Element element : elements) {
			String seriesName = getSearchString( element.text() );
			String seriesURL = element.absUrl("href");
			urls.put( seriesName, seriesURL );
		}
	}
	
	@Override
	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, Locale language) throws IOException {
		
		String url = urls.get( getSearchString( showName ) );
		
		if (url == null) {
			LOGGER.debug("Series " + showName + " was not found");
			return null;
		}
		
		url += "saison_" + season + "/";
		
		Document document = getDocument( url );
		Elements nodes = document.select( "#subtitles_list a.dl_link" );
		
		for( Element node : nodes ) {
			String text = node.text();
			
			if (SubTitlesUtils.isMatch(text, season, episode)) {
				String href = node.absUrl("href");
				return SubTitlesZip.selectBestSubtitles( getBytes(href, url), release, language );
			}
			
		}

		return null;
	}

}
