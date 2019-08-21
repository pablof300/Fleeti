package me.pabloestrada.commands;

import java.util.Arrays;
import java.util.List;

public class Command {
    private String rawCommand;
    private String path;

    // TODO
    // Change to AutoValue class
    public Command(final String rawCommand, final String path) {
        this.rawCommand = rawCommand;
        this.path = path;
    }

    public String getRawCommand() {
        return rawCommand;
    }

    public String getPath() {
        return path;
    }
}