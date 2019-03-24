package org.gradle.samples.plugins.generators;

public class CppLibraryTemplate extends Template {
    private final String name;

    public CppLibraryTemplate(String templateName, String name) {
        super(templateName);
        this.name = name;
    }

    public static CppLibraryTemplate of(String templateName, String name) {
        return new CppLibraryTemplate(templateName, name);
    }

    public final String getName() {
        return name;
    }
}
