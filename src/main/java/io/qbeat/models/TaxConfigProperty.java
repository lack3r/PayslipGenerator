package io.qbeat.models;

public class TaxConfigProperty {
    private final int rangeStartPrice;
    private final int rangeEndPrice;
    private final double rate;

    public TaxConfigProperty(int rangeStartPrice, int rangeEndPrice, double rate) {
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

    public double getRate() {
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
