package com.herringbone.stock.service;

import com.herringbone.stock.exception.QuoteLoadingServiceException;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.repository.TickerRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TickerService {

    private final TickerRepository tickerRepository;

    public TickerService(TickerRepository tickerRepository) {
        this.tickerRepository = tickerRepository;
    }

    @Cacheable("ticker")
    public Ticker getTicker(String symbol) {
        return tickerRepository.findBySymbolOrAlias(symbol, symbol).orElseThrow(QuoteLoadingServiceException::new);
    }
}
