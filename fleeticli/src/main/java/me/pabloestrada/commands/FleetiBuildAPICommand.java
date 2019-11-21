package me.pabloestrada.commands;

import com.sun.tools.javac.util.List;
import me.pabloestrada.FleetiProcess;
import me.pabloestrada.ProcessOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
        // TODO
        // Add isProjectBuilt functionality for mvn clean package
        //        final Command archetypeBuilderCommand = new Command(
        //                "mvn clean package",outputPath + appName + "/" + appName.toLowerCase());
        final String projectBasePath = outputPath + appName;
        final String serviceBasePath = projectBasePath + "/service";
        final String uiBasePath = projectBasePath + "/ui";
        final Command runAPICommand = new Command(
                "openapi-generator generate -g typescript-fetch -i target/swagger-specs/swagger.json -o target/swagger-specs/",
                serviceBasePath
        );
        new FleetiProcess(List.of(runAPICommand)).execute();
        final File apiDirectory = new File(uiBasePath + "/src/api");
        apiDirectory.mkdir();
        try {
            final String apiFilesPath = serviceBasePath + "/target/swagger-specs/src";
            FileUtils.moveDirectoryToDirectory(new File(apiFilesPath + "/apis"), apiDirectory,true);
            FileUtils.moveFileToDirectory(new File(apiFilesPath + "/index.ts"), apiDirectory, false);
            FileUtils.moveFileToDirectory(new File(apiFilesPath + "/runtime.ts"), apiDirectory, false);
            FileUtils.deleteDirectory(new File(serviceBasePath + "/target/swagger-specs/src"));

            final File baseComponentFile = new File(uiBasePath + "/src/components/BaseComponent/BaseComponent.tsx");
            final String updatedComponentContent = IOUtils.toString(new FileInputStream(baseComponentFile), StandardCharsets.UTF_8)
                    .replaceAll("#name#", appName);
            IOUtils.write(updatedComponentContent, new FileOutputStream(baseComponentFile), StandardCharsets.UTF_8);

            final File runtimeFile = new File(uiBasePath + "/src/api/runtime.ts");
            final String updatedRuntimeContent = IOUtils.toString(new FileInputStream(runtimeFile), StandardCharsets.UTF_8)
                    .replace("return this.configuration.credentials", "return this.configuration.credentials as RequestCredentials;")
                    .replace("return this.configuration.headers", "return this.configuration.headers as HTTPHeaders;")
                    .replace("export const BASE_PATH", "declare type GlobalFetch = WindowOrWorkerGlobalScope\nexport const BASE_PATH");
            IOUtils.write(updatedRuntimeContent, new FileOutputStream(runtimeFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
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
