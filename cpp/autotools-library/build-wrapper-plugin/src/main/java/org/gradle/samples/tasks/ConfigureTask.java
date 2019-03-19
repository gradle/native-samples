package org.gradle.samples.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public class ConfigureTask extends DefaultTask {
    private final DirectoryProperty sourceDirectory = getProject().getObjects().directoryProperty();
    private final DirectoryProperty makeDirectory = getProject().getObjects().directoryProperty();
    private final DirectoryProperty prefixDirectory = getProject().getObjects().directoryProperty();
    private final ListProperty<String> arguments = getProject().getObjects().listProperty(String.class);

    @TaskAction
    public void runConfigure() {
        getProject().exec(execSpec -> {
            execSpec.setWorkingDir(getMakeDirectory());

            execSpec.executable(getSourceDirectory().file("configure").get().getAsFile());
            execSpec.args("--prefix=" + getPrefixDirectory().get().getAsFile().getAbsolutePath());
            execSpec.args(getArguments().get());
        });
    }

    @InputDirectory
    public DirectoryProperty getSourceDirectory() {
        return sourceDirectory;
    }

    @OutputDirectory
    public DirectoryProperty getMakeDirectory() {
        return makeDirectory;
    }

    @Internal
    public DirectoryProperty getPrefixDirectory() {
        return prefixDirectory;
    }

    @Input
    public ListProperty<String> getArguments() {
        return arguments;
    }
}
