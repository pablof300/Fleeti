package me.pabloestrada.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public final class PathNormalizer {

    private final String path;
    private final String slash;

    public PathNormalizer(final String rawPath, final String appName) {
        this.slash = PlatformUtils.getSlash();
        this.path = normalizePath(rawPath, appName);
    }

    private String normalizePath(final String rawPath, final String appName) {
        String initialPath = rawPath.endsWith(slash) ? rawPath.substring(0, rawPath.length() - 1) : rawPath;
        if (!isAbsolutePath(initialPath)) {
            try {
                initialPath = (new File(initialPath)).getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return initialPath + slash + appName + slash;
    }

    private boolean isAbsolutePath(final String rawPath) {
        return Paths.get(rawPath).isAbsolute();
    }

    public String toString() {
        return path;
    }
}
