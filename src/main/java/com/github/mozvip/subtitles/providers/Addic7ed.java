package com.github.mozvip.subtitles.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.MatchResult;

import com.github.mozvip.subtitles.SubTitleEvaluator;
import com.github.mozvip.subtitles.model.VideoSource;
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

	public Addic7ed() throws ExecutionException {
		init();
	}

	private final Map<String, String> shows = new HashMap<>();
	
	private String getShowId( String showName ) {
		String name = getShowName( showName );
		if (! shows.containsKey( name )) {
			name = extractNameFromShowName(name);
		}
		return shows.get( name );
	}
	
	@Override
	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, VideoSource source, Locale locale) throws ExecutionException {

		String showId = getShowId( showName );
		if (showId == null) {
			LOGGER.warn( "Couldn't find show {}", showName );
			return null;
		}
		
		try {
			
			String episodeLookupURL = "http://www.addic7ed.com/re_episode.php?ep=" + showId + "-" + season + "x" + episode;
			Response response = get( episodeLookupURL, null, 1, TimeUnit.DAYS ).get();
			
			String episodeUrl = response.request().url().toString();
			
			Document document = Jsoup.parse( response.body().string());
	
			String languageFullName = locale.getDisplayLanguage();
	
			Elements matchingNodes = document.select( "tr:contains("+ languageFullName + "):contains(Completed):contains(Download)" );
			
			int currentScore = -10;
			String currentURL = null;
			for ( Element row : matchingNodes ) {
				List<String> compatibleReleases = new ArrayList<>();
				Element parentTable = row.parent().parent();

				String text = row.text();

				if (parentTable != null) {
					text = parentTable.select("td.NewsTitle").text().trim();

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
	
				int evaluation = SubTitleEvaluator.evaluateSubtitleForRelease(this, text, compatibleReleases, locale, release, source);
				if (evaluation > currentScore) {
					currentScore = evaluation;
					String url = "http://www.addic7ed.com" + row.select("a.buttonDownload").first().attr("href");
					currentURL = url;
				}
			}
			
			if (currentURL != null) {
				Response resp = get( currentURL, episodeUrl ).get();
				if (resp.code() == 200 && resp.header("Content-Type").contains("text/srt")) {
					
					String title = resp.header("Content-Disposition"); // FIXME

					try (Scanner scanner = new Scanner( title )) {
						if (scanner.findInLine(".*filename=\"(.*)\".*") != null) {
							MatchResult result = scanner.match();
							title = result.group(1);
						}
					}
					
					RemoteSubTitles remoteSubTitles = new RemoteSubTitles(this, title, resp.body().bytes(), currentScore );
					return remoteSubTitles;
				}
			}
			
		} catch (Exception e) {
			throw new ExecutionException(e);
		}
		
		return null;
	}
	
	private void init() throws ExecutionException {
		Document document = getDocument( "http://www.addic7ed.com/shows.php", null, 1, TimeUnit.DAYS );
		Elements showLinks = document.select( "h3 > a" );
		for ( Element showLink : showLinks ) {
			String show = showLink.text();

			String href = showLink.absUrl("href");					
			String showId = href.substring( href.lastIndexOf('/') + 1);

			show = getShowName(show);
			show = extractNameFromShowName(show);
			shows.put( show, showId );
		}		
	}

	private String getShowName(String show) {
		show = show.toLowerCase();
		show = show.replaceAll("[\\s',!\\?]", "");
		show = show.replaceAll("\\((19\\d{2}|20\\d{2})\\)", "");
		show = show.replaceAll("\\s+", " ");
		show = show.trim();
		return show;
	}

}
