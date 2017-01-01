package fr.mozvip.subtitles.betaseries;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.mozvip.subtitles.EpisodeSubtitlesFinder;
import fr.mozvip.subtitles.RemoteSubTitles;
import fr.mozvip.subtitles.SubTitlesZip;
import fr.mozvip.subtitles.SubtitlesFinder;

public class BetaSeries extends SubtitlesFinder implements EpisodeSubtitlesFinder {

	public BetaSeries(String login, String password) throws IOException {
		post("https://www.betaseries.com/apps/login.php", "http://www.betaseries.com/introduction",
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

			RemoteSubTitles subtitle = SubTitlesZip.selectBestSubtitles(getBytes(zipFileURL, element.baseUri()),
					release, locale);
			if (subtitle != null) {
				if (currentSubs == null || subtitle.getScore() > currentSubs.getScore()) {
					currentSubs = subtitle;
				}
			}
		}

		return currentSubs;
	}

}
