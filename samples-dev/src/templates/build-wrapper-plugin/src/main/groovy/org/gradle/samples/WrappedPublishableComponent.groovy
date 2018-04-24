package org.gradle.samples

import org.gradle.api.component.PublishableComponent
import org.gradle.api.internal.component.SoftwareComponentInternal

interface WrappedPublishableComponent extends PublishableComponent, SoftwareComponentInternal {}
    