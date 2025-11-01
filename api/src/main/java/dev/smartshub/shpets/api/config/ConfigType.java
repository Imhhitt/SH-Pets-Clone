package dev.smartshub.shpets.api.config;

public enum ConfigType {
    MESSAGES("lang/messages.yml", "messages.yml", "lang"),
    PETS_DEFINITION("pets/", null, "pets");

    private final String defaultPath;
    private final String resourceName;
    private final String parentFolder;

    ConfigType(String defaultPath, String resourceName, String parentFolder) {
        this.defaultPath = defaultPath;
        this.resourceName = resourceName;
        this.parentFolder = parentFolder;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getParentFolder() {
        return parentFolder;
    }

    public boolean isFolder() {
        return defaultPath.endsWith("/");
    }
}
