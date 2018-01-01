package com.github.mozvip.subtitles.providers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mozvip.subtitles.FileHashSubtitlesFinder;
import com.github.mozvip.subtitles.RemoteSubTitles;
import com.github.mozvip.subtitles.SubtitlesFinder;

public class OpenSubtitlesOrg extends SubtitlesFinder implements FileHashSubtitlesFinder {

	private final static Logger LOGGER = LoggerFactory.getLogger(OpenSubtitlesOrg.class);

	private XmlRpcClient xmlRPCClient;

	public OpenSubtitlesOrg() throws MalformedURLException {
		String url = "http://api.opensubtitles.org/xml-rpc";
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL(url));
		xmlRPCClient = new XmlRpcClient();
		xmlRPCClient.setConfig(config);
	}

	@Override
	public RemoteSubTitles downloadSubtitlesForFileHash(String fileHash, long videoByteSize, Locale locale)
			throws Exception {

		String userAgent = "TemporaryUserAgent";

		Map<String, Object> result = (Map<String, Object>) xmlRPCClient.execute("LogIn",
				new Object[] { "", "", locale.getLanguage(), userAgent });
		String token = (String) result.get("token");
		try {

			Map<String, String> sub = new HashMap<String, String>();

			sub.put("sublanguageid", locale.getISO3Language());
			sub.put("moviehash", fileHash);
			sub.put("moviebytesize", "" + videoByteSize);

			List<Map<String, String>> subtitles = new ArrayList<Map<String, String>>();

			subtitles.add(sub);

			result = (Map<String, Object>) xmlRPCClient.execute("SearchSubtitles", new Object[] { token, subtitles });
			Object data = result.get("data");

			if (data instanceof Object[]) {
				Object[] results = (Object[]) data;
				for (Object subtitle : results) {
					Map<String, String> subtitleDataMap = (Map) subtitle;
					String downloadLink = subtitleDataMap.get("SubDownloadLink");
					String subtitlesLink = subtitleDataMap.get("SubtitlesLink");
					String title = subtitleDataMap.get("SubFileName");

					byte[] subTitleData = getBytes(downloadLink, subtitlesLink);
					return new RemoteSubTitles(this, title, subTitleData, 20);
				}
			}

		} finally {
			xmlRPCClient.execute("LogOut", new Object[] { token });
		}

		return null;
	}

}
