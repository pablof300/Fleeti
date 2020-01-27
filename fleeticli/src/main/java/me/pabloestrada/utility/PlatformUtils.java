package me.pabloestrada.utility;

public final class PlatformUtils
{
    static OSType getOperatingSystem() {
        final String operatingSystemProperty = System.getProperty("os.name");
        return operatingSystemProperty != null && operatingSystemProperty.startsWith("Windows")
                ? OSType.WINDOWS : OSType.UNIX;
    }

    public static String getSystemSpecificCommand(final String rawCommand) {
        if (getOperatingSystem() == OSType.UNIX) {
            return rawCommand;
        }
        return "cmd /c " + rawCommand;
    }

    public static String getSystemSpecificPath(final String unixPath) {
        if (getOperatingSystem() == OSType.UNIX) {
            return unixPath;
        }
        return unixPath.replaceAll(OSType.UNIX.getSlash(), OSType.WINDOWS.getSlash());
    }

    public static String getSlash() {
        if (getOperatingSystem() == OSType.UNIX) {
            return OSType.UNIX.getSlash();
        }
        return OSType.WINDOWS.getSlash();
    }
}
