package org.gradle.samples.plugins.generators;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

/**
 * Clones/pulls changes from external repo.
 */
public class SyncExternalRepoTask extends DefaultTask {
    private final Property<String> repoUrl = getProject().getObjects().property(String.class);
    private final DirectoryProperty checkoutDirectory = getProject().getObjects().directoryProperty();

    public SyncExternalRepoTask() {
        getOutputs().upToDateWhen(it -> false);
    }

    @TaskAction
    private void checkout() throws IOException, GitAPIException {
        File checkoutDir = checkoutDirectory.get().getAsFile();
        if (new File(checkoutDir, ".git").exists()) {
            getLogger().lifecycle("Pull " + repoUrl.get() + " into " + checkoutDir);
            try (Git git = Git.open(checkoutDir)) {
                git.pull().setFastForward(MergeCommand.FastForwardMode.FF_ONLY).call();
            }
        } else {
            getLogger().lifecycle("Clone " + repoUrl.get() + " into " + checkoutDir);
            Git git = Git.cloneRepository()
                    .setURI(repoUrl.get())
                    .setDirectory(checkoutDir)
                    .call();
            git.close();
        }
    }


    @Input
    public Property<String> getRepoUrl() {
        return repoUrl;
    }

    @OutputDirectory
    public DirectoryProperty getCheckoutDirectory() {
        return checkoutDirectory;
    }
}
