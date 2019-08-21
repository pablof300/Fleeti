package me.pabloestrada.commands;

import com.sun.tools.javac.util.List;
import me.pabloestrada.FleetiMessage;
import me.pabloestrada.FleetiMessageType;
import me.pabloestrada.FleetiProcess;
import me.pabloestrada.ProcessOutput;
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
        final File outputFile = new File(outputPath + appName);
        final File mavenProjectFile = new File(outputPath + appName + "/" + appName.toLowerCase());
        outputFile.mkdir();

        final Command archetypeGeneratorCommand = new Command(
                "mvn archetype:generate -B " +
                "-DarchetypeGroupId=me.pabloestrada " +
                "-DarchetypeArtifactId=fleeti-archetype " +
                "-DarchetypeVersion=1.0 " +
                "-Dversion=" + "1.0 " +
                "-DgroupId=" + packageName + " " +
                "-Dpackage=" + packageName + " " +
                "-Dname=" + appName + " " +
                "-DartifactId=" + appName.toLowerCase(),
                outputPath + appName);
        final Command archetypeBuilderCommand = new Command(
                "mvn clean package",outputPath + appName + "/" + appName.toLowerCase());

        new FleetiProcess(List.of(archetypeGeneratorCommand, archetypeBuilderCommand)).execute();
        new FleetiBuildAPICommand()
            .setAppName(appName)
            .setOutputPath(outputPath)
            .run();
        System.exit(0);
    }
}
