package com.herringbone.stock.controller;

import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.mapper.DailyQuoteMapper;
import com.herringbone.stock.mapper.MonthlyQuoteMapper;
import com.herringbone.stock.mapper.WeeklyQuoteMapper;
import com.herringbone.stock.model.Dailytrend;
import com.herringbone.stock.model.Monthlytrend;
import com.herringbone.stock.model.Weeklytrend;
import com.herringbone.stock.service.QuoteProcessingService;
import com.herringbone.stock.service.YahooQuoteService;
import com.herringbone.stock.util.Period;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cron")
@Slf4j
public class SchedulingController {

    private final QuoteProcessingService quoteProcessingService;
	private final YahooQuoteService yahooQuoteService;

	public SchedulingController(QuoteProcessingService quoteProcessingService, YahooQuoteService yahooQuoteService) {
		this.quoteProcessingService = quoteProcessingService;
		this.yahooQuoteService = yahooQuoteService;
	}

    @RequestMapping(value="/dailyQuoteJob/{symbol}", method = RequestMethod.GET)
    public boolean dailyQuoteJob(@PathVariable String symbol) {
		YahooQuoteBean quote = yahooQuoteService.getSimpleDailyQuote(symbol);
        // Daily job to load the end of day quote
		if (quote.getSymbol() != null) {
			quoteProcessingService.processQuote(quote, () -> DailyQuoteMapper.INSTANCE.yahooQuoteToDailyQuote(quote), Period.Daily, Dailytrend.class);
			return true;
		} else {
			log.info("Unable to run dailyQuoteJob. Download quote failed. Retry later");
			return false;
		}
    }

	@RequestMapping(value="/weeklyQuoteJob/{symbol}", method = RequestMethod.GET)
	public boolean weeklyQuoteJob(@PathVariable String symbol) {
		YahooQuoteBean quote = yahooQuoteService.getSimpleDailyQuote(symbol);
		// Weekly job to load the end of week quote
		if (quote.getSymbol() != null) {
			quoteProcessingService.processQuote(quote, () -> WeeklyQuoteMapper.INSTANCE.yahooQuoteToWeeklyQuote(quote), Period.Weekly, Weeklytrend.class);
			return true;
		} else {
			log.info("Unable to run dailyQuoteJob. Download quote failed. Retry later");
			return false;
		}
	}

	@RequestMapping(value="/monthlyQuoteJob/{symbol}", method = RequestMethod.GET)
	public boolean monthlyQuoteJob(@PathVariable String symbol) {
		//TODO - pass in as a parameter and and encode it
		YahooQuoteBean quote = yahooQuoteService.getSimpleDailyQuote(symbol);
		// Monthly job to load the end of week quote
		if (quote.getSymbol() != null) {
			quoteProcessingService.processQuote(quote, () -> MonthlyQuoteMapper.INSTANCE.yahooQuoteToMonthlyQuote(quote), Period.Monthly, Monthlytrend.class);
			return true;
		} else {
			log.info("Unable to run dailyQuoteJob. Download quote failed. Retry later");
			return false;
		}
	}
}
