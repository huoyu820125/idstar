package com.github.huoyu820125.idstar.http.core;

import com.github.huoyu820125.idstar.http.Http;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * http操作client
 * @author SunQian
 * @version 1.1
 */
public class HttpClient {
    private static HttpClient client;
    public static org.apache.http.client.HttpClient instance() {
        if (null != client) {
            return client.httpClient;
        }

        synchronized (Http.class) {
            if (null != client) {
                return client.httpClient;
            }

            client = new HttpClient();
        }

        return client.httpClient;
    }

    public static RequestConfig config() {
        if (null != client) {
            return client.requestConfig;
        }

        synchronized (Http.class) {
            if (null != client) {
                return client.requestConfig;
            }

            client = new HttpClient();
        }

        return client.requestConfig;
    }

    private org.apache.http.client.HttpClient httpClient = null;
    private RequestConfig requestConfig = null;

    private HttpClient() {
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(30 * 1000)
                .setConnectTimeout(5 * 1000)
                .setConnectionRequestTimeout(3 * 1000)
                .build();

        httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(20)
                .setUserAgent("user-agent")
                .build();
    }
}
