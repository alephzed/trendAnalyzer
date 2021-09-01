package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.mapper.WeeklyQuoteMapper;
import com.herringbone.stock.model.PeriodTrend;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.model.TrendBase;
import com.herringbone.stock.model.Trendtype;
import com.herringbone.stock.model.WeeklyBasicQuote;
import com.herringbone.stock.model.WeeklyQuote;
import com.herringbone.stock.model.Weeklytrend;
import com.herringbone.stock.repository.WeeklyQuoteRepository;
import com.herringbone.stock.repository.WeeklyTrendRepository;
import com.herringbone.stock.util.ZonedDateTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service("Weekly")
@Slf4j
public class WeeklyQuoteProcessingService implements QuoteLoader {
    private final WeeklyQuoteRepository weeklyQuoteRepository;
    private final WeeklyTrendRepository weeklyTrendRepository;
    private final WeeklyQuoteMapper weeklyQuoteMapper;
    private final ZonedDateTracker dateTracker;

    public WeeklyQuoteProcessingService(WeeklyQuoteRepository weeklyQuoteRepository, WeeklyTrendRepository weeklyTrendRepository, WeeklyQuoteMapper weeklyQuoteMapper, ZonedDateTracker dateTracker) {
        this.weeklyQuoteRepository = weeklyQuoteRepository;
        this.weeklyTrendRepository = weeklyTrendRepository;
        this.weeklyQuoteMapper = weeklyQuoteMapper;
        this.dateTracker = dateTracker;
    }

    @Override
    public WeeklyQuote findLatestQuote(Long tickerId) {
        return weeklyQuoteRepository.findFirstOneByTickerIdOrderByIdDesc(tickerId);
    }

    @Override
    public List findQuotesInRange(Long tickerId, Integer quantity) {
        return weeklyQuoteRepository.findByTickerIdOrderByDateDesc(tickerId, PageRequest.of(0, quantity));
    }

    @Override
    public QuoteBase saveQuote(QuoteBase quote) {
        return weeklyQuoteRepository.saveAndFlush((WeeklyQuote)quote);
    }

    @Override
    public void updateQuote(QuoteBase quote, Long id) {
        WeeklyBasicQuote basicQuote = weeklyQuoteMapper.weeklyQuoteToBasicQuote((WeeklyQuote)quote);
        weeklyQuoteRepository.updateQuote(basicQuote, id);
    }

    @Override
    public QuoteBase findBasicQuote(Long id) {
        return weeklyQuoteRepository.findOne(id);
    }

    @Override
    public PeriodTrend findLatestTrend(Long tickerId) {
       return weeklyTrendRepository.findTop1ByTickerIdOrderByIdDesc(tickerId) ;
    }

    @Override
    public TrendBase saveTrend(PeriodTrend trend) {
        Weeklytrend weeklytrend = (Weeklytrend) trend;
        Weeklytrend previousTrend = Optional.ofNullable(weeklytrend.getPrevioustrend())
                .orElse(Weeklytrend.builder().trendtype(Trendtype.builder().trendvalue(-1).build())
                .ticker(weeklytrend.getTicker()).build());

        WeeklyQuote quoteStart = Optional.ofNullable(weeklytrend.getTrendstart())
                .orElse(WeeklyQuote.builder().close(0.0).ticker(weeklytrend.getTicker()).build());
        WeeklyQuote quoteEnd = weeklytrend.getTrendend();
        Double trendChange = quoteEnd.getClose() - quoteStart.getClose();
        long expectedTrendValue = 0L;
        if (trendChange > 0) {
            expectedTrendValue = 2L;
        } else if (trendChange < 0) {
            expectedTrendValue = 1L;
        }
        if (weeklytrend.getTrendstart() == null) {
            weeklytrend.setTrendstart(weeklytrend.getTrendend());
        }
        if (quoteEnd.getTrendtype().equals(weeklytrend.getTrendtype())
                && quoteEnd.getTrendtype().getTrendvalue() == expectedTrendValue
                && previousTrend.getTrendtype().getTrendvalue() != weeklytrend.getTrendtype().getTrendvalue()) {
            log.info("Inserting a new trend for ticker {} and trend {}", quoteStart.getTicker().getSymbol(), weeklytrend.getTrendtype().getTrendvalue());
            return weeklyTrendRepository.saveAndFlush((Weeklytrend)trend);
        } else {
            log.info("Failed to Insert a new weekly trend for ticker {} ", quoteStart.getTicker().getSymbol());
            throw new RuntimeException("Invalid insert to the trend");
        }
    }

