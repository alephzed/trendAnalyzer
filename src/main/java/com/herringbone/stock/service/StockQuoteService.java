package com.herringbone.stock.service;

import com.herringbone.stock.mapper.DailyQuoteMapper;
import com.herringbone.stock.mapper.MonthlyQuoteMapper;
import com.herringbone.stock.mapper.WeeklyQuoteMapper;
import com.herringbone.stock.model.DailyQuote;
import com.herringbone.stock.model.MonthlyQuote;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.model.WeeklyQuote;
import com.herringbone.stock.repository.DailyQuoteRepository;
import com.herringbone.stock.repository.DailyTrendRepository;
import com.herringbone.stock.repository.MonthlyQuoteRepository;
import com.herringbone.stock.repository.MonthlyTrendRepository;
import com.herringbone.stock.repository.WeeklyQuoteRepository;
import com.herringbone.stock.repository.WeeklyTrendRepository;
import com.herringbone.stock.util.Period;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StockQuoteService {

    private final DailyQuoteRepository dailyQuoteRepository;
    private final DailyTrendRepository dailyTrendRepository;
    private final WeeklyQuoteRepository weeklyQuoteRepository;
    private final WeeklyTrendRepository weeklyTrendRepository;
    private final MonthlyQuoteRepository monthlyQuoteRepository;
    private final MonthlyTrendRepository monthlyTrendRepository;
    private final TickerService tickerService;

    public StockQuoteService(DailyQuoteRepository dailyQuoteRepository, DailyTrendRepository dailyTrendRepository,
                             WeeklyQuoteRepository weeklyQuoteRepository, WeeklyTrendRepository weeklyTrendRepository
            , MonthlyQuoteRepository monthlyQuoteRepository, MonthlyTrendRepository monthlyTrendRepository,
                             TickerService tickerService) {
        this.dailyQuoteRepository = dailyQuoteRepository;
        this.dailyTrendRepository = dailyTrendRepository;
        this.weeklyQuoteRepository = weeklyQuoteRepository;
        this.weeklyTrendRepository = weeklyTrendRepository;
        this.monthlyQuoteRepository = monthlyQuoteRepository;
        this.monthlyTrendRepository = monthlyTrendRepository;
        this.tickerService = tickerService;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<DailyQuote> getHistoricalQuote(String symbol, Integer limit) {
        Ticker ticker = tickerService.getTicker(symbol);
        List<DailyQuote> quotes = dailyQuoteRepository.findByTickerIdOrderByDateDesc(ticker.getId(), PageRequest.of(0, limit));
        for (DailyQuote quote : quotes) {
            Hibernate.initialize(quote.getPrevday());
            Hibernate.initialize(quote.getNextday());
        }
        return quotes;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<QuoteBase> getHistoricalQuote(String period, String symbol, Integer limit) {
        Ticker ticker = tickerService.getTicker(symbol);
        List<QuoteBase> historicalQuotes = new ArrayList<>();
        if (StringUtils.equalsIgnoreCase(period, "weekly")) {
            List<WeeklyQuote> quotes = weeklyQuoteRepository.findByTickerIdOrderByDateDesc(ticker.getId(), PageRequest.of(0, limit));
            for (WeeklyQuote quote : quotes) {
                QuoteBase mappedQuote = WeeklyQuoteMapper.INSTANCE.weeklyQuoteBaseToQuoteBase(quote);
                historicalQuotes.add(mappedQuote);
            }
        } else if (StringUtils.equalsIgnoreCase(period, "daily")) {
            List<DailyQuote> quotes = dailyQuoteRepository.findByTickerIdOrderByDateDesc(ticker.getId(), PageRequest.of(0, limit));
            for (DailyQuote quote : quotes) {
                QuoteBase mappedQuote = DailyQuoteMapper.INSTANCE.dailyQuoteToQuoteBase(quote);
                historicalQuotes.add(mappedQuote);
            }
        } else if (StringUtils.equalsIgnoreCase(period, "monthly")) {
            List<MonthlyQuote> quotes = monthlyQuoteRepository.findByTickerIdOrderByDateDesc(ticker.getId(), PageRequest.of(0, limit));
            for (MonthlyQuote quote : quotes) {
                QuoteBase mappedQuote = MonthlyQuoteMapper.INSTANCE.monthlyQuoteToQuoteBase(quote);
                historicalQuotes.add(mappedQuote);
            }
        }
        return historicalQuotes;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public DailyQuote getQuote(String symbol, Long id) {
        Ticker ticker = tickerService.getTicker(symbol);
        DailyQuote current = dailyQuoteRepository.findByTickerIdAndId(ticker.getId(), id, PageRequest.of(0, 1)).get(0);
        Hibernate.initialize(current.getPrevday());
        Hibernate.initialize(current.getNextday());
        return current;
    }

    public void deleteQuote(String symbol, String period) {
        log.info("Deleting {} for {}", symbol, period);
        if (period.equals(Period.Weekly.name())) {
            weeklyTrendRepository.deleteAll();
            weeklyQuoteRepository.deleteAll();
        } else if (period.equals(Period.Monthly.name())) {
            monthlyTrendRepository.deleteAll();
            monthlyQuoteRepository.deleteAll();
        } else if (period.equals(Period.Daily.name())) {
            dailyTrendRepository.deleteAll();
            dailyQuoteRepository.deleteAll();
        }
    }
}
