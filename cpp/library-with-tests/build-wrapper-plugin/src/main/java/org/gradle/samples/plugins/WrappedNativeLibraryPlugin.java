package org.gradle.samples.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.PublishableComponent;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.artifacts.ArtifactAttributes;
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier;
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact;
import org.gradle.api.internal.artifacts.transform.UnzipTransform;
import org.gradle.api.internal.attributes.AttributeContainerInternal;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.internal.DefaultUsageContext;
import org.gradle.language.cpp.internal.MainLibraryVariant;
import org.gradle.language.nativeplatform.internal.PublicationAwareComponent;
import org.gradle.language.plugins.NativeBasePlugin;
import org.gradle.nativeplatform.Linkage;

import javax.inject.Inject;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.gradle.api.artifacts.type.ArtifactTypeDefinition.DIRECTORY_TYPE;
import static org.gradle.api.artifacts.type.ArtifactTypeDefinition.ZIP_TYPE;
import static org.gradle.language.cpp.CppBinary.LINKAGE_ATTRIBUTE;

public class WrappedNativeLibraryPlugin implements Plugin<Project> {
    private final ImmutableAttributesFactory attributesFactory;

    @Inject
    public WrappedNativeLibraryPlugin(ImmutableAttributesFactory attributesFactory) {
        this.attributesFactory = attributesFactory;
    }

    @Override
    public void apply(final Project project) {
        project.getPluginManager().apply("org.gradle.samples.wrapped-native-base");

        /*
         * Define some configurations to present the outputs of this build
         * to other Gradle projects.
         */
        final Usage cppApiUsage = project.getObjects().named(Usage.class, Usage.C_PLUS_PLUS_API);
        final Usage linkUsage = project.getObjects().named(Usage.class, Usage.NATIVE_LINK);
        final Usage runtimeUsage = project.getObjects().named(Usage.class, Usage.NATIVE_RUNTIME);

        // dependencies of the library
        Configuration implementation = project.getConfigurations().create("implementation", it -> {
            it.setCanBeConsumed(false);
            it.setCanBeResolved(false);
        });

        // incoming compile time headers - this represents the headers we consume
        project.getConfigurations().create("cppCompile", it -> {
            it.setCanBeConsumed(false);
            it.extendsFrom(implementation);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, cppApiUsage);
        });

