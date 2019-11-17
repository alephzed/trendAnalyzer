package com.herringbone.stock.components;

import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.exception.QuoteLoadingServiceException;
import com.herringbone.stock.mapper.DailyQuoteMapper;
import com.herringbone.stock.mapper.MonthlyQuoteMapper;
import com.herringbone.stock.mapper.WeeklyQuoteMapper;
import com.herringbone.stock.model.DailyQuote;
import com.herringbone.stock.model.Dailytrend;
import com.herringbone.stock.model.MonthlyQuote;
import com.herringbone.stock.model.Monthlytrend;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.model.WeeklyQuote;
import com.herringbone.stock.model.Weeklytrend;
import com.herringbone.stock.repository.DailyQuoteRepository;
import com.herringbone.stock.repository.MonthlyQuoteRepository;
import com.herringbone.stock.repository.WeeklyQuoteRepository;
import com.herringbone.stock.service.QuoteProcessingService;
import com.herringbone.stock.service.StockTrendService;
import com.herringbone.stock.service.TickerService;
import com.herringbone.stock.service.YahooQuoteService;
import com.herringbone.stock.util.Period;
import com.herringbone.stock.util.ZonedDateTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Lazy
@Component
@Slf4j
public class HistoricalQuoteLoader {

    private static final DateTimeFormatter dtfOut = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    @Value("${web.dailyFile}")
    private String dailyFile;

    @Value("${web.weeklyFile}")
    private String weeklyFile;

    @Value("${web.monthlyFile}")
    private String monthlyFile;

    @Value("${yahoo.index.symbol}")
    private String yahooIndexSymbol;

    @Value("${yahoo.index.full.symbol}")
    private String yahooIndexFullSymbol;

    private final DailyQuoteRepository dailyQuoteRepository;
    private final WeeklyQuoteRepository weeklyQuoteRepository;
    private final MonthlyQuoteRepository monthlyQuoteRepository;
    private final TickerService tickerService;
    private final YahooQuoteService yahooQuoteService;
    private final QuoteProcessingService quoteProcessingService;
    private final Environment environment;
    private final ZonedDateTracker dateTracker;
    private final StockTrendService stockTrendService;

    public HistoricalQuoteLoader(DailyQuoteRepository dailyQuoteRepository,
                                 WeeklyQuoteRepository weeklyQuoteRepository,
                                 MonthlyQuoteRepository monthlyQuoteRepository,
                                 TickerService tickerService, YahooQuoteService yahooQuoteService,
                                 QuoteProcessingService quoteProcessingService,
                                 Environment environment, ZonedDateTracker dateTracker, StockTrendService stockTrendService) {
        this.dailyQuoteRepository = dailyQuoteRepository;
        this.weeklyQuoteRepository = weeklyQuoteRepository;
        this.monthlyQuoteRepository = monthlyQuoteRepository;
        this.tickerService = tickerService;
        this.yahooQuoteService = yahooQuoteService;
        this.quoteProcessingService = quoteProcessingService;
        this.environment = environment;
        this.dateTracker = dateTracker;
        this.stockTrendService = stockTrendService;
    }

    @Async
    public void processHistoricalQuotes() {
        String[] active = environment.getActiveProfiles();
        if (Arrays.asList(active).contains("test")) {
            processQuoteFromFile(yahooIndexSymbol);
            processQuoteFromWeb(yahooIndexFullSymbol);
        } else {
            processQuoteFromWeb(yahooIndexFullSymbol);
        }
        //TODO only evict the cache if new values have been saved
        stockTrendService.evictAllCacheValues();
    }

