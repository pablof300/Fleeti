package me.pabloestrada;

public class FleetiMessage {
    private final String message;
    private final FleetiMessageType type;

    public FleetiMessage(final FleetiMessageType type, final String message) {
        this.message = message;
        this.type = type;
    }

    @Override
    public String toString() {
        return "[Fleeti] " + type.getName() + ": " + message;
    }

    public static void printMessage(final FleetiMessage message) {
        System.out.println(message);
    }
}
