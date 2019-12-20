package com.herringbone.stock.repository;

    import com.herringbone.stock.model.Ticker;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.repository.query.Param;

    import java.util.Optional;

    public interface TickerRepository extends JpaRepository<Ticker, Long> {

        Optional<Ticker> findBySymbolOrAlias(@Param("symbol") String symbol,
                                             @Param("alias") String alias);
    }