    //TODO - some weirdness happening when saving historical quotes vs quotes not from historical quotes
    private void processQuoteFromWeb(String symbol) {
        Ticker ticker = tickerService.getTicker(symbol);
        DailyQuote dailyQuote = dailyQuoteRepository.findFirstOneByTickerIdOrderByIdDesc(ticker.getId());
        ZonedDateTime lastStoredDate = dailyQuote == null ? ticker.getStartdate() : dailyQuote.getDate();

        loadHistoricalDataForPeriod(yahooIndexFullSymbol, lastStoredDate, dateTracker.getEndOfClosestTradingDayCurrent(), Period.Daily);
        if (!dateTracker.isMarketOpen(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/New_York")))) {
            YahooQuoteBean quote = yahooQuoteService.getSimpleDailyQuote("^GSPC");
            quoteProcessingService.processQuote(quote,
                    () -> DailyQuoteMapper.INSTANCE.yahooQuoteToDailyQuote(quote), Period.Daily, Dailytrend.class);
        }
        ZonedDateTime endDate = dateTracker.getEndOfClosestTradingWeekCurrent();
        WeeklyQuote weeklyQuote = weeklyQuoteRepository.findFirstOneByTickerIdOrderByIdDesc(ticker.getId());
        lastStoredDate = weeklyQuote == null ? ticker.getStartdate() : weeklyQuote.getDate();
        loadHistoricalDataForPeriod(yahooIndexFullSymbol, lastStoredDate, endDate, Period.Weekly);
        endDate = dateTracker.getEndOfClosestTradingMonthCurrent();
        MonthlyQuote monthlyQuote = monthlyQuoteRepository.findFirstOneByTickerIdOrderByIdDesc(ticker.getId());
        lastStoredDate = monthlyQuote == null ? ticker.getStartdate() : monthlyQuote.getDate();
        loadHistoricalDataForPeriod(yahooIndexFullSymbol, lastStoredDate, endDate, Period.Monthly);
        //TODO return true if any new quotes were stored
    }

    private void loadHistoricalDataForPeriod(String symbol, ZonedDateTime startDate, ZonedDateTime endDate, Period period) {
        List<YahooQuoteBean> quotes = yahooQuoteService.downloadData(symbol, startDate, endDate, period).orElse(new ArrayList<>());
        quotes.forEach(
                q -> {
                    //TODO switch this to a factory
                    switch (period) {
                        case Daily:
                            quoteProcessingService.processQuote(q,
                                    () -> DailyQuoteMapper.INSTANCE.yahooQuoteToDailyQuote(q), Period.Daily, Dailytrend.class);
                            break;
                        case Weekly:
                            quoteProcessingService.processQuote(q,
                                    () -> WeeklyQuoteMapper.INSTANCE.yahooQuoteToWeeklyQuote(q), Period.Weekly, Weeklytrend.class);
                            break;
                        case Monthly:
                            quoteProcessingService.processQuote(q,
                                    () -> MonthlyQuoteMapper.INSTANCE.yahooQuoteToMonthlyQuote(q), Period.Monthly, Monthlytrend.class);
                    }
                }
        );
    }

    private void processQuoteFromFile(String symbol) {
        try {
            URI dailyURI = ClassLoader.getSystemResource(dailyFile).toURI();
            try (Stream<String> stream = Files.lines(Paths.get(dailyURI)).skip(1)) {
                Iterator<YahooQuoteBean> reversedStream = stream.map(mapToQuote).collect(Collectors.toCollection
                        (LinkedList::new)).descendingIterator();
                while (reversedStream.hasNext()) {
                    YahooQuoteBean quoteBean = reversedStream.next();
                    quoteBean.setSymbol(symbol);
                    log.info("Loading daily: " + quoteBean.getDate());
                    final Supplier<QuoteBase> supplier =
                            () -> DailyQuoteMapper.INSTANCE.yahooQuoteToDailyQuote(quoteBean);
                    quoteProcessingService.processQuote(quoteBean, supplier, Period.Daily, Dailytrend.class);
                }
            }
            URI weeklyURI = ClassLoader.getSystemResource(weeklyFile).toURI();
            try (Stream<String> stream = Files.lines(Paths.get(weeklyURI)).skip(1)) {
                Iterator<YahooQuoteBean> reversedStream = stream.map(mapToQuote).collect(Collectors.toCollection
                        (LinkedList::new)).descendingIterator();
                while (reversedStream.hasNext()) {
                    YahooQuoteBean quoteBean = reversedStream.next();
                    quoteBean.setSymbol(symbol);
                    log.info("Loading weekly: " + quoteBean.getDate());
                    final Supplier<QuoteBase> supplier =
                            () -> WeeklyQuoteMapper.INSTANCE.yahooQuoteToWeeklyQuote(quoteBean);
                    quoteProcessingService.processQuote(quoteBean, supplier, Period.Weekly, Weeklytrend.class);
                }
            }
            URI monthlyURI = ClassLoader.getSystemResource(monthlyFile).toURI();
            try (Stream<String> stream = Files.lines(Paths.get(monthlyURI)).skip(1)) {
                Iterator<YahooQuoteBean> reversedStream = stream.map(mapToQuote).collect(Collectors.toCollection
                        (LinkedList::new)).descendingIterator();
                while (reversedStream.hasNext()) {
                    YahooQuoteBean quoteBean = reversedStream.next();
                    quoteBean.setSymbol(symbol);
                    log.info("Loading monthly: " + quoteBean.getDate());
                    final Supplier<QuoteBase> supplier =
                            () -> WeeklyQuoteMapper.INSTANCE.yahooQuoteToWeeklyQuote(quoteBean);
                    quoteProcessingService.processQuote(quoteBean, supplier, Period.Monthly, Monthlytrend.class);
                }
            }
        } catch( URISyntaxException|IOException e) {
            throw new QuoteLoadingServiceException(e);
        }
    }

    private static final Function<String, YahooQuoteBean> mapToQuote = line -> {
        String[] p = line.split(",");
        return YahooQuoteBean.builder()
                .date(ZonedDateTime.parse(p[0] + " 00:00:00.0", dtfOut.withZone(ZoneId.systemDefault())))
                .open(Double.parseDouble(p[1]))
                .high(Double.parseDouble(p[2]))
                .low(Double.parseDouble(p[3]))
                .close(Double.parseDouble(p[4]))
                .volume(Long.parseLong(p[5]))
                .adjClose(Double.parseDouble(p[6]))
                .build();
    };
}
