package com.herringbone.stock.service;

public interface StockFindServiceFactory {
    StockFindService getLoader(String period);
}
