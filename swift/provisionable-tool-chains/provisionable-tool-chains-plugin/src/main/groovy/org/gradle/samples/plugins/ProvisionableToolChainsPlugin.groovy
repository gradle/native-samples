package org.gradle.samples.plugins

import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.samples.ProvisionableToolChain
import org.gradle.samples.ProvisionableToolChainExtension
import org.gradle.samples.tasks.DownloadToolChainTask
import org.gradle.samples.tasks.ExtractToolChainTask
import org.gradle.samples.tasks.GenerateProvisionableToolChainMetadata
import org.gradle.samples.tasks.GenerateToolChainConfigurationPlugin

/**
 * This plugin allows to provision Gcc, Clang and Swiftc tool chain in Gradle project. Here are the various piece to allow the provisioning to work properly:
 *
 * 1) Create an composite project that will act as the tool chain provisioning configuration, e.g. `tool-chain-configuration`:
 *   $ mkdir tool-chain-configuration
 *   $ echo "rootProject.name = 'tool-chain-configuration'" > tool-chain-configuration/settings.gradle
 *   $ echo "includeBuild 'tool-chain-configuration'" >> settings.gradle
 *
 * 2) Configure the your tool chain to provision:
 *      buildscript {
 *          dependencies {
 *              classpath "org.gradle.samples:provisionable-tool-chain:latest.integration"
 *          }
 *          repositories {
 *              mavenCentral()
 *          }
 *      }
 *
 *      apply plugin: 'org.gradle.samples.provisionable-tool-chains'
 *
 *      group = 'my.company.toolchains'
 *      version = '1.0-SNAPSHOT'
 *
 *      provisionableToolChains {
 *          repositoryDirectory = new File(projectDir.parentFile, '.toolChainRepository')
 *          toolChains {
 *              gccArmNoneEabi {
 *                  type = Gcc
 *                  url = new URL("...")
 *                  cCompilerExecutable.set("arm-none-eabi-gcc")
 *                  cppCompilerExecutable = "arm-none-eabi-g++"
 *                  linkerExecutable = "arm-none-eabi-g++"
 *                  staticLibArchiverExecutable = "arm-none-eabi-ar"
 *              }
 *              hostClang {
 *                  type = Clang
 *                  url = new URL("...")
 *                  staticLibArchiverExecutable = "llvm-ar"
 *              }
 *          }
 *      }
 *
 *  3) Use tool chain plugins:
 *      buildscript {
 *          dependencies {
 *              classpath "my.company.toolchains:tool-chain-configuration:latest.integration"
 *          }
 *      }
 *      apply plugin: 'my.company.toolchains.gccArmNoneEabi'
 */
class ProvisionableToolChainsPlugin implements Plugin<Project> {
    private static final String PROVISIONABLE_TOOL_CHAINS_METADATA_FILENAME = "provisionable-tool-chains-metadata.json"

    @Override
    void apply(Project project) {
        ProvisionableToolChainExtension provisionableToolChains = project.extensions.create("provisionableToolChains", ProvisionableToolChainExtension, project)

        // If provisionable metadata file is present, deserialize it
        InputStream ins = this.getClass().getResourceAsStream("/META-INF/${PROVISIONABLE_TOOL_CHAINS_METADATA_FILENAME}")
        if (ins != null) {
            def parsedMetadata = new JsonSlurper().parse(ins)
            provisionableToolChains.repositoryDirectory = new File(parsedMetadata.repositoryDirectory)
            for (def toolChainMetaData : parsedMetadata.toolChains) {
                provisionableToolChains.toolChains.create(toolChainMetaData.name) {
                    type = Class.forName(toolChainMetaData.type)
                    location = new File(toolChainMetaData.location)
                    url = new URL(toolChainMetaData.url)
                    md5 = toolChainMetaData.get('md5', null)
                    cCompilerExecutable.set(toolChainMetaData.get('cCompilerExecutable', null))  // TODO: Gradle bug
                    cppCompilerExecutable = toolChainMetaData.get('cppCompilerExecutable', null)
                    linkerExecutable = toolChainMetaData.get('linkerExecutable', null)
                    staticLibArchiverExecutable = toolChainMetaData.get('staticLibArchiverExecutable', null)
                }
            }

        // Else, wire everything together to generate it (and the plugin jar)
        } else {
            project.plugins.apply("groovy")
            project.plugins.apply("java-gradle-plugin")

            provisionableToolChains.toolChains.all { ProvisionableToolChain toolChain ->
                location = provisionableToolChains.repositoryDirectory.dir(toolChain.name)
            }

            // Create metadata file to cross included build boundaries
            GenerateProvisionableToolChainMetadata toolChainMetadata = project.tasks.create("generateProvisionableToolChainMetadata", GenerateProvisionableToolChainMetadata) {
                repositoryDirectory = provisionableToolChains.repositoryDirectory
                outputFile = project.layout.buildDirectory.file(PROVISIONABLE_TOOL_CHAINS_METADATA_FILENAME)
            }
            project.tasks.jar.metaInf.from toolChainMetadata.outputFile

            // Wire tasks together
            provisionableToolChains.toolChains.all { ProvisionableToolChain toolChain ->
                DownloadToolChainTask downloadTask = project.tasks.create("download${toolChain.name.capitalize()}ToolChain", DownloadToolChainTask) {
                    it.fromUrl = toolChain.url
                    it.md5 = toolChain.md5
                    it.toolChainRepositoryDirectory = provisionableToolChains.repositoryDirectory
                }

                ExtractToolChainTask extractTask = project.tasks.create("extract${toolChain.name.capitalize()}ToolChain", ExtractToolChainTask) {
                    it.toolChainArchive = downloadTask.toolChainArchive
                    it.toolChainLocation = toolChain.location
                }

                Task provisionTask = project.tasks.create("provision${toolChain.name.capitalize()}ToolChain") { Task it ->
                    it.dependsOn extractTask
                    it.group = 'Tool Chain Provisioning'
                    it.description = "Provision '${toolChain.name}' tool chain"
                }

                // Generate tool chain plugin code
                GenerateToolChainConfigurationPlugin generateToolChainConfigurationPluginTask = project.tasks.create("generate${toolChain.name.capitalize()}ToolChainConfigurationPlugin", GenerateToolChainConfigurationPlugin) {
                    packageName = project.provider { "${project.group}.plugins".toString() }
                    toolChainType = toolChain.type
                    toolChainName = toolChain.name
                    toolChainLocation = toolChain.location.asFile
                    cCompilerExecutable.set(toolChain.cCompilerExecutable)  // TODO: Bug in gradle
                    cppCompilerExecutable = toolChain.cppCompilerExecutable
                    linkerExecutable = toolChain.linkerExecutable
                    staticLibArchiverExecutable = toolChain.staticLibArchiverExecutable
                    pluginSourceFile = project.layout.buildDirectory.file("${toolChain.name.capitalize()}Plugin.groovy")
                }
                project.tasks.compileGroovy.source generateToolChainConfigurationPluginTask.pluginSourceFile
                project.gradlePlugin.plugins.create(toolChain.name) {
                    id = "${project.group}.${toolChain.name}"
                    implementationClass = "${project.group}.plugins.${toolChain.name.capitalize()}Plugin"
                }

                // Add tool chain to metadata
                toolChainMetadata.toolChains.add(toolChain)

                project.tasks.jar.dependsOn provisionTask
            }
        }
    }
}
