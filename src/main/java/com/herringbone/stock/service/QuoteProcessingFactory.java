package com.herringbone.stock.service;

import com.herringbone.stock.util.Period;

public interface QuoteProcessingFactory {
    QuoteProcessor getProcessor(Period period);
}
