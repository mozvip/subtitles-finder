package com.github.mozvip.subtitles;

import com.netflix.hystrix.*;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

public class HttpRequestCommand extends HystrixCommand<Response> {

    private static OkHttpClient client;

    static {

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        File directory = new File("cache");
        Cache cache = new Cache(directory, 1024 * 1024 * 250);  // 250 MB

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .cache(cache)
                .readTimeout(30, TimeUnit.SECONDS)
                .cookieJar(new JavaNetCookieJar(cookieManager)).build();
    }

    private Request request;

    public HttpRequestCommand(Request request) {
        super(
                Setter.withGroupKey(
                        HystrixCommandGroupKey.Factory.asKey(request.url().host())
                ).andCommandKey(
                        HystrixCommandKey.Factory.asKey(request.url().toString())
                ).andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionTimeoutInMilliseconds(15 * 1000)
                                .withCircuitBreakerRequestVolumeThreshold(2)
                                .withMetricsRollingStatisticalWindowInMilliseconds(60 * 1000)
                                .withCircuitBreakerSleepWindowInMilliseconds(240 * 1000) // circuit will remain open for 4 minutes
                )
            );
        this.request = request;
    }

    @Override
    protected Response run() {
        try {
            return client.newCall(request).execute();
        } catch( IOException e) {
            throw new RuntimeException(e);
        }
    }

}
