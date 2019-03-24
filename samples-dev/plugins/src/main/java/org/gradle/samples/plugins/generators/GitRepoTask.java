package org.gradle.samples.plugins.generators;

public class GitRepoTask extends UpdateRepoTask {
    @Override
    boolean isDeleteRepo() {
        return true;
    }
}
