package org.gradle.samples.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Zip
import org.gradle.language.cpp.internal.MainLibraryVariant
import org.gradle.language.nativeplatform.internal.PublicationAwareComponent

/**
 * Example of publishing a different kind of publication that is consumed by another system.
 *
 * Gradle does not use this publication when resolving dependencies.
 */
class CustomPublicationPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.apply 'maven-publish'
        project.components.withType(PublicationAwareComponent) {
            MainLibraryVariant mainVariant = (MainLibraryVariant) it.mainPublication

            Zip publicationPackage = project.tasks.create("createPublicationPackage", Zip) {
                assert mainVariant.usages.size() == 1
                UsageContext mainUsageContext = mainVariant.usages.iterator().next()

                dependsOn { mainUsageContext.artifacts*.buildDependencies }

                from({
                    assert mainUsageContext.artifacts.size() == 1
                    PublishArtifact headersArtifact = mainUsageContext.artifacts.iterator().next()
                    project.zipTree(headersArtifact.file)
                }) {
                    into 'include'
                }


                dependsOn {
                    // TODO: Make MainLibraryVariant.getVariants() lazy container to avoid ordering issues
                    for (SoftwareComponent variant : mainVariant.variants) {
                        from(variant*.usages.flatten()*.artifacts*.file) {
                            into "lib/${variant.name}"
                        }
                    }

                    return mainVariant.variants*.usages.flatten()*.artifacts*.buildDependencies
                }

                // TODO - should track changes to build directory
                destinationDir = new File(project.getBuildDir(), "custom-publications")
                classifier = System.getProperty("os.name").toLowerCase().replace(" ", "-")
                archiveName = "custom-package.zip"
            }

            project.extensions.configure(PublishingExtension) {
                it.publications.create("mainCustomPublication", MavenPublication) {
                    groupId = project.getGroup().toString()
                    artifactId = project.getName()
                    version = project.getVersion().toString()
                    artifact publicationPackage
                    publishWithOriginalFileName()
                }
            }
        }
    }
}
