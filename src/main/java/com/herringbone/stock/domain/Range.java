package com.herringbone.stock.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Range implements Comparable<Range>, Serializable {
    private static final long serialVersionUID = 1L;
    private Double minimumPercent;
    private Double maximumPercent;

    public Range() {

    }

    public Range(double min, double max) {
        this.minimumPercent = min;
        this.maximumPercent = max;
    }

    public boolean contains( Double value) {
        if (minimumPercent <= value && maximumPercent > value) {
            return true;
        }
        return false;
    }

    public int compare(Object arg0, Object arg1) {
        // TODO Auto-generated method stub
        Range range1 = (Range)arg0;
        Range range2 = (Range)arg1;
        Double min1 = range1.getMinimumPercent();
        Double min2 = range2.getMinimumPercent();
        if (min1 > min2) {
            return 1;
        }
        else if (min1 < min2) {
            return -1;
        }
        return 0;
    }

    public int compareTo(Range range) {
        Range range2 = range;
        Double min2 = range2.getMinimumPercent();
        if (this.minimumPercent > min2) {
            return 1;
        }
        else if (this.minimumPercent < min2) {
            return -1;
        }
        return 0;
    }

    public String toString() {
        return minimumPercent + " to " + maximumPercent;
    }
}
