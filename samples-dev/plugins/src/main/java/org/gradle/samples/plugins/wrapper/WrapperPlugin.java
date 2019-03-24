package org.gradle.samples.plugins.wrapper;

import groovy.json.JsonSlurper;
import org.apache.commons.io.IOUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.wrapper.Wrapper;
import org.gradle.internal.Cast;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Adds a 'nightlyWrapper' task.
 */
public class WrapperPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getTasks().register("nightlyWrapper", Wrapper.class, task -> {
            task.getLogger().lifecycle("Locating nightly version ...");
            try {
                Map<String, String> result = Cast.uncheckedCast(new JsonSlurper().parseText(IOUtils.toString(new URL("https://services.gradle.org/versions/nightly"), Charset.defaultCharset())));
                if (result.isEmpty()) {
                    throw new GradleException("Cannot update wrapper to 'nightly' version as there is currently no version of that label");
                }
                String version = result.get("version");
                String url = result.get("downloadUrl");
                task.doFirst(t -> {
                    task.getLogger().lifecycle("Updating wrapper to nightly version: " + version + " (downloadUrl: " + url + ")");
                });
                task.setDistributionUrl(url);
                task.setGroup("wrapper");
                task.setDescription("Updates the samples to use the most recent nightly");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
