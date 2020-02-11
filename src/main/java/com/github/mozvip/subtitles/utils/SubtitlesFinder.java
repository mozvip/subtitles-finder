package com.github.mozvip.subtitles.utils;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.MatchResult;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SubtitlesFinder {

	private final static Logger LOGGER = LoggerFactory.getLogger(SubtitlesFinder.class);

	protected String extractNameFromShowName(String name) {
		try (Scanner scanner = new Scanner(name)) {
			if (scanner.findInLine("(.*)\\s+\\((19|20\\d{2})\\)") != null) {
				MatchResult match = scanner.match();
				name = match.group(1);
			}
		}
		return name;
	}

	public Document getDocument(String url, String refererUrl, int maxStale, TimeUnit timeUnit) throws ExecutionException {
        try {
            Response response = get(url, refererUrl, maxStale, timeUnit).get();
            return Jsoup.parse(response.body().string(), url);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
	}

	public byte[] getBytes(String url, String refererUrl) throws ExecutionException {
	    try {
            Response response = get(url, refererUrl, 1, TimeUnit.DAYS).get();
            return response.body().bytes();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
	}

	public Future<Response> post(String url, String refererUrl, String... params) {
		FormBody.Builder formBodyBuilder = new FormBody.Builder();
		for (String param : params) {

			try (Scanner scanner = new Scanner(param)) {
				scanner.findInLine("(.+)=(.+)");
				MatchResult result = scanner.match();
				formBodyBuilder.add(result.group(1), result.group(2));
			}

		}
		FormBody formBody = formBodyBuilder.build();

		Request.Builder builder = getRequestBuilder(url, refererUrl);
		builder.post(formBody);
		Request request = builder.build();

		return new HttpRequestCommand(request).queue();
	}

	private Request.Builder getRequestBuilder(String url, String refererUrl) {
		Request.Builder builder = new Request.Builder().url(url);
		builder.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
		if (refererUrl != null) {
			refererUrl = HttpUrl.parse( refererUrl ).toString();
			builder.addHeader("Referer", refererUrl);
		}
		return builder;
	}

	protected Future<Response> get(String url, String refererUrl) {
		return get(url, refererUrl, 0, null);
	}

	protected Future<Response> get(String url, String refererUrl, int maxStale, TimeUnit timeUnit) {
		Request.Builder builder = getRequestBuilder(url, refererUrl);
		builder.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		if (maxStale > 0) {
			builder.cacheControl(new CacheControl.Builder()
					.maxStale(maxStale, timeUnit)
					.build());
		}
		Request request = builder.build();
        return new HttpRequestCommand(request).queue();
	}



}