    @Override
    public void updateTrend(PeriodTrend trend, QuoteBase currentQuote) {
        Weeklytrend weeklytrend = (Weeklytrend)trend;
        WeeklyQuote weeklyQuote = (WeeklyQuote)currentQuote;
        if (weeklyQuote.getTrendtype().equals(weeklytrend.getTrendtype()) || weeklyQuote.getTrendtype().getTrendvalue() == 0) {
            weeklyTrendRepository.updateTrend(weeklytrend.getWeeksintrendcount(), weeklytrend.getTrendpercentagechange(),
                    weeklytrend.getTrendpointchange(), weeklyQuote, weeklytrend.getId());
            log.info("Updating trend {} for ticker {} ", weeklytrend.getId(), weeklyQuote.getTicker().getSymbol());
        } else {
            log.info("Failed to Update trend {} for ticker {} ", weeklytrend.getId(), weeklyQuote.getTicker().getSymbol());
            throw new RuntimeException("Invalid update to the trend");
        }
    }

    @Override
    public void updatePreviousTrend(PeriodTrend nextTrend, Long trendId) {
        Weeklytrend weeklytrend = Weeklytrend.builder().id(((Weeklytrend)nextTrend).getId()).build();
        weeklyTrendRepository.updateNextTrend(weeklytrend, trendId);
    }

    @Override
    public Trend getTrend(Ticker ticker, Long directionId) {
        return weeklyTrendRepository.findMatchingTrend(directionId, ticker.getId())
                .stream().findFirst().orElse(Trend.EMPTY);
    }

    @Override
    public boolean quoteGatingRule(YahooQuoteBean yahooQuote,
                                   QuoteBase lastQuote) {
        //TODO figre out error in quotegatingrule for weekly quotes
        if (lastQuote == null) {
            return true;
        }
        ZonedDateTime dateToSave = yahooQuote.getDate();
        ZonedDateTime lastSavedDate = lastQuote.getDate();
        boolean differentDate = dateTracker.isDifferentDate(dateToSave, lastQuote.getDate());
        boolean isAfter = dateTracker.isAfter(dateToSave, lastQuote.getDate());
        boolean isMonday = dateToSave.getDayOfWeek().equals(DayOfWeek.MONDAY);
//        boolean isMonday = dateToSave.getDayOfWeek().equals(DayOfWeek.SUNDAY);
        boolean notInCurrentWeek = !dateTracker.inCurrentWeek(dateToSave);
        log.info("Proposing to save {} as the next date after {}", dateToSave, lastQuote.getDate());
        if (!differentDate) {
            log.info("Saving date {} not different from previous date {}", dateToSave, lastQuote.getDate());
        }
        if (!isAfter) {
            log.info("Saving date {} not after previous date {}", dateToSave, lastQuote.getDate());
        }
        if (!isMonday) {
            //TODO weekly quotes are not Mondays apparently. It seems that they are Sundays.
            log.info("Saving date {} is not Sunday", yahooQuote.getDate().getDayOfWeek());
        }
        if (!notInCurrentWeek) {
            log.info("Save date {} is in the current week", yahooQuote.getDate());
        }
        boolean gate = differentDate && isAfter && isMonday && notInCurrentWeek;
        log.info("Gate for quote {} is {}", dateToSave, true);
        //TODO add another gating rule to make sure the number of days between is exactly 7.
        return gate;
    }
}
