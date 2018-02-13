package org.gradle.samples

import org.gradle.api.provider.Property
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

class CMakeExtension {
    final Property<String> includeDir
    final Property<String> binary

    @Inject
    CMakeExtension(ObjectFactory objectFactory) {
        binary = objectFactory.property(String)
        includeDir = objectFactory.property(String)
    }
}