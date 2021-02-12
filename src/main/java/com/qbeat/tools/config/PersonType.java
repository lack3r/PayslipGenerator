package com.qbeat.tools.config;

public enum PersonType {
    EMPLOYEE("Employee"),
    EMPLOYER("Employer");

    private String value;

    PersonType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PersonType fromValue(String value) {
        for (PersonType personType : PersonType.values()) {
            if (personType.value.equalsIgnoreCase(value)) {
                return personType;
            }
        }

        throw new IllegalArgumentException("Invalid person type: " + value);
    }
}
