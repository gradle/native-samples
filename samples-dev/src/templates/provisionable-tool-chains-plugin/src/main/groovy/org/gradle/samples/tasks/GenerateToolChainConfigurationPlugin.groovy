package org.gradle.samples.tasks

import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.nativeplatform.toolchain.NativeToolChain

@CompileStatic
class GenerateToolChainConfigurationPlugin extends DefaultTask {
    @Input
    final Property<Class> toolChainType = project.objects.property(Class)

    @Input
    final Property<String> toolChainName = project.objects.property(String)

    @Input
    final Property<File> toolChainLocation = project.objects.property(File)

    @Input
    final Property<String> packageName = project.objects.property(String)

    @Input
    @Optional
    final Property<String> cCompilerExecutable = project.objects.property(String)

    @Input
    @Optional
    final Property<String> cppCompilerExecutable = project.objects.property(String)

    @Input
    @Optional
    final Property<String> linkerExecutable = project.objects.property(String)

    @Input
    @Optional
    final Property<String> staticLibArchiverExecutable = project.objects.property(String)

    @OutputFile
    final RegularFileProperty pluginSourceFile = newOutputFile()

    @TaskAction
    private void generate() {
        if (!NativeToolChain.isAssignableFrom(toolChainType.get())) {
            throw new IllegalArgumentException("tool chain type must be a subclass of NativeToolChain")
        }

        pluginSourceFile.get().asFile.text = """package ${packageName.get()}

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.model.Mutate
import org.gradle.model.RuleSource
import org.gradle.nativeplatform.plugins.NativeComponentModelPlugin
import org.gradle.nativeplatform.toolchain.NativeToolChainRegistry
import org.gradle.nativeplatform.toolchain.Swiftc

import java.io.File

class ${FilenameUtils.removeExtension(pluginSourceFile.get().asFile.name)} implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.apply(NativeComponentModelPlugin)
    }
    
    static class Rule extends RuleSource {
        @Mutate
        void register${toolChainName.get().capitalize()}(NativeToolChainRegistry toolChains) {
            toolChains.create("${toolChainName.get()}", ${toolChainType.get().canonicalName}) {
                path "${toolChainLocation.get().absolutePath}/bin"
                eachPlatform {
                    if (new File("${toolChainLocation.get().absolutePath}").exists()) {
                        ${generateExecutableMutatorCode('cCompiler', cCompilerExecutable)}
                        ${generateExecutableMutatorCode('cppCompiler', cppCompilerExecutable)}
                        ${generateExecutableMutatorCode('linker', linkerExecutable)}
                        ${generateExecutableMutatorCode('staticLibArchiver', staticLibArchiverExecutable)}
                    } else if (Swiftc.isAssignableFrom(${toolChainType.get().canonicalName})) {
                        // Configuration of the executable isn't allowed for Swiftc tool chain, so we generate an exception now instead of configuring bad executable to cause the tool chain discovery to generate an error.
                        throw new UnsupportedOperationException("Provisioning of tool chain '${toolChainName.get()}' wasn't successful")
                    } else {
                        // Gcc and Clang can configure the tool executable so we differ the exception only if the tool chain is used
                        cCompiler.executable = "unprovisionable"
                        cppCompiler.executable = "unprovisionable"
                        linker.executable = "unprovisionable"
                        staticLibArchiver.executable = "unprovisionable"
                    }
                }
            }
        }
    }
}
"""
    }

    private String generateExecutableMutatorCode(String toolName, Provider<String> value) {
        if (value.isPresent()) {
            return """${toolName}.executable = "${value.get()}" """
        }
        return ""
    }
}
