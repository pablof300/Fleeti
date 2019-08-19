package me.pabloestrada.commands;

import picocli.CommandLine;

import java.io.File;

public class FleetiBuildAPICommand implements Runnable {

    @CommandLine.Option(
            names = { "-t" },
            description = "Type of Fleeti subcommand ()")
    private String type;

    @CommandLine.Option(
            names = { "-n", "--name" },
            description = "Name of the application",
            required = true)
    private String appName;

    @CommandLine.Option(
            names = { "-o", "--output" },
            description = "Output path to create application",
            required = true)
    private String outputPath;

    @Override
    public void run() {
        final ProcessBuilder builder = new ProcessBuilder();
        builder.command("openapi-generator",
                "generate",
                "-g",
                "typescript-fetch",
                "-i",
                "");
        //final File outputFile = new File(outputPath + appName);
    }

    public FleetiBuildAPICommand setAppName(final String appName) {
        this.appName = appName;
        return this;
    }

    public FleetiBuildAPICommand setOutputPath(final String outputPath) {
        this.outputPath = outputPath;
        return this;
    }
}
