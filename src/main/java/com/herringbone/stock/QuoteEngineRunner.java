package com.herringbone.stock;

import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.model.Trendtype;
import com.herringbone.stock.repository.TickerRepository;
import com.herringbone.stock.repository.TrendTypeRepository;
import com.herringbone.stock.service.CookieService;
import com.herringbone.stock.service.TickerService;
import com.herringbone.stock.util.TrendtypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class QuoteEngineRunner implements CommandLineRunner {
    @Value("${yahoo.index.symbol}")
    private String yahooIndexAlias;

    @Value("${yahoo.index.full.symbol}")
    private String yahooIndexFullSymbol;

    private final TickerRepository tickerRepository;
    private final TrendTypeRepository trendTypeRepository;
    private final TickerService tickerService;
    private final CookieService cookieService;

    public QuoteEngineRunner(TickerRepository tickerRepository,
                             TrendTypeRepository trendTypeRepository,
                             TickerService tickerService,
                             CookieService cookieService) {
        this.tickerRepository = tickerRepository;
        this.trendTypeRepository = trendTypeRepository;
        this.tickerService = tickerService;
        this.cookieService = cookieService;
    }

    @Override
    public void run(String... args) {
        initializeIfEmpty(yahooIndexFullSymbol, yahooIndexAlias);
        Ticker ticker = tickerService.getTicker(yahooIndexFullSymbol);
        Ticker tickerByAlias = tickerService.getTicker(yahooIndexAlias);
        log.info("Initialized ticker {} and by alias {}", ticker.getId(), tickerByAlias.getId());
    }

    private void initializeIfEmpty(String symbol, String alias) {
        try {
            tickerRepository.findBySymbolOrAlias(symbol, alias).orElseThrow(() -> new Exception("unable to find ticker"));
        } catch (Exception e) {
            log.info("Ticker with symbol {} and alias {} does not exist saving", symbol, alias);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(1950, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
            Ticker ticker = Ticker.builder().symbol(symbol).alias(alias).exchange("index").startdate(zonedDateTime)
                    .lastUpdate(ZonedDateTime.now())
                    .build();
            tickerRepository.save(ticker);
        }
        List<Trendtype> trendTypes = trendTypeRepository.findAll();
        if (trendTypes.size() == 0) {
            AtomicLong index = new AtomicLong();
            EnumSet.allOf(TrendtypeEnum.class).forEach(type -> {
                Trendtype trendtype = Trendtype.builder().description(type.getDescription())
                        .trendvalue(type.getTrendValue()).id(index.incrementAndGet()).build();
                trendTypeRepository.save(trendtype);
            });
        }
    }
}
