package org.gradle.samples.plugins;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.cpp.CppLibrary;
import org.gradle.nativeplatform.Linkage;
import org.gradle.samples.tasks.GenerateDummyCppSource;

import java.util.Arrays;

public class CppHeaderLibraryPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("cpp-library");

        CppLibrary library = project.getExtensions().getByType(CppLibrary.class);
        library.getLinkage().set(Arrays.asList(Linkage.STATIC));

        TaskProvider<GenerateDummyCppSource> generateTask = createTask(project.getTasks(), project);
        library.getSource().from(generateTask.flatMap(it -> it.getOutputFile()));
    }

    private static TaskProvider<GenerateDummyCppSource> createTask(TaskContainer tasks, Project project) {
        return tasks.register("generateCppHeaderSourceFile", GenerateDummyCppSource.class, (task) -> {
            Provider<RegularFile> sourceFile = project.getLayout().getBuildDirectory().file("dummy-source.cpp");
            task.getOutputFile().set(sourceFile);
            task.getSymbolName().set("__" + toSymbol(project.getPath()) + "_" + toSymbol(project.getName()) + "__");
        });
    }

    private static String toSymbol(String s) {
        return s.replace(":", "_").replace("-", "_");
    }
}
