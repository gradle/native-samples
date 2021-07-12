package org.gradle.samples.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.language.cpp.CppLibrary;
import org.gradle.language.cpp.internal.DefaultCppStaticLibrary;
import org.gradle.language.cpp.tasks.CppCompile;
import org.gradle.nativeplatform.Linkage;
import org.gradle.nativeplatform.tasks.CreateStaticLibrary;

import java.util.Arrays;

public class CppHeaderLibraryPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("cpp-library");

        CppLibrary library = project.getExtensions().getByType(CppLibrary.class);
        library.getLinkage().set(Arrays.asList(Linkage.STATIC));

        project.getComponents().withType(DefaultCppStaticLibrary.class, staticLibrary ->
            staticLibrary.getLinkElements().get().outgoing(outgoingLinkElementsConfiguration ->
                    outgoingLinkElementsConfiguration.getArtifacts().removeIf(publishArtifact ->
                            publishArtifact.getFile().equals(staticLibrary.getLinkFile().get().getAsFile()))));

        project.getTasks().withType(CreateStaticLibrary.class, task -> task.setEnabled(false));
        project.getTasks().withType(CppCompile.class, task -> task.setEnabled(false));

    }

}
