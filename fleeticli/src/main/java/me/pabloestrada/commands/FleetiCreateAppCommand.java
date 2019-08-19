package me.pabloestrada.commands;

import me.pabloestrada.FleetiMessage;
import me.pabloestrada.FleetiMessageType;
import picocli.CommandLine;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@CommandLine.Command(
    name = "FleetiCLI",
    description = "Build a Fleeti App"
)
public class FleetiCreateAppCommand implements Runnable {

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
            defaultValue = "~/Desktop/")
    private String outputPath;

    @CommandLine.Option(
            names = { "-p", "--package" },
            description = "Package path of the application",
            defaultValue = "me.pabloestrada")
    private String packageName;

    public void run() {
        FleetiMessage.printMessage(new FleetiMessage(FleetiMessageType.INFO, "Attempting to generate Fleeti app " + appName));
        try {
            final ProcessBuilder builder = new ProcessBuilder();
            final File outputFile = new File(outputPath + appName);
            outputFile.mkdir();
            builder.command("mvn",
                    "archetype:generate",
                    "-B",
                    "-DarchetypeGroupId=me.pabloestrada",
                    "-DarchetypeArtifactId=fleeti-archetype",
                    "-DarchetypeVersion=1.0",
                    "-Dversion=" + "1.0",
                    "-DgroupId=" + packageName,
                    "-Dpackage=" + packageName,
                    "-Dname=" + appName,
                    "-DartifactId=" + appName.toLowerCase());
            builder.directory(outputFile);

            final Process process = builder.start();
            new FleetiBuildAPICommand()
                    .setAppName(appName)
                    .setOutputPath(outputPath)
                    .run();

        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
