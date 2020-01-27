package me.pabloestrada.commands;

import me.pabloestrada.processes.FleetiPath;
import me.pabloestrada.processes.FleetiProcess;
import me.pabloestrada.utility.PathNormalizer;
import me.pabloestrada.utility.PlatformUtils;
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
        final FleetiPath fleetiPath = new FleetiPath(outputPath, appName);

        final String swaggerSpecPath = PlatformUtils.getSystemSpecificPath("src/main/resources/swagger.json");
        final String jarFilePath = PlatformUtils.getSystemSpecificPath("target/" + appName + "-1.0.jar");
        final String configPath = PlatformUtils.getSystemSpecificPath("src/main/resources/config.yml");
        final Command archetypeBuilderCommand = new Command(
                "mvn clean package", fleetiPath.getServiceCodebasePath());
        final Command runService = new Command(
                "java -jar " + jarFilePath + " server " + configPath, fleetiPath.getServiceCodebasePath());
        final Command getSwaggerSpec = new Command(
                "curl http://localhost:8080/swagger.json --output " + swaggerSpecPath, fleetiPath.getServiceCodebasePath());
        new FleetiProcess(List.of(archetypeBuilderCommand)).execute();
        new FleetiProcess(List.of(getSwaggerSpec)).executeAsync(10);
        new FleetiProcess(List.of(runService)).execute(20);


        // Refactor this code
        final File apiDirectory = new File(fleetiPath.getUICodebasePath() + PlatformUtils.getSystemSpecificPath("/src/api"));
        try {
            if (apiDirectory.exists()) {
                FileUtils.deleteDirectory(apiDirectory);
            }
            apiDirectory.mkdir();

            final String uiApiPath = PlatformUtils.getSystemSpecificPath("../ui/src/api/");
            final Command runAPICommand = new Command(
                    "openapi-generator generate -g typescript-fetch -i " + swaggerSpecPath + " -o " + uiApiPath + " --skip-validate-spec",
                    fleetiPath.getServiceCodebasePath()
            );
            new FleetiProcess(List.of(runAPICommand)).execute();

            final String baseComponentPath = fleetiPath.getUICodebasePath() + PlatformUtils.getSystemSpecificPath("/src/components/BaseComponent/BaseComponent.tsx");
            final File baseComponentFile = new File(baseComponentPath);
            final String updatedComponentContent = IOUtils.toString(new FileInputStream(baseComponentFile), StandardCharsets.UTF_8)
                    .replaceAll("#name#", appName);
            IOUtils.write(updatedComponentContent, new FileOutputStream(baseComponentFile), StandardCharsets.UTF_8);

            final String runtimePath = fleetiPath.getUICodebasePath() + PlatformUtils.getSystemSpecificPath("/src/api/runtime.ts");
            final String updatedRuntimeContent = IOUtils.toString(new FileInputStream(new File(runtimePath)), StandardCharsets.UTF_8)
                    .replace("export const BASE_PATH", "declare type GlobalFetch = WindowOrWorkerGlobalScope\nexport const BASE_PATH")
                    .replace("http://localhost", "http://localhost:8080");
            IOUtils.write(updatedRuntimeContent, new FileOutputStream(new File(runtimePath)), StandardCharsets.UTF_8);
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
