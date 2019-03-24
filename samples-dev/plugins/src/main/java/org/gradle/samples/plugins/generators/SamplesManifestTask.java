package org.gradle.samples.plugins.generators;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;

/**
 * Generates a manifest file listing the location of each generated sample.
 */
public class SamplesManifestTask extends DefaultTask {
    private final RegularFileProperty manifest = getProject().getObjects().fileProperty();
    private final SetProperty<String> sampleDirs = getProject().getObjects().setProperty(String.class);
    private final SetProperty<String> repoDirs = getProject().getObjects().setProperty(String.class);

    @TaskAction
    private void generate() throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(manifest.get().getAsFile()))) {
            for (String path : sampleDirs.get()) {
                String relPath = getProject().getProjectDir().toPath().relativize(Paths.get(path)).toString();
                writer.println("sample=" + relPath);
            }
            for (String path : repoDirs.get()) {
                String relPath = getProject().getProjectDir().toPath().relativize(Paths.get(path)).toString();
                writer.println("repo=" + relPath);
            }
        }
    }

    @OutputFile
    public RegularFileProperty getManifest() {
        return manifest;
    }

    @Input
    public SetProperty<String> getSampleDirs() {
        return sampleDirs;
    }

    @Input
    public SetProperty<String> getRepoDirs() {
        return repoDirs;
    }
}
