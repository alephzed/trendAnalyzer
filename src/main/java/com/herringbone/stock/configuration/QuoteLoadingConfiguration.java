package com.herringbone.stock.configuration;

import com.herringbone.stock.service.QuoteLoadingFactory;
import com.herringbone.stock.service.QuoteProcessingFactory;
import com.herringbone.stock.service.StockFindServiceFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QuoteLoadingConfiguration {

    @Bean("quoteLoadingFactory")
    public FactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(QuoteLoadingFactory.class);
        return factoryBean;
    }

    @Bean("stockFindFactory")
    public FactoryBean stockFindServiceLocatory() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(StockFindServiceFactory.class);
        return factoryBean;
    }

    @Bean("quoteProcessingFactory")
    public FactoryBean quoteProcessingFactory() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(QuoteProcessingFactory.class);
        return factoryBean;
    }

}
