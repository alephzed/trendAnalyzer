package com.herringbone.stock.mapper;

import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.MonthlyQuote;
import com.herringbone.stock.model.QuoteBase;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MonthlyQuoteMapper {
    MonthlyQuoteMapper INSTANCE = Mappers.getMapper(MonthlyQuoteMapper.class);

    MonthlyQuote yahooQuoteToMonthlyQuote(YahooQuoteBean yahooQuoteBean);
    QuoteBase monthlyQuoteToQuoteBase(MonthlyQuote monthlyQuote);
}
