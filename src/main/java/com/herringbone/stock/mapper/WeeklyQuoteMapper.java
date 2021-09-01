package com.herringbone.stock.mapper;

import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.WeeklyBasicQuote;
import com.herringbone.stock.model.WeeklyQuote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")

public interface WeeklyQuoteMapper {
    WeeklyQuoteMapper INSTANCE = Mappers.getMapper(WeeklyQuoteMapper.class);

    WeeklyQuote yahooQuoteToWeeklyQuote(YahooQuoteBean yahooQuoteBean);
    QuoteBase weeklyQuoteBaseToQuoteBase(WeeklyQuote weeklyQuote);

    @Mapping(source = "nextweek.id", target = "nextweek")
    @Mapping(source = "prevweek.id", target = "prevweek")
    WeeklyBasicQuote weeklyQuoteToBasicQuote(WeeklyQuote weeklyQuote);
}
