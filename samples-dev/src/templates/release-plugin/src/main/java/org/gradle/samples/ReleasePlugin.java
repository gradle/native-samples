package org.gradle.samples;

import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A sample plugin that produces releases of a Gradle project. Adds a 'release' task that generates the Swift Package
 * Manager manifest for the project and then commits and tags the result. It uses the 'swiftpm-export' plugin to generate
 * the Swift Package Manager manifest.
 *
 * The 'release' task also increments the version in the build.gradle, ready for the next release.
 *
 * Note: because this is just a sample, it only deals with single project builds.
 */
public class ReleasePlugin implements Plugin<Project> {
    public void apply(final Project project) {
        project.getPluginManager().apply("swiftpm-export");
        project.getTasks().register("release", task -> {
            // Generate the Swift PM manifest prior to commit
            task.dependsOn(project.getTasks().named("generateSwiftPmManifest"));
            task.doLast(it -> {
                // Commit and tag changes
                project.exec(execSpec -> {
                    execSpec.commandLine("git", "add", "Package.swift");
                });
                project.exec(execSpec -> {
                    execSpec.commandLine("git", "commit", "-a", "-m", "version " + project.getVersion());
                });
                project.exec(execSpec -> {
                    execSpec.commandLine("git", "tag", project.getVersion());
                });

                // Increment the version in the build script, for next release
                Pattern versionPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d)");
                Matcher matcher = versionPattern.matcher(project.getVersion().toString());
                if (!matcher.matches()) {
                    throw new GradleException("Could not parse project version \'" + project.getVersion() + "\'");
                }

                String newVersion = matcher.group(1) + "." + ((StringGroovyMethods.asType(matcher.group(2), Integer.class)) + 1) + ".0";
                String buildFileText = readFileAsString(project.getBuildFile());
                String updatedText = buildFileText.replaceAll("version\\s*=\\s*\'" + String.valueOf(project.getVersion()) + "\'", "version = \'" + newVersion + "\'");
                if (updatedText.equals(buildFileText)) {
                    throw new GradleException("Could not update version in " + project.getBuildFile().getName());
                }

                writeFile(project.getBuildFile(), updatedText);
            });
        });
    }

    private static String readFileAsString(File file) {
        try (Scanner in = new Scanner(file).useDelimiter("\\Z")) {
            return in.next();
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeFile(File file, String content) {
        try (OutputStream out = new FileOutputStream(file)) {
            out.write(content.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
