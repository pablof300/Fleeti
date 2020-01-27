package me.pabloestrada.utility;

import me.pabloestrada.processes.FleetiPath;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public final class FleetiFilesUtils {

    public static void replaceUIWithTemplateCode(final FleetiPath fleetiPath) {
        try {
            FileUtils.deleteDirectory(new File(fleetiPath.getUICodebasePath() + "/src"));
            System.out.println("Address 1 " + fleetiPath.getServiceCodebasePath() + PlatformUtils.getSystemSpecificPath( "/ui"));
            System.out.println("Address 2 " + fleetiPath.getUICodebasePath() + PlatformUtils.getSystemSpecificPath("/src"));
            FileUtils.moveDirectory(
                    new File(fleetiPath.getMavenArchetypePath() + PlatformUtils.getSystemSpecificPath( "/ui")),
                    new File(fleetiPath.getUICodebasePath() + PlatformUtils.getSystemSpecificPath("/src")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void renameMavenProjectToServiceCodebase(final FleetiPath fleetiPath) {
        try {
            FileUtils.moveDirectory(new File(fleetiPath.getMavenArchetypePath()), new File(fleetiPath.getServiceCodebasePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
