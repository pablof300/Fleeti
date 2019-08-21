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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
        // Add isProjectBuilt functionality
        //        final Command archetypeBuilderCommand = new Command(
        //                "mvn clean package",outputPath + appName + "/" + appName.toLowerCase());
        final String serviceBasePath = outputPath + appName + "/service";
        final String uiBasePath = outputPath + appName + "/ui";
        final Command runAPICommand = new Command(
                "openapi-generator generate -g typescript-fetch -i target/swagger-specs/swagger.json -o " + uiBasePath,
                serviceBasePath
        );
        new FleetiProcess(List.of(runAPICommand)).execute();
        final File apiDirectory = new File(uiBasePath + "/src/api");
        if (apiDirectory.mkdir()) {
            try {
                FileUtils.moveDirectoryToDirectory(new File(uiBasePath + "/src/apis"), apiDirectory,true);
                FileUtils.moveFileToDirectory(new File(uiBasePath + "/src/index.ts"), apiDirectory, false);
                FileUtils.moveFileToDirectory(new File(uiBasePath + "/src/runtime.ts"), apiDirectory, false);

                final File baseComponentFile = new File(uiBasePath + "/src/components/BaseComponent/BaseComponent.tsx");
                final String updatedComponentContent = IOUtils.toString(new FileInputStream(baseComponentFile), StandardCharsets.UTF_8)
                        .replaceAll("#name#", appName);
                IOUtils.write(updatedComponentContent, new FileOutputStream(baseComponentFile), StandardCharsets.UTF_8);

                final File runtimeFile = new File(uiBasePath + "/src/api/runtime.ts");
                final String updatedRuntimeContent = IOUtils.toString(new FileInputStream(runtimeFile), StandardCharsets.UTF_8)
                        .replace("return this.configuration.credentials", "return this.configuration.credentials as RequestCredentials;")
                        .replace("return this.configuration.headers", "return this.configuration.headers as HTTPHeaders;");
                IOUtils.write(updatedRuntimeContent, new FileOutputStream(runtimeFile), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // TODO
            // Exception catching and proper logging
        }

        final Command buildYarn = new Command("yarn", uiBasePath);
        new FleetiProcess(List.of(buildYarn)).execute();
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
