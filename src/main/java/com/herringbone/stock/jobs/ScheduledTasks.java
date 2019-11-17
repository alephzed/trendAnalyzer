package com.herringbone.stock.jobs;

import com.herringbone.stock.components.HistoricalQuoteLoader;
import com.herringbone.stock.controller.SchedulingController;
import com.herringbone.stock.domain.RangeDetail;
import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.service.BucketService;
import com.herringbone.stock.service.CookieService;
import com.herringbone.stock.service.StockTrendService;
import com.herringbone.stock.service.YahooQuoteService;
import com.herringbone.stock.util.CookieCrumb;
import com.herringbone.stock.util.Period;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import static com.herringbone.stock.configuration.WebSocketConfig.MESSAGE_PREFIX;

@Component
//@Profile("!local")
@Slf4j
public class ScheduledTasks {

    public static final int SOCKET_PUBLISH_FIXED_RATE = 300000;

    @Value("${yahoo.index.symbol}")
    private String yahooIndexSymbol;

    @Value("${yahoo.index.full.symbol}")
    private String yahooIndexFullSymbol;

    private final SchedulingController schedulingController;
    private final YahooQuoteService yahooQuoteService;
    private final StockTrendService trendService;
    private final SimpMessagingTemplate websocket;
    private final BucketService bucketService;
    private final HistoricalQuoteLoader historicalQuoteLoader;
    private final CookieService cookieService;

    public ScheduledTasks(SchedulingController schedulingController,
                          YahooQuoteService yahooQuoteService,
                          StockTrendService trendService,
                          SimpMessagingTemplate websocket,
                          BucketService bucketService,
                          HistoricalQuoteLoader historicalQuoteLoader,
                          CookieService cookieService) {
        this.schedulingController = schedulingController;
        this.yahooQuoteService = yahooQuoteService;
        this.trendService = trendService;
        this.websocket = websocket;
        this.bucketService = bucketService;
        this.historicalQuoteLoader = historicalQuoteLoader;
        this.cookieService = cookieService;
    }

    @Scheduled(cron = "0 15 14 ? * MON-FRI", zone = "America/Denver")
    public void loadDailyQuote() {
        log.info("Processing dailyquote");
        schedulingController.dailyQuoteJob(yahooIndexSymbol);
    }

    @Scheduled(cron = "0 30 14 ? * FRI", zone = "America/Denver")
    public void loadWeeklyQuote() {
        log.info("Processing weeklyquote");
        schedulingController.weeklyQuoteJob(yahooIndexSymbol);
        log.info("Processing weeklyquote done");
    }

    @Scheduled(cron = "0 15 6 ? * MON-FRI", zone = "America/Denver")
    public void evictCache() {
        trendService.evictAllCacheValues();
    }

