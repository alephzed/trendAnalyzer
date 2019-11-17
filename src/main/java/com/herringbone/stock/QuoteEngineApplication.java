package com.herringbone.stock;

import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class QuoteEngineApplication {

    private int webserviceTimeout = 20000;

    private int maxTotal = 1;

    public static void main(String[] args) {
        SpringApplication.run(QuoteEngineApplication.class, args);
    }

    @Bean
    @Qualifier("yahooRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().requestFactory(this::clientHttpRequestFactory)
                .build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient());
        return requestFactory;
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig =
                RequestConfig.custom().setTargetPreferredAuthSchemes(Collections.singletonList(AuthSchemes.NTLM))
                        .setConnectionRequestTimeout(webserviceTimeout).setConnectTimeout(webserviceTimeout)
                        .setSocketTimeout(webserviceTimeout)
                        .build();

        SocketConfig socketConfig =
                SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
        return HttpClientBuilder
                .create()
                .setConnectionManager(poolingConnectionManager())
                .setConnectionManagerShared(true)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultSocketConfig(socketConfig)
                .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
                new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(maxTotal);
        return poolingHttpClientConnectionManager;
    }
}
