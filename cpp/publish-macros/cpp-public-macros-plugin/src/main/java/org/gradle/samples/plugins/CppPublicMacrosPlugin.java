package org.gradle.samples.plugins;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.ConfigurablePublishArtifact;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.PublishableComponent;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.AppliedPlugin;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.cpp.CppApplication;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppLibrary;
import org.gradle.language.cpp.internal.DefaultUsageContext;
import org.gradle.language.cpp.internal.MainLibraryVariant;
import org.gradle.language.cpp.tasks.CppCompile;
import org.gradle.language.nativeplatform.internal.PublicationAwareComponent;
import org.gradle.samples.tasks.GeneratePublicMacrosManifest;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class CppPublicMacrosPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("cpp-library", new Action<AppliedPlugin>() {
            @Override
            public void execute(AppliedPlugin appliedPlugin) {
                Configuration cppPublicMacrosElements = createElementsConfiguration(project.getConfigurations(), project.getObjects());
                Configuration cppPublicMacros = createConfiguration(project.getConfigurations(), project.getObjects());
                TaskProvider<GeneratePublicMacrosManifest> generateTask = createTask(project.getTasks());
                CppLibrary library = project.getExtensions().getByType(CppLibrary.class);
                configureLibrary(library, generateTask, project.getProviders(), cppPublicMacros);
                configureElementsConfiguration(cppPublicMacrosElements, generateTask);
                configurePublishing(project.getComponents(), project, cppPublicMacrosElements);
            }
        });

        project.getPluginManager().withPlugin("cpp-application", new Action<AppliedPlugin>() {
            @Override
            public void execute(AppliedPlugin appliedPlugin) {
                Configuration cppPublicMacros = createConfiguration(project.getConfigurations(), project.getObjects());
                CppApplication application = project.getExtensions().getByType(CppApplication.class);
                configureApplication(application, project.getProviders(), cppPublicMacros);
            }
        });
    }

    private static void configurePublishing(SoftwareComponentContainer components, Project project, Configuration cppPublicMacrosElements) {
        components.withType(PublicationAwareComponent.class, new Action<PublicationAwareComponent>() {
            @Override
            public void execute(PublicationAwareComponent component) {
                MainLibraryVariant mainVariant = (MainLibraryVariant) component.getMainPublication();
                mainVariant.addVariant(new PublicMacrosVariantComponent(project.getObjects(), project.getGroup().toString(), project.getName() + "_publicMacros", project.getVersion().toString(), cppPublicMacrosElements));
            }
        });

    }

    private static class PublicMacrosVariantComponent implements SoftwareComponentInternal, PublishableComponent {

        private final ObjectFactory objectFactory;
        private final String group;
        private final String name;
        private final String version;
        private final Configuration cppPublicMacrosElements;

        private PublicMacrosVariantComponent(ObjectFactory objectFactory, String group, String name, String version, Configuration cppPublicMacrosElements) {
            this.objectFactory = objectFactory;
            this.group = group;
            this.name = name;
            this.version = version;
            this.cppPublicMacrosElements = cppPublicMacrosElements;
        }

        @Override
        public ModuleVersionIdentifier getCoordinates() {
            return DefaultModuleVersionIdentifier.newId(group, name, version);
        }

        @Override
        public Set<? extends UsageContext> getUsages() {
            return Collections.singleton(new DefaultUsageContext(new DefaultUsageContext("cpp-public-macros", objectFactory.named(Usage.class, "cpp-public-macros"), cppPublicMacrosElements.getAttributes()), cppPublicMacrosElements.getAllArtifacts(), cppPublicMacrosElements));
        }

        @Override
        public String getName() {
            return "cppPublicMacros";
        }
    }

    private static void configureApplication(CppApplication application, ProviderFactory providerFactory, Configuration cppPublicMacros) {
        application.getBinaries().whenElementFinalized(new Action<CppBinary>() {
            @Override
            public void execute(CppBinary binary) {
                configureCompileTaskWithDependencies(binary.getCompileTask().get(), providerFactory, cppPublicMacros);
            }
        });
    }

    private static void configureElementsConfiguration(Configuration elements, TaskProvider<GeneratePublicMacrosManifest> generateTask) {
        elements.getOutgoing().artifact(generateTask.map(new Transformer<File, GeneratePublicMacrosManifest>() {
            @Override
            public File transform(GeneratePublicMacrosManifest it) {
                return it.getOutputFile().getAsFile().get();
            }
        }), new Action<ConfigurablePublishArtifact>() {
            @Override
            public void execute(ConfigurablePublishArtifact it) {
                it.builtBy(generateTask);
            }
        });
    }

    private static Configuration createConfiguration(ConfigurationContainer configurations, ObjectFactory objectFactory) {
        return configurations.create("cppPublicMacros", new Action<Configuration>() {
            @Override
            public void execute(Configuration configuration) {
                configurations.all(new Action<Configuration>() {
                    @Override
                    public void execute(Configuration it) {
                        if (it.getName().toLowerCase().endsWith("implementation")) {
                            configuration.extendsFrom(it);
                        }
                    }
                });
                configuration.setCanBeConsumed(false);
                configuration.setCanBeResolved(true);
                configuration.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, "cpp-public-macros"));
            }
        });
    }

    private static Configuration createElementsConfiguration(ConfigurationContainer configurations, ObjectFactory objectFactory) {
        return configurations.create("cppPublicMacrosElements", new Action<Configuration>() {
            @Override
            public void execute(Configuration configuration) {
                configurations.all(new Action<Configuration>() {
                    @Override
                    public void execute(Configuration it) {
                        if (it.getName().toLowerCase().endsWith("implementation")) {
                            configuration.extendsFrom(it);
                        }
                    }
                });
                configuration.setCanBeConsumed(true);
                configuration.setCanBeResolved(false);
                configuration.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, "cpp-public-macros"));
            }
        });
    }

    private static TaskProvider<GeneratePublicMacrosManifest> createTask(TaskContainer tasks) {
        return tasks.register("generatePublicMacros", GeneratePublicMacrosManifest.class, new Action<GeneratePublicMacrosManifest>() {
            @Override
            public void execute(GeneratePublicMacrosManifest task) {
                task.getOutputFile().set(new File(task.getTemporaryDir(), "public-macros.txt"));
            }
        });
    }

    private static void configureLibrary(CppLibrary library, TaskProvider<GeneratePublicMacrosManifest> generateTask, ProviderFactory providerFactory, Configuration cppPublicMacros) {
        library.getBinaries().whenElementFinalized(new Action<CppBinary>() {
            @Override
            public void execute(CppBinary binary) {
                configureCompileTask(binary.getCompileTask().get(), generateTask, providerFactory, cppPublicMacros);
            }
        });
    }

    private static void configureCompileTask(CppCompile compileTask, TaskProvider<GeneratePublicMacrosManifest> generateTask, ProviderFactory providerFactory, Configuration cppPublicMacros) {
        compileTask.dependsOn(generateTask);
        compileTask.getCompilerArgs().addAll(generateTask.map(new Transformer<Iterable<? extends String>, GeneratePublicMacrosManifest>() {
            @Override
            public Iterable<? extends String> transform(GeneratePublicMacrosManifest it) {
                List<String> result = new ArrayList<>();
                for (GeneratePublicMacrosManifest.Macro macro : it.getMacros().get()) {
                    result.add(macro.getAsFlag());
                }
                return result;
            }
        }));
        configureCompileTaskWithDependencies(compileTask, providerFactory, cppPublicMacros);
    }

    private static void configureCompileTaskWithDependencies(CppCompile compileTask, ProviderFactory providerFactory, Configuration cppPublicMacros) {
        compileTask.dependsOn(cppPublicMacros);
        compileTask.getCompilerArgs().addAll(providerFactory.provider(new Callable<Iterable<String>>() {
            @Override
            public Iterable<String> call() throws Exception {
                List<String> result = new ArrayList<>();
                for (File file : cppPublicMacros.resolve()) {
                    for (String macro : Files.readAllLines(file.toPath())) {
                        result.add("-D" + macro);
                    }
                }

                return result;
            }
        }));
    }
}
