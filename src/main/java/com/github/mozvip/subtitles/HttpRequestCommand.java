package com.github.mozvip.subtitles;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

public class HttpRequestCommand extends HystrixCommand<Response> {

    private static OkHttpClient client;

    static {

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cookieJar(new JavaNetCookieJar(cookieManager)).build();
    }

    private Request request;

    public HttpRequestCommand(Request request) {
        super(
                Setter.withGroupKey(
                        HystrixCommandGroupKey.Factory.asKey(request.url().host())
                ).andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionTimeoutInMilliseconds(30 * 1000)
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