        // incoming linktime libraries (i.e. static libraries) - this represents the libraries we consume
        project.getConfigurations().create("cppLinkDebug", it -> {
            it.setCanBeConsumed(false);
            it.extendsFrom(implementation);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, linkUsage);
            it.getAttributes().attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
            it.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false);
        });
        project.getConfigurations().create("cppLinkRelease", it -> {
            it.setCanBeConsumed(false);
            it.extendsFrom(implementation);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, linkUsage);
            it.getAttributes().attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
            it.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true);
        });

        // incoming runtime libraries (i.e. shared libraries) - this represents the libraries we consume
        project.getConfigurations().create("cppRuntimeDebug", it -> {
            it.setCanBeConsumed(false);
            it.extendsFrom(implementation);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage);
            it.getAttributes().attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
            it.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false);
        });
        project.getConfigurations().create("cppRuntimeRelease", it -> {
            it.setCanBeConsumed(false);
            it.extendsFrom(implementation);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage);
            it.getAttributes().attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
            it.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true);
        });

        // outgoing public headers - this represents the headers we expose (including transitive headers)
        Configuration headers = project.getConfigurations().create("headers", it -> {
            it.setCanBeResolved(false);
            it.extendsFrom(implementation);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, cppApiUsage);
            it.getAttributes().attribute(ArtifactAttributes.ARTIFACT_FORMAT, DIRECTORY_TYPE);
        });

        // outgoing linktime libraries (i.e. static libraries) - this represents the libraries we expose (including transitive headers)
        Configuration linkDebug = project.getConfigurations().create("linkDebug", it -> {
            it.setCanBeResolved(false);
            it.extendsFrom(implementation);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, linkUsage);
            it.getAttributes().attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
            it.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false);
        });
        Configuration linkRelease = project.getConfigurations().create("linkRelease", it -> {
            it.setCanBeResolved(false);
            it.extendsFrom(implementation);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, linkUsage);
            it.getAttributes().attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
            it.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true);
        });

        // outgoing runtime libraries (i.e. shared libraries) - this represents the libraries we expose (including transitive headers)
        Configuration runtimeDebug = project.getConfigurations().create("runtimeDebug", it -> {
            it.setCanBeResolved(false);
            it.extendsFrom(implementation);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage);
            it.getAttributes().attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
            it.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false);
        });
        Configuration runtimeRelease = project.getConfigurations().create("runtimeRelease", it -> {
            it.setCanBeResolved(false);
            it.extendsFrom(implementation);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage);
            it.getAttributes().attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
            it.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true);
        });

        // Using the publication generation logic from Gradle
        project.getPlugins().apply(NativeBasePlugin.class);

        /*
        TODO: We need to detangle this from the built-in plugins so that external plugins can opt into this same behavior
         */
        // Create components expected by the native Gradle publication code
        DefaultMainPublication mainComponent = new DefaultMainPublication(project.getProviders().provider(() -> project.getName()), cppApiUsage, headers, attributesFactory, project.getObjects());

        mainComponent.getMainPublication().addVariant(new DefaultWrappedPublishableComponent("debug", project.getGroup(), project.getName(), project.getVersion(), linkUsage, linkDebug, runtimeUsage, runtimeDebug, attributesFactory));
        mainComponent.getMainPublication().addVariant(new DefaultWrappedPublishableComponent("release", project.getGroup(), project.getName(), project.getVersion(), linkUsage, linkRelease, runtimeUsage, runtimeRelease, attributesFactory));

        TaskProvider<Zip> headersZip = project.getTasks().register("cppHeaders", Zip.class, task -> {
            task.from(headers.getArtifacts().getFiles().getAsFileTree());
            task.getDestinationDirectory().set(project.getLayout().getBuildDirectory().dir("headers"));
            task.getArchiveClassifier().set("cpp-api-headers");
            task.getArchiveFileName().set("cpp-api-headers.zip");
        });
        // TODO: ArchivePublishArtifact should take a `TaskProvider`
        mainComponent.getMainPublication().addArtifact(new ArchivePublishArtifact(headersZip.get()));

        project.getDependencies().registerTransform(UnzipTransform.class, variantTransform -> {
            variantTransform.getFrom().attribute(ArtifactAttributes.ARTIFACT_FORMAT, ZIP_TYPE);
            variantTransform.getFrom().attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, Usage.C_PLUS_PLUS_API));
            variantTransform.getTo().attribute(ArtifactAttributes.ARTIFACT_FORMAT, DIRECTORY_TYPE);
            variantTransform.getTo().attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, Usage.C_PLUS_PLUS_API));
        });

        project.getComponents().add(mainComponent);
    }

    private static class DefaultMainPublication implements PublicationAwareComponent {
        private final Provider<String> baseName;
        private final MainLibraryVariant mainVariant;

        public DefaultMainPublication(Provider<String> baseName, Usage apiUsage, Configuration api, ImmutableAttributesFactory immutableAttributesFactory, ObjectFactory objectFactory) {
            this.baseName = baseName;

            AttributeContainer publicationAttributes = immutableAttributesFactory.mutable();
            publicationAttributes.attribute(Usage.USAGE_ATTRIBUTE, apiUsage);
            publicationAttributes.attribute(ArtifactAttributes.ARTIFACT_FORMAT, ZIP_TYPE);
            this.mainVariant = new MainLibraryVariant("api", apiUsage, api, publicationAttributes, objectFactory);
        }

        @Override
        public Provider<String> getBaseName() {
            return baseName;
        }

        @Override
        public MainLibraryVariant getMainPublication() {
            return mainVariant;
        }

        @Override
        public String getName() {
            return "main";
        }
    }

    private static class DefaultWrappedPublishableComponent implements PublishableComponent, SoftwareComponentInternal {
        private final String variantName;
        private final Object group;
        private final String projectName;
        private final Object version;
        private final Usage linkUsage;
        private final Configuration link;
        private final Usage runtimeUsage;
        private final Configuration runtime;
        private final ImmutableAttributesFactory attributesFactory;

        public DefaultWrappedPublishableComponent(String variantName, Object group, String projectName, Object version, Usage linkUsage, Configuration link, Usage runtimeUsage, Configuration runtime, ImmutableAttributesFactory attributesFactory) {
            this.attributesFactory = attributesFactory;
            this.variantName = variantName;
            this.group = group;
            this.projectName = projectName;
            this.version = version;
            this.linkUsage = linkUsage;
            this.link = link;
            this.runtimeUsage = runtimeUsage;
            this.runtime = runtime;
        }

        @Override
        public ModuleVersionIdentifier getCoordinates() {
            return DefaultModuleVersionIdentifier.newId(group.toString(), projectName + "_" + variantName, version.toString());
        }

        @Override
        public Set<? extends UsageContext> getUsages() {
            Set<UsageContext> result = new HashSet<>();

            AttributeContainer linkAttributes = attributesFactory.mutable((AttributeContainerInternal) link.getAttributes().getAttributes());
            linkAttributes.attribute(Usage.USAGE_ATTRIBUTE, linkUsage);
            linkAttributes.attribute(LINKAGE_ATTRIBUTE, Linkage.SHARED);
            result.add(new DefaultUsageContext(variantName + "Link", linkAttributes, link.getArtifacts(), link));

            AttributeContainer runtimeAttributes = attributesFactory.mutable((AttributeContainerInternal) runtime.getAttributes().getAttributes());
            runtimeAttributes.attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage);
            runtimeAttributes.attribute(LINKAGE_ATTRIBUTE, Linkage.SHARED);
            result.add(new DefaultUsageContext(variantName + "Runtime", runtimeAttributes, runtime.getArtifacts(), runtime));

            return result;
        }

        @Override
        public String getName() {
            return variantName;
        }
    }
}
