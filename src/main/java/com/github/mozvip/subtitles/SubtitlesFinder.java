package com.github.mozvip.subtitles;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.MatchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class SubtitlesFinder {

	private OkHttpClient client;

	public SubtitlesFinder() {
		
		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		
		client = new OkHttpClient.Builder()
				.connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.cookieJar(new JavaNetCookieJar(cookieManager)).build();
	}

	protected String extractNameFromShowName(String name) {
		try (Scanner scanner = new Scanner(name)) {
			if (scanner.findInLine("(.*)\\s+\\((19|20\\d{2})\\)") != null) {
				MatchResult match = scanner.match();
				name = match.group(1);
			}
		}
		return name;
	}

	public Document getDocument(String url) throws IOException {
		return getDocument(url, null);
	}

	public Document getDocument(String url, String refererUrl) throws IOException {

		Response response = get(url, refererUrl);
		return Jsoup.parse(response.body().string(), url);
	}

	public byte[] getBytes(String url, String refererUrl) throws IOException {

		Response response = get(url, refererUrl);
		return response.body().bytes();
	}

	protected void submit(Element formElement, String... params) throws IOException {
		post(formElement.absUrl("action"), formElement.baseUri(), params);
	}

	public Response post(String url, String refererUrl, String... params) throws IOException {
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

		Response response = client.newCall(request).execute();
		return response;
	}

	private Request.Builder getRequestBuilder(String url, String refererUrl) {
		Request.Builder builder = new Request.Builder().url(url);
		builder.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
		if (refererUrl != null) {
			builder.addHeader("Referer", refererUrl);
		}
		return builder;
	}

	protected Response get(String url, String refererUrl) throws IOException {
		Request.Builder builder = getRequestBuilder(url, refererUrl);
		Request request = builder.build();

		Response response = client.newCall(request).execute();
		return response;
	}

}
