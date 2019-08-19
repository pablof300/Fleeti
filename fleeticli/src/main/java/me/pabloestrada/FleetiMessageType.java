package me.pabloestrada;

public enum FleetiMessageType {
    INFO("INFO"),
    ERROR("ERROR"),
    SUCCESS("SUCCESS");

    private final String name;

    private FleetiMessageType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
