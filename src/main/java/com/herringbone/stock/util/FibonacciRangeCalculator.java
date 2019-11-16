package com.herringbone.stock.util;

import com.herringbone.stock.domain.Range;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FibonacciRangeCalculator {

    private ArrayList<BigInteger> fibCache;

    public  FibonacciRangeCalculator() {
        fibCache = new ArrayList<>();
        fibCache.add(BigInteger.ZERO);
        fibCache.add(BigInteger.ONE);
        fib(30);
    }

    private static final int NUM_DIGITS = 500;

    private  BigInteger fib(int n) {
        if (n >= fibCache.size()) {
            fibCache.add(n, fib(n-1).add(fib(n-2)));
        }
        return fibCache.get(n);
    }

    private Float getNextFibNumber(int index) {
        MathContext hiPrecision = new MathContext(NUM_DIGITS);
            BigDecimal bigD = new BigDecimal(fibCache.get(index), hiPrecision);
            BigDecimal outValue = bigD.multiply( BigDecimal.valueOf((double) 1) );
            BigDecimal displayValue = outValue.setScale(8, RoundingMode.HALF_EVEN );
            return displayValue.floatValue( );
    }

    public List<Range> getFibonacciRangeScaled(double maxImpulseStats, double minImpulseStats, long historicalTrendElementsSize, Integer intervalCount, Double minimumInterval) {
        if (historicalTrendElementsSize == 0) {
            throw new RuntimeException("statistics is not initialized");
        }
        List<Range> fibRanges = new ArrayList<>();
        List<Float> fibRangeList = new ArrayList<>();
        int index = 0;
        float fibNumber;
        float calculatedNumber = 0;
        float max = (float)maxImpulseStats;
        float min = (float)minImpulseStats;
        float pct = (float)minimumInterval.doubleValue();
        float minScaledPct = (max - min) * pct;
        while (fibRangeList.size() <= intervalCount) {
            float prevfibNumber = calculatedNumber;
            fibNumber = getNextFibNumber(index);
            index++;
            calculatedNumber = prevfibNumber + minScaledPct * fibNumber;
            fibRangeList.add(calculatedNumber);
        }

        for (int i=0; i< fibRangeList.size()-1; i++) {
            fibRanges.add( new Range(fibRangeList.get(i), fibRangeList.get(i+1)));
        }

        return fibRanges;
    }

}
