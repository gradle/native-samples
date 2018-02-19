package org.gradle.sample.plugins.generators

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.TaskAction

class SourceCopyTask extends DefaultTask {
    final DirectoryProperty sampleDir = project.layout.directoryProperty()
    final DirectoryProperty templatesDir = project.layout.directoryProperty()
    final Map<String, TemplateTarget> projects = [:]

    @TaskAction
    def go() {
        def cleaned = new HashSet()
        projects.values().each { p ->
            def modules = p.includedModules.collect { "import $it" as String }
            def testModules = p.includedModules.collect { "@testable import $it" as String }
            def sourceBuilder = new SourceBuilder(p.module, modules, testModules, cleaned)
            p.visitDirs(sourceBuilder)
        }
    }

    GradleTarget project(String projectDir) {
        return add(new GradleTarget(projectDir))
    }

    AppTarget appProject(String projectDir) {
        return add(new AppTarget(projectDir))
    }

    LibTarget libProject(String projectDir) {
        return add(new LibTarget(projectDir))
    }

    StaticLibTarget staticLibProject(String projectDir) {
        return add(new StaticLibTarget(projectDir))
    }

    SwiftPmTarget swiftPMProject(String projectDir, String targetName) {
        return add(new SwiftPmTarget(projectDir, targetName))
    }

    CmakeTarget cmakeProject(String projectDir) {
        return add(new CmakeTarget(projectDir, projectDir))
    }

    CmakeTarget cmakeProject(String projectDir, String targetName) {
        return add(new CmakeTarget(projectDir, targetName))
    }

    private def add(TemplateTarget target) {
        if (projects.containsKey(target.key)) {
            return projects.get(target.key)
        }
        projects.put(target.key, target)
        return target
    }

    static abstract class TemplateTarget {
        final List<Template> templates = []
        final String projectDir
        boolean rootDir

        TemplateTarget(String projectDir) {
            this.projectDir = projectDir
        }

        TemplateTarget fromTemplate(String templateName) {
            return fromTemplate(Template.of(templateName))
        }

        TemplateTarget fromTemplate(Template template) {
            templates.add(template)
            return this
        }

        TemplateTarget buildRoot() {
            rootDir = true
            return this
        }

        String getModule() {
            def libs = templates.findAll { it instanceof SwiftLibraryTemplate }
            return libs.empty ? null : libs.first().module
        }

        List<String> getIncludedModules() {
            def libs = templates.findAll { it instanceof SwiftLibraryTemplate }
            return libs.collect { it.module }
        }

        abstract String getKey()

        /**
         * Visits each src, dest directory pair for this project.
         * @param cl
         */
        void visitDirs(SourceBuilder builder) {
            if (rootDir) {
                def relPath = builder.relativePathTo(projectDir)
                def bashContent = builder.getTemplateFile("build-root/gradlew").text
                bashContent = bashContent.replace("REL_PATH", relPath)
                def bashFile = builder.writeTargetFile("${projectDir}/gradlew", bashContent)
                bashFile.setExecutable(true)
                def batContent = builder.getTemplateFile("build-root/gradlew.bat").text
                batContent = batContent.replace("REL_PATH", relPath.replace("/", "\\"))
                builder.writeTargetFile("${projectDir}/gradlew.bat", batContent)
            }
            templates.each { template ->
                if (rootDir) {
                    builder.copyDir("${template.templateName}/buildSrc", "${projectDir}/buildSrc")
                }
                builder.copyDir(template.templateName, projectDir)
                visitDirMappings(template) { src, dest, lineFilter ->
                    builder.copyDirTree("${template.templateName}/${src}", "${projectDir}/${dest}", lineFilter)
                }
            }
        }

        Closure addDllExportToPublicHeader(Template template) {
            if (template instanceof CppLibraryTemplate) {
                def macroName = template.name.toUpperCase() + "_API"
                def exportMacroName = template.name.toUpperCase() + "_MODULE_EXPORT"
                def macroDef = "#define ${macroName}"

                return { line ->
                    if (line == macroDef) {
                        return """
#ifdef _WIN32
#  ifdef ${exportMacroName}
#    define ${macroName} __declspec(dllexport)
#  else
#    define ${macroName} __declspec(dllimport)
#  endif
#else
#  define ${macroName}
#endif                        
""".trim()
                    }
                    return line
                }
            }
            return null
        }

        /**
         * Visits each candidate src, dest directory pair for this project and the given template.
         */
        abstract void visitDirMappings(Template template, Closure cl)
    }

    class SourceBuilder {
        final Set<File> cleaned
        final String module
        final Collection<String> modules
        final Collection<String> testModules

        SourceBuilder(String module, Collection<String> modules, Collection<String> testModules, Set<File> cleaned) {
            this.module = module
            this.cleaned = cleaned
            this.modules = modules
            this.testModules = testModules
        }

        /**
         * Returns the relative path from the sample dir to the root of the samples source tree.
         */
        String relativePathTo(String targetDirName) {
            return sampleDir.dir(targetDirName).get().asFile.toPath().relativize(project.projectDir.parentFile.toPath()).toString()
        }

        File getTemplateFile(String templateFileName) {
            return templatesDir.file(templateFileName).get().asFile
        }

        File writeTargetFile(String targetFileName, String content) {
            def file = sampleDir.file(targetFileName).get().asFile
            file.parentFile.mkdirs()
            file.text = content
            return file
        }

        void copyDir(String templateDirName, String targetDirName) {
            copy(templateDirName, targetDirName, null, false)
        }

        void copyDirTree(String templateDirName, String targetDirName, Closure lineFilter) {
            copy(templateDirName, targetDirName, lineFilter, true)
        }

