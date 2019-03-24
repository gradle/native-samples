package org.gradle.samples.plugins.generators;

public class SwiftLibraryTemplate extends Template {
    private final String module;

    public SwiftLibraryTemplate(String templateName, String module) {
        super(templateName);
        this.module = module;
    }

    public static SwiftLibraryTemplate of(String templateName, String module) {
        return new SwiftLibraryTemplate(templateName, module);
    }

    public final String getModule() {
        return module;
    }
}
