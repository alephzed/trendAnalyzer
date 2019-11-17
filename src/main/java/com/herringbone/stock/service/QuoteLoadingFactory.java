package com.herringbone.stock.service;

import com.herringbone.stock.util.Period;

public interface QuoteLoadingFactory {
    QuoteLoader getLoader(Period period);
}
