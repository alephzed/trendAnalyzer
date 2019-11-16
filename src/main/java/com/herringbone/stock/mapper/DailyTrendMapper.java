package com.herringbone.stock.mapper;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.model.Dailytrend;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DailyTrendMapper {
    DailyTrendMapper INSTANCE = Mappers.getMapper(DailyTrendMapper.class);

    @Mapping(source = "id", target = "trendId")
    @Mapping(source = "trendend.close", target = "end")
    @Mapping(source = "trendstart.close", target = "middle")
    @Mapping(source = "previoustrend.trendstart.close", target = "beginning")
    @Mapping(source = "trendend.id", target = "end_id")
    @Mapping(source = "previoustrend.trendstart.id", target = "beginning_id")
    @Mapping(source = "trendstart.id", target = "middle_id")
    @Mapping(source = "trendend.date", target = "trendDate")
    @Mapping(source = "daysintrendcount", target = "periodsInTrendCount")
    @Mapping(source = "trendpointchange", target = "trendpointchange")
    @Mapping(source = "trendpercentagechange", target = "trendpercentagechange")
    Trend dailyTrendToTrend(Dailytrend dailytrend);
}
