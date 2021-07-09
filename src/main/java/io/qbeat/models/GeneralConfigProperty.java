package io.qbeat.models;

import java.math.BigDecimal;

public class GeneralConfigProperty {
    private final PersonType personType;
    private final String name;
    private final BigDecimal contributionsPercentage;
    private final BigDecimal maxContributions;

    public GeneralConfigProperty(String personType, String name, BigDecimal contributionsPercentage, BigDecimal maxContributions) {
        this.personType = PersonType.fromValue(personType);
        this.name = name;
        this.contributionsPercentage = contributionsPercentage;
        this.maxContributions = maxContributions;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getContributionsPercentage() {
        return contributionsPercentage;
    }

    public boolean hasMaxContributions() {
        return maxContributions.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getMaxContributions() {
        return maxContributions;
    }

    @Override
    public String toString() {
        return "GeneralConfigProperty{" +
                "personType=" + personType +
                ", name='" + name + '\'' +
                ", contributionsPercentage=" + contributionsPercentage +
                ", maxContributions=" + maxContributions +
                '}';
    }
}
