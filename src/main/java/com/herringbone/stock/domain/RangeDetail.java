package com.herringbone.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder(toBuilder = true)
@AllArgsConstructor
@Data
public class RangeDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    private Double rangeBottom;
    private Double rangeTop;
    private Integer quantity;
    private Float percentOfTotal;
    private Float incrementalPercent;
    private Double volatility;
    private Double minimumPercent;
    private Double maximumPercent;

    public RangeDetail() {
        this.quantity = 0;
    }

    public String toString() {
        return "Range Bottom: " + this.rangeBottom + ", Range Top: " + this.rangeTop + ", Quantity: " + this.quantity +
                ", PercentOfTotal: " + this.percentOfTotal + ", IncrementalPercent: " + this.incrementalPercent;
    }
}
