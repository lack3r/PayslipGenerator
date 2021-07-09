package io.qbeat.models;

import java.math.BigDecimal;

public class TaxConfigProperty {
    private final int rangeStartPrice;
    private final int rangeEndPrice;
    private final BigDecimal rate;

    public TaxConfigProperty(int rangeStartPrice, int rangeEndPrice, BigDecimal rate) {
        this.rangeStartPrice = rangeStartPrice;
        this.rangeEndPrice = rangeEndPrice;
        this.rate = rate;
    }

    public int getRangeStartPrice() {
        return rangeStartPrice;
    }

    public int getRangeEndPrice() {
        return rangeEndPrice;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "TaxConfigProperty{" +
                "rangeStartPrice=" + rangeStartPrice +
                ", rangeEndPrice=" + rangeEndPrice +
                ", rate=" + rate +
                '}';
    }
}
