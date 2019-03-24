package org.gradle.samples.plugins.generators;

public class Template {
    private final String templateName;

    public Template(String templateName) {
        this.templateName = templateName;
    }

    public static Template of(String templateName) {
        return new Template(templateName);
    }

    public final String getTemplateName() {
        return templateName;
    }
}
