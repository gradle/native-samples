package org.gradle.samples.plugins;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurablePublishArtifact;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.attributes.Usage;
import org.gradle.api.internal.project.ProjectIdentifier;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppSourceSet;
import org.gradle.model.Model;
import org.gradle.model.ModelMap;
import org.gradle.model.Mutate;
import org.gradle.model.RuleSource;
import org.gradle.nativeplatform.*;
import org.gradle.platform.base.BinarySpec;
import org.gradle.platform.base.ComponentSpec;
import org.gradle.platform.base.ComponentSpecContainer;
import org.gradle.platform.base.SourceComponentSpec;

public class SoftwareModelBridgePlugin implements Plugin<Project> {
    public void apply(Project project) {

    }

    public static class Rule extends RuleSource {
        @Model
        ConfigurationContainer configurations(ProjectIdentifier project) {
            return ((Project) project).getConfigurations();
        }

        @Mutate
        void bridgeMainNativeComponent(ConfigurationContainer configurations, ComponentSpecContainer components, BuildTypeContainer buildTypes, ServiceRegistry serviceRegistry) {
            ComponentSpec component = null;
            if (components.size() == 0) {
                // Nothing to do
                return;
            } else if (components.size() == 1) {
                // Take first component
                component = components.iterator().next();
            } else {
                // In case of multiple component, only the 'main' component is bridged
                component = components.get("main");
            }

            assert component instanceof TargetedNativeComponent;
            final TargetedNativeComponent mainComponent = (TargetedNativeComponent) component;


            ObjectFactory objectFactory = serviceRegistry.get(ObjectFactory.class);
            Usage cppApiUsage = objectFactory.named(Usage.class, Usage.C_PLUS_PLUS_API);
            Usage linkUsage = objectFactory.named(Usage.class, Usage.NATIVE_LINK);
            Usage runtimeUsage = objectFactory.named(Usage.class, Usage.NATIVE_RUNTIME);
            Configuration implementation = configurations.maybeCreate("implementation");

            if (component instanceof NativeLibrarySpec) {
                // outgoing public headers - this represents the headers we expose (including transitive headers)
                Configuration headers = configurations.create("headers");
                headers.setCanBeResolved(false);
                headers.extendsFrom(implementation);
                headers.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, cppApiUsage);
                headers.getOutgoing().artifact(compileArtifact(component), new Action<ConfigurablePublishArtifact>() {
                    @Override
                    public void execute(ConfigurablePublishArtifact artifact) {
                        artifact.builtBy(((SourceComponentSpec) mainComponent).getSources().withType(CppSourceSet.class).iterator().next().getBuildDependencies());
                    }
                });
            }

            for (BinarySpec binary : mainComponent.getBinaries()) {
                assert binary instanceof NativeBinarySpec;

                // outgoing linktime libraries (i.e. static libraries) - this represents the libraries we expose (including transitive headers)
                Configuration link = configurations.create("link" + binary.getName());
                link.setCanBeResolved(false);
                link.extendsFrom(implementation);
                link.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, linkUsage);
                link.attributes(buildTypeAttributes(binary, buildTypes));
                link.attributes(binaryAttributes(binary));
                link.getOutgoing().artifact(linkArtifact(binary), new Action<ConfigurablePublishArtifact>() {
                    @Override
                    public void execute(ConfigurablePublishArtifact artifact) {
                        artifact.builtBy(binary.getBuildDependencies());
                    }
                });


                // outgoing runtime libraries (i.e. shared libraries) - this represents the libraries we expose (including transitive headers)
                Configuration runtime = configurations.create("runtime" + binary.getName());
                runtime.setCanBeResolved(false);
                runtime.extendsFrom(implementation);
                runtime.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage);
                runtime.attributes(buildTypeAttributes(binary, buildTypes));
                runtime.attributes(binaryAttributes(binary));
                if (binary instanceof SharedLibraryBinarySpec) {
                    runtime.getOutgoing().artifact(runtimeArtifact(binary), new Action<ConfigurablePublishArtifact>() {
                        @Override
                        public void execute(ConfigurablePublishArtifact artifact) {
                            artifact.builtBy(binary.getBuildDependencies());
                        }
                    });
                }
            }
        }

        private static Action<AttributeContainer> buildTypeAttributes(BinarySpec binary, BuildTypeContainer buildTypes) {
            assert binary instanceof NativeBinarySpec;

            return new Action<AttributeContainer>() {
                @Override
                public void execute(AttributeContainer attributes) {
                    if (isDebugBuildType() || isDefaultBuildType()) {
                        attributes.attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
                        attributes.attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false);
                    } else if (isReleaseBuildType()) {
                        attributes.attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
                        attributes.attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true);
                    }
                    // Else, we don't assume anything
                }

                private boolean isDebugBuildType() {
                    return ((NativeBinarySpec) binary).getBuildType().getName().equals("debug");
                }

                private boolean isReleaseBuildType() {
                    return ((NativeBinarySpec) binary).getBuildType().getName().equals("release");
                }

                private boolean isDefaultBuildType() {
                    return buildTypes.size() == 1 && ((NativeBinarySpec) binary).getBuildType().getName().equals("default");
                }
            };
        }

        private static Object compileArtifact(ComponentSpec component) {
            assert component instanceof SourceComponentSpec;
            ModelMap<CppSourceSet> sourceSets = ((SourceComponentSpec) component).getSources().withType(CppSourceSet.class);
            assert sourceSets.size() == 1;

            CppSourceSet sourceSet = sourceSets.iterator().next();

            assert sourceSet.getExportedHeaders().getSrcDirs().size() == 1;

            return sourceSet.getExportedHeaders().getSrcDirs().iterator().next();
        }

        private static Object linkArtifact(BinarySpec binary) {
            if (binary instanceof StaticLibraryBinarySpec) {
                return ((StaticLibraryBinarySpec) binary).getStaticLibraryFile();
            } else if (binary instanceof SharedLibraryBinarySpec) {
                return ((SharedLibraryBinarySpec) binary).getSharedLibraryLinkFile();
            }
            throw new UnsupportedOperationException("Binary not supported for link artifact");
        }

        private static Object runtimeArtifact(BinarySpec binary) {
            if (binary instanceof SharedLibraryBinarySpec) {
                return ((SharedLibraryBinarySpec) binary).getSharedLibraryFile();
            }
            throw new UnsupportedOperationException("Binary not supported for runtime artifact");
        }

        private static Action<AttributeContainer> binaryAttributes(BinarySpec binary) {
            return new Action<AttributeContainer>() {
                @Override
                public void execute(AttributeContainer attributes) {
                    if (binary instanceof StaticLibraryBinarySpec) {
                        attributes.attribute(CppBinary.LINKAGE_ATTRIBUTE, Linkage.STATIC);
                    } else if (binary instanceof SharedLibraryBinarySpec) {
                        attributes.attribute(CppBinary.LINKAGE_ATTRIBUTE, Linkage.SHARED);
                    }
                }
            };
        }

        @Mutate
        void resolve(TaskContainer tasks, ConfigurationContainer configurations) {
            // Ensure the ConfigurationContainer rules are resolved
        }
    }
}