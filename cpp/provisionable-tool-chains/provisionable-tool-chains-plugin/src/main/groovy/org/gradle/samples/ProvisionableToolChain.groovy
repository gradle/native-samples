package org.gradle.samples

import groovy.transform.CompileStatic
import jdk.internal.util.xml.impl.Input
import org.gradle.api.Named
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option

import javax.inject.Inject

@CompileStatic
class ProvisionableToolChain implements Named {
    @Input
    final String name

    @Input
    @Optional
    final Property<URL> url

    @Input
    @Optional
    final Property<String> md5

    @Input
    final Property<Class> type

    final DirectoryProperty location

    @Input
    @Optional
    final Property<String> cCompilerExecutable

    @Input
    @Optional
    final Property<String> cppCompilerExecutable

    @Input
    @Optional
    final Property<String> linkerExecutable

    @Input
    @Optional
    final Property<String> staticLibArchiverExecutable

    @Inject
    ProvisionableToolChain(ObjectFactory objectFactory, ProjectLayout projectLayout, ProviderFactory providerFactory, String name) {
        this.name = name
        this.url = objectFactory.property(URL)
        this.md5 = objectFactory.property(String)
        this.type = objectFactory.property(Class)
        this.location = projectLayout.directoryProperty()

        this.cCompilerExecutable = objectFactory.property(String)
        this.cppCompilerExecutable = objectFactory.property(String)
        this.linkerExecutable = objectFactory.property(String)
        this.staticLibArchiverExecutable = objectFactory.property(String)
    }

    @Input
    protected File getLocationFile() {
        return location.getOrNull().asFile
    }
}
