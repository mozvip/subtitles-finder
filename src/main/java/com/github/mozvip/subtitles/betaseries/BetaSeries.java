package com.github.mozvip.subtitles.betaseries;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.mozvip.subtitles.EpisodeSubtitlesFinder;
import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.SubTitlesZip;
import com.github.mozvip.subtitles.SubtitlesFinder;

import okhttp3.Response;

public class BetaSeries extends SubtitlesFinder implements EpisodeSubtitlesFinder {
	
	public static final class Builder {

		private String login, password;

		public Builder login(String login) {
			this.login = login;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public BetaSeries build() throws IOException {
			return new BetaSeries(login, password);
		}

	}

	private BetaSeries(String login, String password) throws IOException {
		Response post = post("https://www.betaseries.com/apps/login.php", "http://www.betaseries.com/identification",
				"login=" + login, "pass=" + password);
	}

	public Element getEpisodeElement(String showName, int season, int episode)
			throws UnsupportedEncodingException, IOException {

		Document searchResults = getDocument(String.format("http://www.betaseries.com/ajax/header/search.php?q=%s",
				URLEncoder.encode(showName, "UTF-8")));
		Elements nodes = searchResults.select("item:contains(serie) > url");
		for (Element node : nodes) {
			String url = "http://www.betaseries.com/ajax/episodes/season.php?url=" + node.text() + "&saison=" + season;
			Document document = getDocument(url);
			Elements elements = document.select(String.format("div[id*=%s%d%d]", node.text(), season, episode));
			if (elements != null && elements.size() > 0) {
				return elements.first();
			}
		}

		return null;
	}

	@Override
	public RemoteSubTitles downloadEpisodeSubtitle(String showName, int season, int episode, String release,
			Locale locale) throws IOException {

		Element element = getEpisodeElement(showName, season, episode);
		if (element == null) {
			return null;
		}

		String flag = "vo.png";
		if (locale.getLanguage().equals("fr")) {
			flag = "vf.png";
		}

		Elements listItems = element.select("li>img[src*=" + flag + "]");
		RemoteSubTitles currentSubs = null;
		for (Element imageItem : listItems) {

			Element listItem = imageItem.parent();

			Element subTitleLink = listItem.select("span>a").first();
			String zipFileURL = subTitleLink.attr("abs:href");
			
			RemoteSubTitles subtitle;

			try (Response response = get(zipFileURL, element.baseUri())) {
			
				String contentDisposition = response.header("Content-Disposition");
				String filename;
				try (Scanner scanner = new Scanner(contentDisposition)) {
					scanner.findInLine(".*filename=.(.*).");
					MatchResult result = scanner.match();
					filename = result.group( 1 );
				}
	
				if (filename.endsWith(".zip")) {
					subtitle = SubTitlesZip.selectBestSubtitles(this, response.body().bytes(), release, locale);
				} else {
					subtitle = new RemoteSubTitles(this, filename, response.body().bytes(), 1);
				}
			}
			if (subtitle != null) {
				if (currentSubs == null || subtitle.getScore() > currentSubs.getScore()) {
					currentSubs = subtitle;
				}
			}
		}

		return currentSubs;
	}

}
