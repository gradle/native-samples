package org.gradle.samples.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.file.FileTree;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal;
import org.gradle.api.tasks.TaskDependency;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.language.cpp.internal.MainLibraryVariant;
import org.gradle.language.nativeplatform.internal.PublicationAwareComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Example of publishing a different kind of publication that is consumed by another system.
 *
 * Gradle does not use this publication when resolving dependencies.
 */
public class CustomPublicationPlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        project.getPlugins().apply("maven-publish");
        project.getComponents().withType(PublicationAwareComponent.class, component -> {
            final MainLibraryVariant mainVariant = (MainLibraryVariant) component.getMainPublication();

            TaskProvider<Zip> publicationPackage = project.getTasks().register("createPublicationPackage", Zip.class, task -> {
                assert mainVariant.getUsages().size() == 1;
                UsageContext mainUsageContext = mainVariant.getUsages().iterator().next();

                task.dependsOn((Callable<List<TaskDependency>>) () -> mainUsageContext.getArtifacts().stream().map(PublishArtifact::getBuildDependencies).collect(Collectors.toList()));

                task.from((Callable<FileTree>) () -> {
                    assert mainUsageContext.getArtifacts().size() == 1;
                    PublishArtifact headersArtifact = mainUsageContext.getArtifacts().iterator().next();
                    return project.zipTree(headersArtifact.getFile());
                }, it -> it.into("include"));


                task.dependsOn((Callable<List<TaskDependency>>) () -> {
                    // TODO: Make MainLibraryVariant.getVariants() lazy container to avoid ordering issues
                    List<TaskDependency> result = new ArrayList<>();
                    for (SoftwareComponent variant : mainVariant.getVariants()) {
                        for (UsageContext usage : ((SoftwareComponentInternal) variant).getUsages()) {
                            for (PublishArtifact artifact : usage.getArtifacts()) {
                                task.from(artifact.getFile(), it -> it.into("lib/" + variant.getName()));
                                result.add(artifact.getBuildDependencies());
                            }
                        }
                    }

                    return result;
                });

                task.getDestinationDirectory().set(project.getLayout().getBuildDirectory().dir("custom-publications"));
                task.getArchiveClassifier().set(System.getProperty("os.name").toLowerCase().replace(" ", "-"));
                task.getArchiveFileName().set("custom-package.zip");
            });

            project.getExtensions().configure(PublishingExtension.class, publishing -> {
                publishing.getPublications().create("mainCustomPublication", MavenPublication.class, publication -> {
                    publication.setGroupId(project.getGroup().toString());
                    publication.setArtifactId(project.getName());
                    publication.setVersion(project.getVersion().toString());
                    publication.artifact(publicationPackage.get()); // TODO: This should be lazy
                    ((MavenPublicationInternal) publication).publishWithOriginalFileName();
                });
            });
        });
    }
}
