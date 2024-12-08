package io.github.oscar0812.apkstudio.java2smali.models;

public enum BuildTaskType {
    GRADLE("GRADLE"),
    MAVEN("Maven"),
    IDE("IDE"),
    OTHER("OTHER");

    private final String description; // The value associated with each enum constant

    // Constructor to initialize the enum constants with their associated value
    BuildTaskType(String description) {
        this.description = description;
    }

    // Getter method to retrieve the value (description)
    public String getDescription() {
        return description;
    }
}
