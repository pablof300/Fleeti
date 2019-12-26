package me.pabloestrada.commands;

import me.pabloestrada.FleetiMessage;
import me.pabloestrada.FleetiMessageType;
import me.pabloestrada.processes.FleetiProcess;
import me.pabloestrada.utility.PathNormalizer;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine;

import java.io.*;
import java.util.List;

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
            defaultValue = ".")
    private String outputPath;

    @CommandLine.Option(
            names = { "-p", "--package" },
            description = "Package path of the application",
            defaultValue = "me.pabloestrada")
    private String packageName;

    public void run() {
        FleetiMessage.printMessage(new FleetiMessage(FleetiMessageType.INFO, "Attempting to generate Fleeti app " + appName));
        final String baseFolderPath = new PathNormalizer(outputPath, appName).toString();
        final String serviceBasePath = baseFolderPath + "/" + appName.toLowerCase();
        final File outputFile = new File(baseFolderPath);

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
                baseFolderPath);
        final Command archetypeBuilderCommand = new Command(
                "mvn clean package", serviceBasePath);
        final Command createReactAppCommand = new Command(
                "npx create-react-app ui --typescript", baseFolderPath);
        final Command addReactRouterToUiCommand = new Command(
                "yarn add react-router-dom", baseFolderPath + "/ui");
        final Command addReactRouterTypesToUiCommand = new Command(
                "npm install @types/react-router-dom", baseFolderPath + "/ui");
        final Command performNpmChangesCommand = new Command(
                "npm i", baseFolderPath + "/ui");
        new FleetiProcess(
                List.of(archetypeGeneratorCommand,
                        archetypeBuilderCommand,
                        createReactAppCommand,
                        addReactRouterToUiCommand,
                        addReactRouterTypesToUiCommand,
                        performNpmChangesCommand)
        ).execute();

        try {
            FileUtils.deleteDirectory(new File(baseFolderPath + "/ui/src"));
            FileUtils.moveDirectory(new File(serviceBasePath + "/ui"), new File(baseFolderPath + "/ui/src"));
            FileUtils.moveDirectory(new File(serviceBasePath), new File(baseFolderPath + "/service"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        new FleetiBuildAPICommand()
            .setAppName(appName)
            .setOutputPath(outputPath)
            .setProjectBuilt(true)
            .run();
        System.exit(0);
    }
}
