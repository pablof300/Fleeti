package me.pabloestrada.commands;

import me.pabloestrada.processes.FleetiProcess;
import me.pabloestrada.utility.PathNormalizer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    private boolean isProjectBuilt = false;

    @Override
    public void run() {
        final String projectBasePath =  new PathNormalizer(outputPath, appName).toString();
        final String serviceBasePath = projectBasePath + "/service";
        final String uiBasePath = projectBasePath + "/ui";

        final Command archetypeBuilderCommand = new Command(
                "mvn clean package", serviceBasePath);
        final Command runService = new Command(
                "java -jar target/" + appName + "-1.0.jar server src/main/resources/config.yml", serviceBasePath);
        final Command getSwaggerSpec = new Command(
                "curl http://localhost:8080/swagger.json --output src/main/resources/swagger.json", serviceBasePath);
        new FleetiProcess(List.of(archetypeBuilderCommand)).execute();
        new FleetiProcess(List.of(getSwaggerSpec)).executeAsync(10);
        new FleetiProcess(List.of(runService)).execute(20);

        final File apiDirectory = new File(uiBasePath + "/src/api");
        try {
            if (apiDirectory.exists()) {
                FileUtils.deleteDirectory(apiDirectory);
            }
            apiDirectory.mkdir();

            final Command runAPICommand = new Command(
                    "openapi-generator generate -g typescript-fetch -i src/main/resources/swagger.json -o ../ui/src/api/ --skip-validate-spec",
                    serviceBasePath
            );
            new FleetiProcess(List.of(runAPICommand)).execute();

            final File baseComponentFile = new File(uiBasePath + "/src/components/BaseComponent/BaseComponent.tsx");
            final String updatedComponentContent = IOUtils.toString(new FileInputStream(baseComponentFile), StandardCharsets.UTF_8)
                    .replaceAll("#name#", appName);
            IOUtils.write(updatedComponentContent, new FileOutputStream(baseComponentFile), StandardCharsets.UTF_8);

            final File runtimeFile = new File(uiBasePath + "/src/api/runtime.ts");
            final String updatedRuntimeContent = IOUtils.toString(new FileInputStream(runtimeFile), StandardCharsets.UTF_8)
                    .replace("export const BASE_PATH", "declare type GlobalFetch = WindowOrWorkerGlobalScope\nexport const BASE_PATH")
                    .replace("http://localhost", "http://localhost:8080");
            IOUtils.write(updatedRuntimeContent, new FileOutputStream(runtimeFile), StandardCharsets.UTF_8);
            System.exit(0);
        } catch (final IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    FleetiBuildAPICommand setAppName(final String appName) {
        this.appName = appName;
        return this;
    }

    FleetiBuildAPICommand setOutputPath(final String outputPath) {
        this.outputPath = outputPath;
        return this;
    }

    FleetiBuildAPICommand setProjectBuilt(final boolean isProjectBuilt) {
        this.isProjectBuilt = isProjectBuilt;
        return this;
    }
}
