package org.gradle.samples.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;

@CacheableTask
public class GenerateDummyCppSource extends DefaultTask {
    private final Property<String> symbolName = getProject().getObjects().property(String.class);
    private final RegularFileProperty outputFile = newOutputFile();

    @TaskAction
    private void doGenerate() throws IOException {
        String source = "void " + symbolName.get() + "() {}";
        Files.write(outputFile.getAsFile().get().toPath(), source.getBytes());
    }

    @Input
    public Property<String> getSymbolName() {
        return symbolName;
    }

    @OutputFile
    public RegularFileProperty getOutputFile() {
        return outputFile;
    }
}
