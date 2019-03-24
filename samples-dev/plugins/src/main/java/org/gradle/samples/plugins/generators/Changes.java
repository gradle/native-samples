package org.gradle.samples.plugins.generators;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Changes {
    private String tag;
    private String branch;
    private String message;
    private final File workDir;
    private final Git git;

    public Changes(File workDir, Git git) {
        this.workDir = workDir;
        this.git = git;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Changes tag(String tag) {
        this.tag = tag;
        return this;
    }

    public String getBranch() {
        return this.branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Changes branch(String branch) {
        this.branch = branch;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Changes message(String message) {
        this.message = message;
        return this;
    }

    public File file(String name) {
        return new File(this.workDir, name);
    }

    public void changeContent(String srcFileName, String pattern, String replacement) throws IOException {
        File srcFile = new File(this.workDir, srcFileName);
        String original = FileUtils.readFileToString(srcFile, Charset.defaultCharset());
        if (!original.contains(pattern)) {
            throw new IllegalArgumentException("Source file " + srcFile + " does not contain anything that matches '" + pattern + "'.");
        } else {
            FileUtils.write(srcFile, original.replace(pattern, replacement), Charset.defaultCharset());
        }
    }

    public void checkout(String branch) throws GitAPIException {
        this.git.checkout().setName(branch).call();
    }

    public File getWorkDir() {
        return this.workDir;
    }

    public Git getGit() {
        return this.git;
    }
}
