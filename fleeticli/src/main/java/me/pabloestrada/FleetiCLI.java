package me.pabloestrada;

import me.pabloestrada.commands.FleetiBuildAPICommand;
import me.pabloestrada.commands.FleetiCreateAppCommand;
import me.pabloestrada.commands.FleetiRunServiceCommand;
import picocli.CommandLine;

import java.io.File;

public class FleetiCLI {
    public static void main(final String[] args) {
        if (args.length < 2 && !args[0].equals("-t")) {
            FleetiMessage.printMessage(
                    new FleetiMessage(FleetiMessageType.ERROR, "No subcommand (-t) was provided (create, build-api)"));
            return;
        }
        final String subCommand = args[1].toLowerCase();
        if (subCommand.equals("create")) {
            new CommandLine(new FleetiCreateAppCommand()).execute(args);
        } else if (subCommand.equals("build-api")) {
            new CommandLine(new FleetiBuildAPICommand()).execute(args);
        } else if (subCommand.equals("run-service") || subCommand.equals("rs")) {
            new CommandLine(new FleetiRunServiceCommand()).execute(args);
        } else {
            FleetiMessage.printMessage(
                    new FleetiMessage(FleetiMessageType.ERROR, "No valid subcommand was provided (create, build-api, run-service)"));
        }
    }
}
