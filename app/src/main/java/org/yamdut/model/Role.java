package org.yamdut.model;

public enum Role {
    DRIVER("Driver"),
    PASSENGER("Passenger"),
    ADMIN("admin");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
