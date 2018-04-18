package org.gradle.samples.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
class CreateEntitlement extends DefaultTask {
    @Input
    final Property<String> identifier = project.objects.property(String)

    @OutputFile
    final RegularFileProperty outputFile = newOutputFile()

    @TaskAction
    private void doCreate() {
        outputFile.asFile.get().text = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
\t<key>application-identifier</key>
\t<string>XXXXXXXXXX.${identifier.get()}</string>
\t<key>keychain-access-groups</key>
\t<array>
\t\t<string>XXXXXXXXXX.${identifier.get()}</string>
\t</array>
</dict>
</plist>       
"""
    }
}
