package com.github.mozvip.subtitles.isubtitles;

import java.math.BigDecimal;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.mozvip.subtitles.MovieSubtitlesFinder;
import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.SubTitlesZip;
import com.github.mozvip.subtitles.SubtitlesFinder;
import com.github.mozvip.subtitles.model.Release;

public class ISubtitlesNet extends SubtitlesFinder implements MovieSubtitlesFinder {
	
	public final static String BASE_URL = "https://isubtitles.in";

	@Override
	public RemoteSubTitles downloadMovieSubtitles(String movieName, int year, String release, BigDecimal fps,
			Locale locale) throws Exception {
		
		Release releaseGroup = Release.firstMatch( release );
		
		movieName = movieName.replaceAll(":/-_\\[\\]\\{\\}\\(\\)", " ");
		movieName = movieName.replaceAll("\\s+", " ");
		movieName = movieName.replaceAll("\\s", "+");
		
		String url = String.format("%s/search?kwd=%s+%d", BASE_URL, movieName, year);
		
		Document document = getDocument(url);
		Elements movieLinks = document.select(".movie-list-info .row a");
		if (movieLinks.size() == 0) {
			return null;
		}
		Element movieLink = movieLinks.first();
		
		String movieUrl = movieLink.absUrl("href");
		document = getDocument( movieUrl, url );
		
		Elements rows = document.select( String.format( "tr:has(td:contains(%s))", locale.getDisplayLanguage() ));
		for (Element row : rows) {
			
			boolean hasRelease = false;
			
			if (releaseGroup != Release.UNKNOWN) {
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
				byte[] zipData = get(downloadUrl, movieUrl).body().bytes();
				
				RemoteSubTitles subtitles = SubTitlesZip.selectBestSubtitles(this, zipData, release, locale);
				subtitles.setScore( 10 );
				return subtitles;
			}
		}

		return null;
	}

}
