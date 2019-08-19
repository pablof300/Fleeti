package me.pabloestrada;

import picocli.CommandLine;

import java.awt.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@CommandLine.Command(
    name = "FleetiCLI",
    description = "Build a Fleeti App"
)
public class FleetiCommand implements Runnable {

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

    public static void main(String[] args) {
        new CommandLine(new FleetiCommand()).execute(args);
    }

    public void run() {
        printMessage(new FleetiMessage(FleetiMessageType.INFO, "Attempting to generate Fleeti app " + appName));
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
            final StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                printMessage(new FleetiMessage(FleetiMessageType.SUCCESS, "Successfully generate Fleeti app " + appName));
                System.exit(0);
            } else {
                printMessage(new FleetiMessage(FleetiMessageType.ERROR, "could not generate Fleeti app " + appName));
            }

        } catch (final IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printMessage(final FleetiMessage message) {
        System.out.println(message);
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }
}
