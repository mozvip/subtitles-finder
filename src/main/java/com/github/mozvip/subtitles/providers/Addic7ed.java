package com.github.mozvip.subtitles.providers;

import com.github.mozvip.subtitles.utils.RemoteSubTitles;
import com.github.mozvip.subtitles.utils.SubTitleEvaluator;
import com.github.mozvip.subtitles.utils.SubtitlesFinder;
import com.github.mozvip.subtitles.model.VideoSource;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.MatchResult;

public class Addic7ed extends SubtitlesFinder implements EpisodeSubtitlesFinder {

	public static final String ROOT_URL = "http://www.addic7ed.com";

	@Override
	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release, VideoSource source, Locale locale) throws InterruptedException, ExecutionException {

		showName = showName.replace(' ', '+');
		String searchURL = String.format("%s/srch.php?search=%s+%02dx%02d&Submit=Search", ROOT_URL, showName, season, episode);
		Response response;
		try {
			response = get( searchURL, ROOT_URL, 1, TimeUnit.DAYS ).get();
		} catch (InterruptedException e) {
			throw e;
		}
		String episodeUrl = response.request().url().toString();

		try {
			Document document = Jsoup.parse( response.body().string());
	
			String languageFullName = locale.getDisplayLanguage(Locale.ENGLISH);
	
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
					String url = ROOT_URL + row.select("a.buttonDownload").first().attr("href");
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

}
