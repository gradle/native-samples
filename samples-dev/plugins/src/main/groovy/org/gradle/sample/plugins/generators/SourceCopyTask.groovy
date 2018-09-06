package org.gradle.sample.plugins.generators

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.TaskAction
import org.gradle.samples.plugins.SampleGeneratorTask

class SourceCopyTask extends DefaultTask implements SampleGeneratorTask {
    final DirectoryProperty sampleDir = project.objects.directoryProperty()
    final DirectoryProperty templatesDir = project.objects.directoryProperty()
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
            templates.each { Template template ->
                builder.copyDir(template.templateName, projectDir) { TemplateDirectory dir ->
                    mapDir(template, dir)
                }
            }
            def testDir = builder.targetFile("${projectDir}/${swiftTestDirName}")
            if (testDir.directory) {
                def testNames = testDir.listFiles()
                        .findAll { it.name.endsWith(".swift") && it.name != "LinuxMain.swift" }
                        .collect {
                            def name = it.name.replace(".swift", "")
                            "testCase(${name}.allTests)"
                        }.sort()
                        .join(", ")
                new File(testDir, "LinuxMain.swift").text = """import XCTest

XCTMain([${testNames}])
"""
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

        String getSwiftTestDirName() {
            return "src/test/swift"
        }

        /**
         * Visits each source directory for the template, allowing this target to map the destination dir and apply filtering.
         */
        abstract void mapDir(Template template, TemplateDirectory directory)
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
            File file = targetFile(targetFileName)
            file.parentFile.mkdirs()
            file.text = content
            return file
        }

        File targetFile(String targetFileName) {
            def file = sampleDir.file(targetFileName).get().asFile
            file
        }

        void copyDir(String templateDirName, String targetDir, Closure dirAction) {
            doCopyDir(templateDirName, "", targetDir, dirAction)
        }

        void doCopyDir(String templateDirName, String srcName, String targetDir, Closure dirAction) {
            def srcFileName = "${templateDirName}/${srcName}"
            def srcDir = templatesDir.dir(srcFileName).get().asFile
            if (!srcDir.directory || srcDir.list().length == 0) {
                return
            }
            def dirDetails = new TemplateDirectory(srcName)
            dirAction.call(dirDetails)
            def destDir = sampleDir.dir("${targetDir}/${dirDetails.targetDirName}").get().asFile
            copy(srcDir, destDir, dirDetails.lineFilter, dirDetails.recursive)
            if (dirDetails.recursive) {
                return
            }
            srcDir.listFiles().each { f ->
                if (f.directory) {
                    doCopyDir(templateDirName, srcName ? "$srcName/$f.name" : f.name, targetDir, dirAction)
                }
            }
        }

        private void copy(File srcDir, File destDir, Closure lineFilter, boolean recursive) {
            if (recursive) {
                cleanDir(destDir)
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

        private void cleanDir(File destDir) {
            if (cleaned.add(destDir)) {
                destDir.listFiles().each { f ->
                    if (f.directory) {
                        cleanDir(f)
                    } else if (f.name != 'CMakeLists.txt') {
                        f.delete()
                    }
                }
                destDir.delete()
            }
        }
    }

    static class TemplateDirectory {
        final String sourceDirName
        String targetDirName
        Closure lineFilter
        boolean recursive

        TemplateDirectory(String sourceDirName) {
            this.sourceDirName = sourceDirName
            this.targetDirName = sourceDirName
        }

        void map(String srcDir, String destDir, Closure lineFilter) {
            if (sourceDirName == srcDir) {
                targetDirName = destDir
                this.lineFilter = lineFilter
                recursive = true
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
        void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/java", "src/main/java", null)
            directory.map("src/main/groovy", "src/main/groovy", null)
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
        void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/cpp", "src/main/cpp", null)
            directory.map("src/main/c", "src/main/c", null)
            directory.map("src/main/headers", "src/main/headers", null)
            directory.map("src/main/public", "src/main/headers", null)
            directory.map("src/main/swift", "src/main/swift", null)
            directory.map("src/test/swift", "src/test/swift", null)
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
        void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/cpp", "src/main/cpp", null)
            directory.map("src/main/c", "src/main/c", null)
            if (privateHeaderDir) {
                directory.map("src/main/headers", "src/main/headers", null)
            } else {
                directory.map("src/main/headers", "src/main/cpp", null)
            }
            directory.map("src/main/public", "src/main/public", addDllExportToPublicHeader(template))
            directory.map("src/main/swift", "src/main/swift", null)
            directory.map("src/test/swift", "src/test/swift", null)
        }
    }

    static class StaticLibTarget extends GradleTarget {
        StaticLibTarget(String projectDir) {
            super(projectDir)
        }

        @Override
        void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/cpp", "src/main/cpp", null)
            directory.map("src/main/c", "src/main/c", null)
            directory.map("src/main/headers", "src/main/headers", null)
            directory.map("src/main/public", "src/main/public", null)
            directory.map("src/main/swift", "src/main/swift", null)
            directory.map("src/test/swift", "src/test/swift", null)
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
        String getSwiftTestDirName() {
            return "Tests/${targetName}Tests"
        }

        @Override
        void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/cpp", "Sources/${targetName}", null)
            directory.map("src/main/c", "Sources/${targetName}", null)
            directory.map("src/main/headers", "Sources/${targetName}/include", null)
            directory.map("src/main/public", "Sources/${targetName}/include", addDllExportToPublicHeader(template))
            directory.map("src/main/swift", "Sources/${targetName}", null)
            directory.map("src/test/swift", "Tests/${targetName}Tests", null)
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
        void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/cpp", "src/${targetName}", null)
            directory.map("src/main/c", "src/${targetName}", null)
            directory.map("src/main/headers", "src/${targetName}/include", null)
            directory.map("src/main/public", "src/${targetName}/include", addDllExportToPublicHeader(template))
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
