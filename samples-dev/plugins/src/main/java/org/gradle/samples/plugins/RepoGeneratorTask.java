package org.gradle.samples.plugins;

import org.gradle.api.Task;
import org.gradle.api.file.DirectoryProperty;

/**
 * This is here to help migrate from Groovy to Kotlin.
 */
public interface RepoGeneratorTask extends Task {
    DirectoryProperty getSampleDir();
}
