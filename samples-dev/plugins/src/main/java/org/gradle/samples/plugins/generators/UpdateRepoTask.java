package org.gradle.samples.plugins.generators;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RelativePath;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class UpdateRepoTask extends DefaultTask {
    private final DirectoryProperty sampleDir = getProject().getObjects().directoryProperty();
    private final List<Action<? super Changes>> changes = new ArrayList<>();

    boolean isDeleteRepo() {
        return false;
    }

    public void change(Action<? super Changes> cl) {
        changes.add(cl);
    }

    @TaskAction
    private void createRepo() throws IOException, GitAPIException {
        File destDir = sampleDir.get().getAsFile();
        File parentIgnoreFile = new File(destDir.getParentFile(), ".gitignore");
        String parentIgnore = destDir.getName() + "/\n";
        if (!parentIgnoreFile.isFile() || !FileUtils.readFileToString(parentIgnoreFile, Charset.defaultCharset()).contains(parentIgnore)) {
            FileUtils.write(parentIgnoreFile, parentIgnore, Charset.defaultCharset(), true);
        }
        if (isDeleteRepo()) {
            getProject().delete(new File(destDir, ".git"));
        }
        InitCommand init = Git.init();
        try (Git git = init.setDirectory(destDir).call()) {
            FileUtils.write(new File(destDir, ".gitignore"), "\n/.gradle\nbuild\n/.build\n", Charset.defaultCharset());
            List<RelativePath> files = new ArrayList<>();
            getProject().fileTree(destDir).visit( f -> {
                if (f.getFile().isFile()) {
                    files.add(f.getRelativePath());
                }
            });
            AddCommand add = git.add();
            add.addFilepattern(".gitignore");
            files.forEach(it -> {
                add.addFilepattern(it.getPathString());
            });
            add.call();

            for (Action<? super Changes> change : changes) {
                Changes changes = new Changes(destDir, git);
                change.execute(changes);
                if (changes.getBranch() != null) {
                    git.branchCreate().setName(changes.getBranch()).call();
                    git.checkout().setName(changes.getBranch()).call();
                }
                git.commit().setAll(true).setMessage(changes.getMessage()).call();
                if (changes.getTag() != null) {
                    git.tagDelete().setTags(changes.getTag()).call();
                    git.tag().setName(changes.getTag()).call();
                }
            }
            if (changes.isEmpty()) {
                git.commit().setAll(true).setMessage("initial version").call();
            }
        }
    }


    public DirectoryProperty getSampleDir() {
        return sampleDir;
    }
}
