package me.pabloestrada.utility;

public enum OSType {
    UNIX("/"),
    WINDOWS("\\");

    private String slash;

    OSType(final String slash) {
        this.slash = slash;
    }

    public String getSlash() {
        return slash;
    }

    public void setSlash(String slash) {
        this.slash = slash;
    }
}