        private void copy(String templateDirName, String targetDirName, Closure lineFilter, boolean recursive) {
            def srcDir = templatesDir.dir(templateDirName).get().asFile
            if (!srcDir.directory || srcDir.list().length == 0) {
                return
            }
            def destDir = sampleDir.dir(targetDirName).get().asFile
            if (recursive && cleaned.add(destDir)) {
                // TODO - generate the test main.swift
                // TODO - generate the CMake build
                project.delete project.fileTree(destDir, {
                    exclude '**/main.swift'
                    exclude 'CMakeLists.txt'
                })
            }
            project.copy {
                from srcDir
                into destDir
                includeEmptyDirs = false
                if (!recursive) {
                    include '*'
                }
                if (lineFilter) {
                    filter(lineFilter)
                }
                filter { line ->
                    if (modules.contains(line)) {
                        return null
                    }
                    if (testModules.contains(line)) {
                        return "@testable import " + module
                    }
                    line
                }
            }
        }
    }

    static class GradleTarget extends TemplateTarget {

        GradleTarget(String projectDir) {
            super(projectDir)
        }

        @Override
        String getKey() {
            return projectDir
        }

        @Override
        void visitDirMappings(Template template, Closure cl) {
            cl.call("src/main/java", "src/main/java", null)
            cl.call("src/main/groovy", "src/main/groovy", null)
        }
    }

    static class AppTarget extends GradleTarget {
        AppTarget(String projectDir) {
            super(projectDir)
        }

        @Override
        String getModule() {
            return "App"
        }

        @Override
        void visitDirMappings(Template template, Closure cl) {
            cl.call("src/main/cpp", "src/main/cpp", null)
            cl.call("src/main/c", "src/main/c", null)
            cl.call("src/main/headers", "src/main/headers", null)
            cl.call("src/main/public", "src/main/headers", null)
            cl.call("src/main/swift", "src/main/swift", null)
            cl.call("src/test/swift", "src/test/swift", null)
        }
    }

    static class LibTarget extends GradleTarget {
        boolean privateHeaderDir = true

        LibTarget(String projectDir) {
            super(projectDir)
        }

        LibTarget noPrivateHeaderDir() {
            privateHeaderDir = false
            return this
        }

        @Override
        void visitDirMappings(Template template, Closure cl) {
            cl.call("src/main/cpp", "src/main/cpp", null)
            cl.call("src/main/c", "src/main/c", null)
            if (privateHeaderDir) {
                cl.call("src/main/headers", "src/main/headers", null)
            } else {
                cl.call("src/main/headers", "src/main/cpp", null)
            }
            cl.call("src/main/public", "src/main/public", addDllExportToPublicHeader(template))
            cl.call("src/main/swift", "src/main/swift", null)
            cl.call("src/test/swift", "src/test/swift", null)
        }
    }

    static class StaticLibTarget extends GradleTarget {
        StaticLibTarget(String projectDir) {
            super(projectDir)
        }

        @Override
        void visitDirMappings(Template template, Closure cl) {
            cl.call("src/main/cpp", "src/main/cpp", null)
            cl.call("src/main/c", "src/main/c", null)
            cl.call("src/main/headers", "src/main/headers", null)
            cl.call("src/main/public", "src/main/public", null)
            cl.call("src/main/swift", "src/main/swift", null)
            cl.call("src/test/swift", "src/test/swift", null)
        }
    }

    static class SwiftPmTarget extends TemplateTarget {
        final String targetName

        SwiftPmTarget(String projectDir, String targetName) {
            super(projectDir)
            this.targetName = targetName
        }

        @Override
        String getModule() {
            return targetName
        }

        @Override
        String getKey() {
            return "$projectDir:$targetName"
        }

        @Override
        void visitDirMappings(Template template, Closure cl) {
            cl.call("src/main/cpp", "Sources/${targetName}", null)
            cl.call("src/main/c", "Sources/${targetName}", null)
            cl.call("src/main/headers", "Sources/${targetName}/include", null)
            cl.call("src/main/public", "Sources/${targetName}/include", addDllExportToPublicHeader(template))
            cl.call("src/main/swift", "Sources/${targetName}", null)
            cl.call("src/test/swift", "Tests/${targetName}Tests", null)
        }
    }

    static class CmakeTarget extends TemplateTarget {
        final String targetName

        CmakeTarget(String projectDir, String targetName) {
            super(projectDir)
            this.targetName = targetName
        }

        @Override
        String getModule() {
            return targetName
        }

        @Override
        String getKey() {
            return "$projectDir:$targetName"
        }

        @Override
        void visitDirMappings(Template template, Closure cl) {
            cl.call("src/main/cpp", "src/${targetName}", null)
            cl.call("src/main/c", "src/${targetName}", null)
            cl.call("src/main/headers", "src/${targetName}/include", null)
            cl.call("src/main/public", "src/${targetName}/include", addDllExportToPublicHeader(template))
        }
    }
}

class Template {
    final String templateName

    Template(String templateName) {
        this.templateName = templateName
    }

    static of(String templateName) {
        return new Template(templateName)
    }
}

class SwiftLibraryTemplate extends Template {
    final String module

    SwiftLibraryTemplate(String templateName, String module) {
        super(templateName)
        this.module = module
    }

    static of(String templateName, String module) {
        return new SwiftLibraryTemplate(templateName, module)
    }
}

class CppLibraryTemplate extends Template {
    final String name

    CppLibraryTemplate(String templateName, String name) {
        super(templateName)
        this.name = name
    }

    static of(String templateName, String name) {
        return new CppLibraryTemplate(templateName, name)
    }
}
