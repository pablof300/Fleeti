package me.pabloestrada.commands;

import com.sun.tools.javac.util.List;
import me.pabloestrada.FleetiProcess;
import me.pabloestrada.ProcessOutput;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;

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
//        final File mavenProjectFile = new File(outputPath + appName + "/" + appName.toLowerCase());
//        final ProcessBuilder runAPI = new ProcessBuilder();
//        runAPI.command("java",
//                "-jar",
//                "/target/" + appName + "-1.0",
//                "server",
//                "/src/main/resources/config.yml");
//        runAPI.directory(mavenProjectFile);
//        System.out.println("Running the directory of " + mavenProjectFile.getAbsolutePath() + "/target/" + appName + "-1.0");
        final Command runAPICommand = new Command(
                "java -jar target/" + appName + "-1.0.jar server src/main/resources/config.yml",
                outputPath + appName + "/" + appName.toLowerCase()
        );
        new FleetiProcess(runAPICommand).execute();
    }

    FleetiBuildAPICommand setAppName(final String appName) {
        this.appName = appName;
        return this;
    }

    FleetiBuildAPICommand setOutputPath(final String outputPath) {
        this.outputPath = outputPath;
        return this;
    }
}
