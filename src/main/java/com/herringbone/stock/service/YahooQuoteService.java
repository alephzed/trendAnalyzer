package com.herringbone.stock.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.mapper.DailyQuoteMapper;
import com.herringbone.stock.model.IBasicQuote;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.repository.DailyQuoteRepository;
import com.herringbone.stock.util.CookieCrumb;
import com.herringbone.stock.util.Period;
import com.herringbone.stock.util.ZonedDateTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class YahooQuoteService {
    private final DateTimeFormatter dtfOut = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm:ss.S");

    private final RestTemplate restTemplate;
    private final TickerService tickerService;
    private final CookieService cookieService;
    private final ZonedDateTracker zonedDateTracker;
    private final DailyQuoteRepository dailyQuoteRepository;

    @Value("${quote.scheme}")
    private String scheme;

    @Value("${quote.url}")
    private String host;

    @Value("${quote.historic.url}")
    private String historicUrl;

    public YahooQuoteService(@Qualifier("yahooRestTemplate") RestTemplate restTemplate, TickerService tickerService, CookieService cookieService, ZonedDateTracker zonedDateTracker, DailyQuoteRepository dailyQuoteRepository) {
        this.restTemplate = restTemplate;
        this.tickerService = tickerService;
        this.cookieService = cookieService;
        this.zonedDateTracker = zonedDateTracker;
        this.dailyQuoteRepository = dailyQuoteRepository;
    }

    public YahooQuoteBean getSimpleDailyQuote(String symbol) {
        ZonedDateTime now =
                ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America" +
                        "/New_York"));

        // Determine the last trading day
        ZonedDateTime lastTradingDay =
                zonedDateTracker.getEndOfClosestTradingDayCurrent().truncatedTo(ChronoUnit.DAYS);
        YahooQuoteBean quote = downloadData(symbol, lastTradingDay, now,
                Period.Daily)
                .orElse((List<YahooQuoteBean>) Collections.EMPTY_LIST).stream()
                .reduce((first, second) -> second)
                .orElse(getLastStoredQuote(symbol));
        quote.setLast(quote.getClose());
        return quote;
    }

    private YahooQuoteBean getLastStoredQuote(String symbol) {
        Ticker ticker = tickerService.getTicker(symbol);
        IBasicQuote dailyBasicQuote =
                dailyQuoteRepository.findTop1ByTickerIdOrderByIdDesc(ticker.getId());
        return DailyQuoteMapper.INSTANCE.dailyBasicQuoteToYahooQuote(dailyBasicQuote);
    }

    public Optional<List<YahooQuoteBean>> downloadData(String symbol,
                                                       ZonedDateTime startDate, ZonedDateTime endDate, Period period) {
        Ticker ticker = tickerService.getTicker(symbol);
        String actualSymbol = ticker.getSymbol();
        log.info("Load simple daily quote {}", actualSymbol);
        List<YahooQuoteBean> list = new ArrayList<>();
        CookieCrumb cookieCrumb =
                cookieService.getCachedCookieCrumb(actualSymbol);
        if (null != cookieCrumb.getCookie() && null != cookieCrumb.getCrumb()) {
            URI uri = buildHistoricQuoteUri(actualSymbol, startDate, endDate,
                    period, cookieCrumb.getCrumb());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.COOKIE, cookieCrumb.getCookie());
            org.springframework.http.HttpEntity<Object> httpEntity =
                    new org.springframework.http.HttpEntity<>(headers);
            log.info("Calling uri {} ",uri.toString());
            log.info("Cookie {}", httpEntity.getHeaders());
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(uri
                        , HttpMethod.GET, httpEntity, String.class);
                String response = responseEntity.getBody();
                assert response != null;
                log.info("Response {}", response);
                CsvMapper csvMapper = new CsvMapper();
                CsvSchema schema = CsvSchema.emptySchema().withHeader();
                ObjectReader oReader =
                        csvMapper.readerFor(YahooQuoteBean.class).with(schema);
                try (MappingIterator<YahooQuoteBean> mi =
                             oReader.readValues(Objects.requireNonNull(response).getBytes())) {
                    while (mi.hasNext()) {
                        YahooQuoteBean current = mi.next();
                        current.setSymbol(actualSymbol);
                        current.setDate(ZonedDateTime.parse(current.getDateStr() + " 00:00:00.0", dtfOut.withZone(ZoneId.systemDefault())));
                        list.add(current);
                    }
                }
            } catch (Exception e) {
                log.info("Error getting latest quote, attempt to reload cookie", e);
                cookieService.getCookieCrumb(actualSymbol);
            }
        } else {
            log.error("Not trying to download quote due to uninitialized cookie and crumb");
        }
        return Optional.of(list);
    }


    public URI buildHistoricQuoteUri(String symbol, ZonedDateTime startDate,
                                     ZonedDateTime endDate, Period period,
                                     String crumb) {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(historicUrl)
                .path(String.format("/v7/finance/download/%s", symbol))
                .queryParam("period1",
                        startDate.toInstant().toEpochMilli() / 1000)
                .queryParam("period2",
                        endDate.toInstant().toEpochMilli() / 1000)
                .queryParam("interval", "1" + period.getAbbreviation())
                .queryParam("events", "history")
                .queryParam("crumb", crumb /*getCrumb(symbol)*/)
                .build()
                .encode().toUri();
    }
}
