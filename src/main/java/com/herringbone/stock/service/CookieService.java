package com.herringbone.stock.service;

import com.herringbone.stock.util.CookieCrumb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Objects;

@Service
@Slf4j
public class CookieService {

    @Value("${quote.scheme}")
    private String scheme;

    @Value("${quote.url}")
    private String host;

    private final RestTemplate restTemplate;

    public CookieService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "cookie", unless = "#result==null or !#result.initialized()")
    public CookieCrumb getCachedCookieCrumb(String actualSymbol) {
        log.info("**************Getting cookie to load into the cache - should only see this when calling to web {}", actualSymbol);
        return getCookieCrumb(actualSymbol);
    }

    //TODO need to lock this code to only allow one caller in and if another caller tries to
    //look at using the trylock. If the trylock fails, fail over to reading from secondary storage
    @CachePut(value = "cookie", unless = "#result==null or !#result.initialized()")
    public CookieCrumb getCookieCrumb(String actualSymbol) {
//        log.info("Getting a fresh copy of the quote {} and loading to the cache", actualSymbol);
        UriComponents uriComponents;
        uriComponents = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(String.format("/quote/%s/",actualSymbol))
                .queryParam("p", actualSymbol)
                .build().encode();
        log.info(uriComponents.toUri().toString());
        String cookie = null;
        String crumb = null;
        try {
            ResponseEntity<String> response
                    = restTemplate.getForEntity(uriComponents.toUri().toString(), String.class);
            String body = response.getBody();
            HttpHeaders headers = response.getHeaders();
            headers.entrySet().forEach(s -> log.info("Headers: key {} value {}", s.getKey(), s.getValue()));
//            try {
                cookie = Objects.requireNonNull(response.getHeaders().get(HttpHeaders.SET_COOKIE)).get(0);
//            } catch (Exception e) {
//
//            }
            log.info("Retrieved Cookie: {} with body: {}", cookie, body);
            assert body != null;
            crumb = Arrays.stream(body.split("}")).filter(s -> s.contains("CrumbStore")).map(s -> {
                String[] vals = s.split(":");
                return vals[2].replace("\"", "");

            }).findFirst().map(s -> {
                log.info(s);
                try {
                    byte[] utf8Bytes = s.getBytes("UTF8");
                    String converted = new String(utf8Bytes, "UTF8");
                    return converted;
                } catch (Exception e) {
                    log.info("failed to getBytes");
                }
                return s;
            }).orElseThrow(RuntimeException::new);
            log.info("Retrieved crumb: {}", crumb);
        } catch (Exception e) {
            log.error("Unable to connect to " + uriComponents.toUri().toString() + " and initialize Crumb Store", e);
        }
        return CookieCrumb.builder().cookie(cookie).crumb(crumb).build();
    }
}
