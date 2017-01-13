package com.github.mozvip.subtitles.addic7ed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.regex.MatchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mozvip.subtitles.EpisodeSubtitlesFinder;
import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.SubtitlesFinder;

import okhttp3.Response;

public class Addic7ed extends SubtitlesFinder implements EpisodeSubtitlesFinder {
	
	private final static Logger LOGGER = LoggerFactory.getLogger( Addic7ed.class );
	private Semaphore semaphore = new Semaphore(1);	// this is to avoid multiple concurrent invocations

	public Addic7ed() throws IOException {
		init();
	}

	private Map<String, String> shows = new HashMap<>();
	
	private String getShowId( String showName ) {
		String name = getShowName( showName );
		if (! shows.containsKey( name )) {
			name = extractNameFromShowName(name);
		}
		return shows.get( name );
	}
	
	@Override
	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, Locale locale) throws IOException {

		String showId = getShowId( showName );
		if (showId == null) {
			LOGGER.warn( String.format("Couldn't find show %s", showName) );
			return null;
		}
		
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			throw new IOException( e ); 
		}

		try {
			
			String episodeLookupURL = "http://www.addic7ed.com/re_episode.php?ep=" + showId + "-" + season + "x" + episode;
			Response response = get( episodeLookupURL, null );
			
			String episodeUrl = response.request().url().toString();
			
			Document document = Jsoup.parse( response.body().string());
	
			String languageFullName = locale.getDisplayLanguage();
	
			Elements matchingNodes = document.select( "tr:contains("+ languageFullName + "):contains(Completed):contains(Download)" );
			
			int currentScore = -10;
			String currentURL = null;
			for ( Element row : matchingNodes ) {
				List<String> compatibleReleases = new ArrayList<String>();
				Element parentTable = row.parent().parent();
				if (parentTable != null) {
					String text = parentTable.select("td.NewsTitle").text();
					text = text.trim();
					
					try (Scanner scanner = new Scanner( text )) {
						if (scanner.findInLine(".*Version (.*), .*") != null) {
							MatchResult result = scanner.match();
							String releaseText = result.group(1);
							compatibleReleases.add( releaseText );
						}
					}
				
					String additionalText = parentTable.select("td.newsDate:contains(Works with)").text();
					additionalText = additionalText.trim();
					
					try (Scanner scanner = new Scanner( additionalText )) {
						if (scanner.findInLine(".*Works? with (.*)") != null) {
							MatchResult result = scanner.match();
							String releaseText = result.group(1);
							compatibleReleases.add( releaseText );
						}					
					}
	
				}
	
				String url = "http://www.addic7ed.com" + row.select("a.buttonDownload").first().attr("href");
				
				if (!compatibleReleases.isEmpty()) {
					for (String string : compatibleReleases) {
						if (string.contains(release)) {
							currentScore = 20;
							currentURL = url;
							break;
						}
					}
				} else {
					currentScore = 5;
					currentURL = url;
				}
			}
			
			if (currentURL != null) {
				Response resp = get( currentURL, episodeUrl );
				if (resp.code() == 200 && resp.header("Content-Type").contains("text/srt")) {
					RemoteSubTitles remoteSubTitles = new RemoteSubTitles( resp.body().bytes(), currentScore );
					return remoteSubTitles;
				}
			}
			
		} finally {
			semaphore.release();
		}
		
		return null;
	}
	
	private void init() throws IOException {
		Document document = getDocument( "http://www.addic7ed.com/shows.php" );
		Elements showLinks = document.select( "h3 > a" );
		for ( Element showLink : showLinks ) {
			String show = showLink.text();

			String href = showLink.absUrl("href");					
			String showId = href.substring( href.lastIndexOf("/") + 1);

			show = getShowName(show);
			show = extractNameFromShowName(show);
			shows.put( show, showId );
		}		
	}

	private String getShowName(String show) {
		show = show.toLowerCase();
		show = show.replaceAll("[\\s',!\\?]", "");
		show = show.trim();
		return show;
	}

}
