package org.gradle.samples.plugins.gcov;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.project.ProjectIdentifier;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppLibrary;
import org.gradle.model.Mutate;
import org.gradle.model.RuleSource;
import org.gradle.nativeplatform.test.cpp.CppTestExecutable;
import org.gradle.nativeplatform.test.cpp.CppTestSuite;
import org.gradle.nativeplatform.toolchain.Gcc;
import org.gradle.nativeplatform.toolchain.NativeToolChainRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

// TODO: Missing some sanity check to avoid hijacking the debug variant for coverage when the user wants the vanilla variant
public class GCovPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        TaskProvider<Task> runCoverageTask = project.getTasks().register("runCoverage", task -> {
            task.setGroup("Code Coverage");
            task.setDescription("Run gcov on C++ unit test binary");
        });
        project.getPluginManager().withPlugin("cpp-unit-test", appliedPlugin -> {
            Provider<List<String>> gcovCompilerFlags = project.provider(() -> {
                if (project.getGradle().getTaskGraph().hasTask(runCoverageTask.get())) {
                    return Arrays.asList("-fprofile-arcs", "-ftest-coverage");
                }
                return Arrays.asList();
            });

            // TODO: Add support for application
            project.getPluginManager().withPlugin("cpp-library", appliedLibraryPlugin -> {
                CppLibrary library = project.getExtensions().getByType(CppLibrary.class);
                library.getBinaries().configureEach(CppBinary.class, binary -> {
                    if (binary.isDebuggable() && !binary.isOptimized()) {
                        binary.getCompileTask().get().getCompilerArgs().addAll(gcovCompilerFlags);
                    }
                });
            });

            CppTestSuite unitTest = project.getExtensions().getByType(CppTestSuite.class);
            unitTest.getBinaries().configureEach(CppTestExecutable.class, executable -> {
                executable.getCompileTask().get().getCompilerArgs().addAll(gcovCompilerFlags);
                executable.getLinkTask().get().getLinkerArgs().addAll(project.provider(() -> {
                    if (project.getGradle().getTaskGraph().hasTask(runCoverageTask.get())) {
                        return Arrays.asList("-fprofile-arcs", "-lgcov");
                    }
                    return Arrays.asList();
                }));
                runCoverageTask.configure(it -> it.dependsOn(executable.getRunTask()));
            });
        });
    }

    static class WholeArchiveRule extends RuleSource {
        @Mutate
        void addWholeArchiveFlags(NativeToolChainRegistry toolChains, ProjectIdentifier projectIdentifier) {
            toolChains.withType(Gcc.class, toolChain -> {
                toolChain.eachPlatform(platformToolChain -> {
                    platformToolChain.getLinker().withArguments(args -> {
                        // WARNING: Casting ProjectIdentifier is very evil but legal ;-)
                        Project project = (Project)projectIdentifier;
                        if (project.getGradle().getTaskGraph().hasTask(toTaskPath(project, "runCoverage"))) {
                            // TODO: Assuming linux extension
                            for (ListIterator<String> it = args.listIterator(); it.hasNext();) {
                                String arg = it.next();
                                if (arg.endsWith(".a") && arg.startsWith(project.getProjectDir().getAbsolutePath())) {
                                    // TODO: Assuming not first entry
                                    args.add(it.previousIndex(), "-Wl,--whole-archive");

                                    // TODO: Assuming not last entry
                                    args.add(it.nextIndex(), "-Wl,--no-whole-archive");
                                }
                            }
                        }
                    });
                });
            });
        }

        static String toTaskPath(Project project, String taskName) {
            if (project.getParent() == null) {
                return ":" + taskName;
            }
            return project.getPath() + ":" + taskName;
        }
    }
}