    @Scheduled(cron = "0 45 14 28-31 * ?", zone = "America/Denver")
    public void loadMonthlyQuote() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        ZonedDateTime endOfMonth =
                dateTime.with(TemporalAdjusters.lastDayOfMonth());
        if (dateTime.equals(endOfMonth)) {
            log.info("Processing monthlyquote");
            schedulingController.monthlyQuoteJob(yahooIndexSymbol);
        }
    }

    @Scheduled(fixedRate = 86400000, initialDelay = 86400000)
    public void reloadCookie() {
        log.info("trying to Reload cookie");
        CookieCrumb cookieCrumb =
                cookieService.getCookieCrumb(yahooIndexFullSymbol);
        log.info("Retrieved cookiecrumb {} {}", cookieCrumb.getCookie(),
                cookieCrumb.getCrumb());
    }

    @Scheduled(fixedRate = 30000, initialDelay = 60000)
    public void getCurrentQuote() {
        log.info("Processing current quote");

        YahooQuoteBean quote =
                yahooQuoteService.getSimpleDailyQuote(yahooIndexFullSymbol);
        //If we can't retrieve a quote, then no need to generate a websocket
        // event
        if (quote != null && quote.getLast() != null && quote.getLast() > 0) {

            //TODO figure out what to do with the matched ranges, currently
            // the UI is not using this topic
            // take the quote and check which bucket if falls into for the
            // trends
//            Trend dailyUpTrend = trendService.getLatestTrend
//            (yahooIndexSymbol, "Up", Period.Daily, 1);
//            Trend dailyDownTrend = trendService.getLatestTrend
//            (yahooIndexSymbol, "Down", Period.Daily, 1);
//            Trend weeklyUpTrend = trendService.getLatestTrend
//            (yahooIndexSymbol, "Up", Period.Weekly, 1);
//            Trend weeklyDownTrend = trendService.getLatestTrend
//            (yahooIndexSymbol, "Down", Period.Weekly, 1);
//            Trend monthlyUpTrend = trendService.getLatestTrend
//            (yahooIndexSymbol, "Up", Period.Monthly, 1);
//            Trend monthlyDownTrend = trendService.getLatestTrend
//            (yahooIndexSymbol, "Down", Period.Monthly, 1);
//
//            Map<String, RangeDetail> matchedRanges = new HashMap<>();
//            double lastQuote = quote.getLast();
//            RangeDetail dailyUp = evaluateTrend(dailyUpTrend, lastQuote);
//            matchedRanges.put("dailyUp", dailyUp);
//            RangeDetail dailyDown = evaluateTrend(dailyDownTrend, lastQuote);
//            matchedRanges.put("dailyDown", dailyDown);
//            RangeDetail weeklyUp = evaluateTrend(weeklyUpTrend, lastQuote);
//            matchedRanges.put("weeklyUp", weeklyUp);
//            RangeDetail weeklyDown = evaluateTrend(weeklyDownTrend,
//            lastQuote);
//            matchedRanges.put("weeklyDown", weeklyDown);
//            RangeDetail monthlyUp = evaluateTrend(monthlyUpTrend, lastQuote);
//            matchedRanges.put("monthlyUp", monthlyUp);
//            RangeDetail monthlyDown = evaluateTrend(monthlyDownTrend,
//            lastQuote);
//            matchedRanges.put("monthlyDown", monthlyDown);

            this.websocket.convertAndSend(
                    MESSAGE_PREFIX + "/lastfullquote", quote);
//            this.websocket.convertAndSend(
//                    MESSAGE_PREFIX + "/trends", matchedRanges);
        } else {
            log.info("Quote was not retrieved. Not sending websocket event.");
        }
    }

    @Scheduled(fixedRate = SOCKET_PUBLISH_FIXED_RATE, initialDelay = 6000)
    public void processHistoricalQuotes() {
        log.info("Load historical quotes");
        historicalQuoteLoader.processHistoricalQuotes();
    }

    @Scheduled(fixedRate = SOCKET_PUBLISH_FIXED_RATE, initialDelay = 60000)
    public void getQuoteBuckets() {
        try {
            Map buckets = bucketService.getCurrentBuckets(yahooIndexSymbol);
            this.websocket.convertAndSend(
                    MESSAGE_PREFIX + "/quotebuckets", buckets);
        } catch (Exception e) {
            log.info("Error with current buckets",e);
        }
    }

    @Scheduled(fixedRate = SOCKET_PUBLISH_FIXED_RATE, initialDelay = 60000)
    public void getDailyUpTrend() {
        try {
            Trend dailyUpTrend = trendService.getLatestTrend(yahooIndexSymbol,
                    "Up", Period.Daily, 1);
            this.websocket.convertAndSend(MESSAGE_PREFIX + "/dailyUp",
                    dailyUpTrend);
        } catch(Exception e) {
            log.info("Unable to get dailyuptrend", e);
        }
    }

    @Scheduled(fixedRate = SOCKET_PUBLISH_FIXED_RATE, initialDelay = 60000)
    public void getWeeklyUpTrend() {
        try {
            Trend weeklyUpTrend = trendService.getLatestTrend(yahooIndexSymbol,
                    "Up", Period.Weekly, 1);
            this.websocket.convertAndSend(MESSAGE_PREFIX + "/weeklyUp",
                    weeklyUpTrend);
        } catch (Exception e) {
            log.info("Unable to get weeklyuptrend", e);

        }
    }

    @Scheduled(fixedRate = SOCKET_PUBLISH_FIXED_RATE, initialDelay = 60000)
    public void getMonthlyUpTrend() {
        try {
            int precision = 1;
            Trend monthlyUpTrend = trendService.getLatestTrend(yahooIndexSymbol,
                    "Up", Period.Monthly, precision);
            while (monthlyUpTrend.getRanges().size() == 1) {
                monthlyUpTrend = trendService.getLatestTrend(yahooIndexSymbol,
                        "Up", Period.Monthly, precision++);
            }
            this.websocket.convertAndSend(MESSAGE_PREFIX + "/monthlyUp",
                    monthlyUpTrend);
        } catch (Exception e) {
            log.info("Unable to get monthlyuptrend", e);

        }
    }

    @Scheduled(fixedRate = SOCKET_PUBLISH_FIXED_RATE, initialDelay = 60000)
    public void getDailyDownTrend() {
        try {
            Trend dailyDownTrend = trendService.getLatestTrend(yahooIndexSymbol,
                    "Down", Period.Daily, 1);
            this.websocket.convertAndSend(MESSAGE_PREFIX + "/dailyDown",
                    dailyDownTrend);
        } catch (Exception e) {
            log.info("Unable to get dailyDownTrend", e);
        }
    }

    @Scheduled(fixedRate = SOCKET_PUBLISH_FIXED_RATE, initialDelay = 60000)
    public void getWeeklyDownTrend() {
        try {
            Trend weeklyDownTrend = trendService.getLatestTrend(yahooIndexSymbol,
                    "Down", Period.Weekly, 1);
            this.websocket.convertAndSend(MESSAGE_PREFIX + "/weeklyDown",
                    weeklyDownTrend);
        } catch (Exception e) {
            log.info("Unable to get weeklyDownTrend", e);
        }
    }

    @Scheduled(fixedRate = SOCKET_PUBLISH_FIXED_RATE, initialDelay = 60000)
    public void getMonthlyDownTrend() {
        try {
            Trend monthlyDownTrend = trendService.getLatestTrend(yahooIndexSymbol
                    , "Down", Period.Monthly, 1);
            this.websocket.convertAndSend(MESSAGE_PREFIX + "/monthlyDown",
                    monthlyDownTrend);
        } catch (Exception e) {
            log.info("Unable to get monthlyDownTrend", e);

        }
    }

    @Scheduled(fixedRate = 600000)
    public void healthCheck() {
        String url = "https://trendapp123.herokuapp.com/";
        try {
            URL obj = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            log.info("ResponseCode {}", responseCode);
        } catch (IOException e) {
            log.error("Unable to connect", e);
        }

    }

    private RangeDetail evaluateTrend(Trend trend, double lastQuote) {
        List<RangeDetail> ranges = trend.getRanges();
        Period period = trend.getPeriod();
        RangeDetail matchedRangeDetail = null;
        for (RangeDetail detail : ranges) {
            if (lastQuote >= detail.getRangeBottom() && lastQuote <= detail.getRangeTop()) {
                log.info("Last Price: {} TrendType: {} {} Description: {}",
                        lastQuote, period.name(), detail.toString(),
                        trend.getDescription());
                matchedRangeDetail = detail;
                break;
            }
        }
        return matchedRangeDetail;
    }

}
