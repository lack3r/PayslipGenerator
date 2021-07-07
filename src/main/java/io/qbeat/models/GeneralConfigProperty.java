package io.qbeat.models;

public class GeneralConfigProperty {
    private final PersonType personType;
    private final String name;
    private final double contributionsPercentage;
    private final double maxContributions;

    public GeneralConfigProperty(String personType, String name, double contributionsPercentage, double maxContributions) {
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

    public double getContributionsPercentage() {
        return contributionsPercentage;
    }

    public boolean hasMaxContributions() {
        return maxContributions > 0;
    }

    public double getMaxContributions() {
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
