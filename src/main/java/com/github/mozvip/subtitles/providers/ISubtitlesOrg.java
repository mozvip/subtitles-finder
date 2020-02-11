package com.github.mozvip.subtitles.providers;

import com.github.mozvip.subtitles.utils.RemoteSubTitles;
import com.github.mozvip.subtitles.utils.SubTitlesZip;
import com.github.mozvip.subtitles.utils.SubtitlesFinder;
import com.github.mozvip.subtitles.model.Release;
import com.github.mozvip.subtitles.model.VideoSource;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ISubtitlesOrg extends SubtitlesFinder implements MovieSubtitlesFinder {
	
	public final static String BASE_URL = "https://isubtitles.org";

	@Override
	public RemoteSubTitles downloadMovieSubtitles(String movieName, int year, String release, VideoSource videoSource, BigDecimal fps,
												  Locale locale) throws InterruptedException, ExecutionException {
		
		Release releaseGroup = Release.firstMatch( release );
		
		movieName = movieName.replaceAll(":/-_\\[\\]\\{\\}\\(\\)", " ");
		movieName = movieName.replaceAll("\\s+", " ");
		movieName = movieName.replaceAll("\\s", "+");
		
		String url = String.format("%s/search?kwd=%s+%d", BASE_URL, movieName, year);
		
		Document document = getDocument(url, null, 1, TimeUnit.DAYS);
		Elements movieLinks = document.select(".movie-list-info .row a");
		if (movieLinks.size() == 0) {
			return null;
		}
		Element movieLink = movieLinks.first();
		
		String movieUrl = movieLink.absUrl("href");
		document = getDocument( movieUrl, url, 1, TimeUnit.DAYS );
		
		Elements rows = document.select( String.format( "tr:has(td:contains(%s))", locale.getDisplayLanguage(Locale.ENGLISH) ));
		for (Element row : rows) {
			
			boolean hasRelease = false;
			
			if (releaseGroup != null) {
				Elements links = row.select(".movie-release a");
				for (Element element : links) {
					if (Release.firstMatch( element.text() ) == releaseGroup) {
						hasRelease = true;
						break;
					}
				}
			}
			
			if (hasRelease) {

				String downloadUrl = row.select("a[href*=download]").first().absUrl("href");

				try {
					byte[] zipData = get(downloadUrl, movieUrl).get().body().bytes();
					RemoteSubTitles subtitles = SubTitlesZip.selectBestSubtitlesFromZip(this, zipData, release, videoSource, locale);
					subtitles.setScore(10);
					return subtitles;
				} catch (IOException e) {
					throw new ExecutionException(e);
				}
			}
		}

		return null;
	}

}
