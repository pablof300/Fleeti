package me.pabloestrada.processes;

import me.pabloestrada.FleetiMessage;
import me.pabloestrada.FleetiMessageType;
import me.pabloestrada.utility.PathNormalizer;

import java.io.File;

public class FleetiPath {

    private final String baseFolderPath;
    private final String appName;

    public FleetiPath(final String outputPath, final String appName) {
        this.appName = appName;
        baseFolderPath = new PathNormalizer(outputPath, appName).toString();
        if (new File(baseFolderPath).mkdir()) {
            FleetiMessage.printMessage(new FleetiMessage(FleetiMessageType.INFO, "Created folder " + baseFolderPath));
        }
    }

    public String getBaseFolderPath() {
        return baseFolderPath;
    }

    public String getMavenArchetypePath() {
        return baseFolderPath + appName.toLowerCase();
    }

    public String getServiceCodebasePath() {
        return baseFolderPath + "service";
    }

    public String getUICodebasePath() {
        return baseFolderPath + "ui";
    }
}
