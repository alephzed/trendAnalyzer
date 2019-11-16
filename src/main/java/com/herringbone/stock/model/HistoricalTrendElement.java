package com.herringbone.stock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricalTrendElement {
    @Id
	private Long trendId;
	private Long quoteId;
	private ZonedDateTime quoteDate;
	private Double price1;
	private Double price2;
	private Double price3;
	private Double nextTrendClose;
	private Double firstImpulseMove;
	private Double secondImpulseMove;
	private Double nextImpulseMove;
	private Double reversalPrice;
	private Double finalTrendClose;
	private Double percentChange;
	private Double trendVolatility;
	private Long nextTrendId;
	private Long nextTrendQuoteId;

	public HistoricalTrendElement(Long trendId, Double price1, Double price2,
								  Double price3, Double price4, Double price5, Long nextTrendId, Long nextTrendQuoteId,
								  Double percentChange, Double firstImpulseMove, Double secondImpulseMove,
								  Double nextImpulseMove, Long quoteId, Double trendVolatility, ZonedDateTime quoteDate) {
		this.trendId = trendId;
//		this.quoteDate = quoteDate;
		this.price1 = price1;
		this.price2 = price2;
		this.price3 = price3;
		this.nextTrendClose = price4;
		this.finalTrendClose = price5;
		this.nextTrendId = nextTrendId;
		this.nextTrendQuoteId = nextTrendQuoteId;
		this.percentChange = percentChange;
		this.firstImpulseMove = firstImpulseMove;
		this.secondImpulseMove = secondImpulseMove;
		this.nextImpulseMove = nextImpulseMove;
		this.quoteId = quoteId;
		this.trendVolatility = trendVolatility;
		this.quoteDate = quoteDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		HistoricalTrendElement that = (HistoricalTrendElement) o;
		if ( reversalPrice == null || that.reversalPrice == null) {
			System.out.println("null!");
		}
		return Objects.equals(trendId.doubleValue(), that.trendId.doubleValue()) &&
				Objects.equals(quoteId.doubleValue(), that.quoteId.doubleValue()) &&
//				Objects.equals(quoteDate, that.quoteDate) &&
				Objects.equals(price1.doubleValue(), that.price1.doubleValue()) &&
				Objects.equals(price2.doubleValue(), that.price2.doubleValue()) &&
				Objects.equals(price3.doubleValue(), that.price3.doubleValue()) &&
				Objects.equals(nextTrendClose.doubleValue(), that.nextTrendClose.doubleValue()) &&
				Objects.equals(firstImpulseMove.doubleValue(), that.firstImpulseMove.doubleValue()) &&
				Objects.equals(secondImpulseMove.doubleValue(), that.secondImpulseMove.doubleValue()) &&
				Objects.equals(nextImpulseMove.doubleValue(), that.nextImpulseMove.doubleValue()) &&
				Objects.equals(reversalPrice.doubleValue(), that.reversalPrice.doubleValue()) &&
				Objects.equals(finalTrendClose.doubleValue(), that.finalTrendClose.doubleValue()) &&
				Objects.equals(percentChange.doubleValue(), that.percentChange.doubleValue()) &&
				Objects.equals(trendVolatility.doubleValue(), that.trendVolatility.doubleValue()) &&
				Objects.equals(nextTrendId.intValue(), that.nextTrendId.intValue()) &&
				Objects.equals(nextTrendQuoteId.intValue(), that.nextTrendQuoteId.intValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(trendId, quoteId, quoteDate, price1, price2, price3, nextTrendClose, firstImpulseMove,
				secondImpulseMove, nextImpulseMove, reversalPrice, finalTrendClose, percentChange, trendVolatility,
				nextTrendId, nextTrendQuoteId);
	}
}
