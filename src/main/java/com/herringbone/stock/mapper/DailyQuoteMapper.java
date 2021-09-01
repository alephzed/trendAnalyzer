package com.herringbone.stock.mapper;

import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.DailyBasicQuote;
import com.herringbone.stock.model.DailyQuote;
import com.herringbone.stock.model.IBasicQuote;
import com.herringbone.stock.model.QuoteBase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DailyQuoteMapper {
    DailyQuoteMapper INSTANCE = Mappers.getMapper(DailyQuoteMapper.class);

    DailyQuote yahooQuoteToDailyQuote(YahooQuoteBean yahooQuoteBean);
    QuoteBase dailyQuoteToQuoteBase(DailyQuote dailyQuote);
//    @Mapping(source  = "ticker.symbol", target = "symbol")
//    YahooQuoteBean dailyBasicQuoteToYahooQuote(DailyBasicQuote dailyBasicQuote);
    @Mapping(source  = "ticker.symbol", target = "symbol")
    YahooQuoteBean dailyBasicQuoteToYahooQuote(IBasicQuote dailyBasicQuote);

    @Mapping(source = "nextday.id", target = "nextday")
    @Mapping(source = "prevday.id", target = "prevday")
    DailyBasicQuote dailyQuoteToBasicQuote(DailyQuote dailyQuote);
}
