package org.gradle.samples.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GeneratePublicMacrosManifest extends DefaultTask {
    private final ListProperty<Macro> macros = getProject().getObjects().listProperty(Macro.class).empty();
    private final RegularFileProperty outputFile = getProject().getObjects().fileProperty();

    @TaskAction
    private void doGenerate() throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(outputFile.getAsFile().get())) {
            for (Macro macro : macros.get()) {
                out.println(macro);
            }
        }
    }

    @Nested
    @Optional
    public ListProperty<Macro> getMacros() {
        return macros;
    }

    @OutputFile
    public RegularFileProperty getOutputFile() {
        return outputFile;
    }

    public static Macro macro(String key) {
        return macro(key, null);
    }

    public static Macro macro(String key, String value) {
        return new DefaultMacro(key, value);
    }

    public interface Macro {
        String getAsFlag();
    }

    private static class DefaultMacro implements Macro {
        private final String key;
        private final String value;

        DefaultMacro(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Input
        public String getKey() {
            return key;
        }

        @Input
        @Optional
        public String getValue() {
            return value;
        }

        @Override
        public String getAsFlag() {
            return "-D" + toString();
        }

        public String toString() {
            String result = key;
            if (value != null) {
                result += "=" + value;
            }
            return result;
        }
    }
}
