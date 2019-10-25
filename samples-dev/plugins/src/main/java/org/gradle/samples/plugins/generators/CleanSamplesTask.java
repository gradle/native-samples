package org.gradle.samples.plugins.generators;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * Cleans each of the generated samples listed in the samples manifest.
 */
public class CleanSamplesTask extends DefaultTask {
    private static final Set<String> NAMES = ImmutableSet.of("repo", "build", ".gradle", ".build", "Package.resolved");
    private static final Set<String> EXTENSIONS = ImmutableSet.of("xcworkspace", "xcodeproj", "vs", "sln", "vcxproj", "vcxproj.filters", "vcxproj.user");

    private final RegularFileProperty manifest = getProject().getObjects().fileProperty();

    @TaskAction
    private void clean() throws IOException {
        FileUtils.readLines(manifest.get().getAsFile(), Charset.defaultCharset()).forEach(item -> {
            if (item.startsWith("sample=")) {
                String path = item.substring(7);
                File dir = getProject().file(path);
                if (dir.isDirectory()) {
                    cleanDir(dir);
                }
            } else if (item.startsWith("repo=")) {
                String path = item.substring(5);
                File dir = getProject().file(path);
                if (new File(dir, ".git").isDirectory()) {
                    delete(dir);
                }
            }
        });
    }

    private void cleanDir(File dir) {
        Arrays.stream(Objects.requireNonNull(dir.listFiles())).forEach(file -> {
            if (NAMES.contains(file.getName())) {
                delete(file);
            } else if (EXTENSIONS.contains(StringUtils.substringAfterLast(file.getName(), "."))) {
                delete(file);
            } else if (file.isDirectory()) {
                cleanDir(file);
            }
        });
    }

    private void delete(File file) {
        getLogger().lifecycle("Cleaning " + file.getAbsolutePath());
        try {
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                Files.delete(file.toPath());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't clean file '" + file.getAbsolutePath() + "'", e);
        }
    }

    @InputFile
    public RegularFileProperty getManifest() {
        return manifest;
    }
}
