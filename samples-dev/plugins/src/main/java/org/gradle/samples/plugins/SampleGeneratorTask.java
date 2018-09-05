package org.gradle.samples.plugins;

import org.gradle.api.Task;
import org.gradle.api.file.DirectoryProperty;

/**
 * This is here to help migrate the plugins from Groovy to Kotlin.
 */
public interface SampleGeneratorTask extends Task {
    DirectoryProperty getTemplatesDir();

    DirectoryProperty getSampleDir();
}
