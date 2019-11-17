package com.herringbone.stock.controller;

import com.herringbone.stock.components.HistoricalQuoteLoader;
import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.DailyQuote;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.service.StockQuoteService;
import com.herringbone.stock.service.StockTrendService;
import com.herringbone.stock.service.YahooQuoteService;
import com.herringbone.stock.util.Period;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quote")
public class QuoteController {

    private final StockQuoteService stockQuoteService;
    private final StockTrendService stockTrendService;
    private final HistoricalQuoteLoader historicalQuoteLoader;
    private final YahooQuoteService yahooQuoteService;

    public QuoteController(StockQuoteService stockQuoteService, StockTrendService stockTrendService, HistoricalQuoteLoader historicalQuoteLoader, YahooQuoteService yahooQuoteService) {
        this.stockQuoteService = stockQuoteService;
        this.stockTrendService = stockTrendService;
        this.historicalQuoteLoader = historicalQuoteLoader;
        this.yahooQuoteService = yahooQuoteService;
    }

    @GetMapping(value="/{symbol}/{limit}")
    public @ResponseBody
    List<DailyQuote> list(@PathVariable("symbol") String symbol, @PathVariable("limit") Integer limit) {
        return stockQuoteService.getHistoricalQuote(symbol, limit);
    }

    @GetMapping(value="/{period}/{symbol}/{limit}")
    public @ResponseBody
    List<QuoteBase> historicalList(@PathVariable("period") String period, @PathVariable("symbol") String symbol, @PathVariable("limit") Integer limit) {
        return stockQuoteService.getHistoricalQuote(period, symbol, limit);
    }

    @GetMapping(value="/trend/{symbol}/{period}/{direction}/{precision}")
    public @ResponseBody
    Trend trend(@PathVariable("symbol") String symbol, @PathVariable("period") String period, @PathVariable("direction") String direction, @PathVariable("precision") Integer precision) {
        return stockTrendService.getLatestTrend(symbol, direction, Period.valueOf(period), precision);
    }

    @GetMapping(value="/{symbol}/id/{id}")
    public @ResponseBody
    DailyQuote get(@PathVariable("symbol") String symbol, @PathVariable("id") Long id) {
        return stockQuoteService.getQuote(StringUtils.upperCase(symbol), id);
    }

    @RequestMapping(value="/index/{symbol}", method = RequestMethod.GET)
    public YahooQuoteBean getIndex(@PathVariable("symbol") String symbol) {
        return yahooQuoteService.getSimpleDailyQuote(symbol);
    }

    @DeleteMapping(value="/{period}/{symbol}")
    public void deleteQuote(@PathVariable("period") String period, @PathVariable("symbol") String symbol) {
        stockQuoteService.deleteQuote(symbol, period);
    }

    @PostMapping(value ="/historical")
    public void loadHistoricalQuotes() {
        historicalQuoteLoader.processHistoricalQuotes();
    }
}
