package org.gradle.samples.plugins.generators;

import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Transformer;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.Cast;
import org.gradle.samples.plugins.SampleGeneratorTask;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SourceCopyTask extends DefaultTask implements SampleGeneratorTask {
    private final DirectoryProperty sampleDir = getProject().getObjects().directoryProperty();
    private final DirectoryProperty templatesDir = getProject().getObjects().directoryProperty();
    private final Map<String, TemplateTarget> projects = new LinkedHashMap<>();

    @TaskAction
    private void go() throws IOException {
        Set<File> cleaned = new HashSet();
        for (TemplateTarget p : projects.values()) {
            List<String> modules = p.getIncludedModules().stream().map(it -> "import " + it).collect(Collectors.toList());
            List<String> testModules = p.getIncludedModules().stream().map(it -> "@testable import " + it).collect(Collectors.toList());
            SourceBuilder sourceBuilder = new SourceBuilder(p.getModule(), modules, testModules, cleaned);
            p.visitDirs(sourceBuilder);
        }
    }

    public GradleTarget project(String projectDir) {
        return add(new GradleTarget(projectDir));
    }

    public AppTarget appProject(String projectDir) {
        return add(new AppTarget(projectDir));
    }

    public LibTarget libProject(String projectDir) {
        return add(new LibTarget(projectDir));
    }

    public StaticLibTarget staticLibProject(String projectDir) {
        return add(new StaticLibTarget(projectDir));
    }

    public SwiftPmTarget swiftPMProject(String projectDir, String targetName) {
        return add(new SwiftPmTarget(projectDir, targetName));
    }

    public CmakeTarget cmakeAppProject(String projectDir) {
        return add(new CmakeTarget(projectDir, projectDir, false));
    }

    public CmakeTarget cmakeProject(String projectDir) {
        return add(new CmakeTarget(projectDir, projectDir, true));
    }

    public CmakeTarget cmakeProject(String projectDir, String targetName) {
        return add(new CmakeTarget(projectDir, targetName, true));
    }

    private <T extends TemplateTarget> T add(T target) {
        if (projects.containsKey(target.getKey())) {
            return Cast.uncheckedCast(projects.get(target.getKey()));
        }

        projects.put(target.getKey(), target);
        return target;
    }

    public DirectoryProperty getSampleDir() {
        return sampleDir;
    }

    public DirectoryProperty getTemplatesDir() {
        return templatesDir;
    }

    public Map<String, TemplateTarget> getProjects() {
        return projects;
    }

    public static abstract class TemplateTarget {
        private final List<Template> templates = new ArrayList<Template>();
        private final String projectDir;
        private boolean rootDir;

        public TemplateTarget(String projectDir) {
            this.projectDir = projectDir;
        }

        public TemplateTarget fromTemplate(String templateName) {
            return fromTemplate(Template.of(templateName));
        }

        public TemplateTarget fromTemplate(Template template) {
            templates.add(template);
            return this;
        }

        public TemplateTarget buildRoot() {
            rootDir = true;
            return this;
        }

        public String getModule() {
            return templates.stream().filter(SwiftLibraryTemplate.class::isInstance).map(SwiftLibraryTemplate.class::cast).map(SwiftLibraryTemplate::getModule).findAny().orElse(null);
        }

        public List<String> getIncludedModules() {
            return templates.stream().filter(SwiftLibraryTemplate.class::isInstance).map(SwiftLibraryTemplate.class::cast).map(SwiftLibraryTemplate::getModule).collect(Collectors.toList());
        }

        public abstract String getKey();

        /**
         * Visits each src, dest directory pair for this project.
         */
        private void visitDirs(final SourceBuilder builder) throws IOException {
            if (rootDir) {
                String relPath = builder.relativePathTo(projectDir);
                String bashContent = FileUtils.readFileToString(builder.getTemplateFile("build-root/gradlew"), Charset.defaultCharset());
                bashContent = bashContent.replace("REL_PATH", relPath);
                File bashFile = builder.writeTargetFile(getProjectDir() + "/gradlew", bashContent);
                bashFile.setExecutable(true);
                String batContent = FileUtils.readFileToString(builder.getTemplateFile("build-root/gradlew.bat"), Charset.defaultCharset());
                batContent = batContent.replace("REL_PATH", relPath.replace("/", "\\"));
                builder.writeTargetFile(getProjectDir() + "/gradlew.bat", batContent);
            }

            templates.forEach(template -> {
                builder.copyDir(template.getTemplateName(), getProjectDir(), dir -> mapDir(template, dir));
            });
            File testDir = builder.targetFile(getProjectDir() + "/" + getSwiftTestDirName());
            if (testDir.isDirectory()) {
                final String testNames =
                                        Arrays.stream(testDir.listFiles()).filter(it -> it.getName().endsWith(".swift") && !it.getName().equals("LinuxMain.swift")).map(it -> {
                                            String name = it.getName().replace(".swift", "");
                                            return "testCase(" + name + ".allTests)";
                                        }).sorted().collect(Collectors.joining(", "));

                FileUtils.write(new File(testDir, "LinuxMain.swift"), "import XCTest\n\nXCTMain([" + testNames + "])\n", Charset.defaultCharset());
            }

        }

        public Transformer<String, String> addDllExportToPublicHeader(Template template) {
            if (template instanceof CppLibraryTemplate) {
                final String macroName = ((CppLibraryTemplate) template).getName().toUpperCase() + "_API";
                final String exportMacroName = ((CppLibraryTemplate) template).getName().toUpperCase() + "_MODULE_EXPORT";
                final String macroDef = "#define " + macroName;

                return line -> {
                    if (line.equals(macroDef)) {
                        return "#ifdef _WIN32\n#  ifdef " + exportMacroName + "\n#    define " + macroName + " __declspec(dllexport)\n#  else\n#    define " + macroName + " __declspec(dllimport)\n#  endif\n#else\n#  define " + macroName + "\n#endif";
                    }

                    return line;
                };
            }

            return null;
        }

        public String getSwiftTestDirName() {
            return "src/test/swift";
        }

        /**
         * Visits each source directory for the template, allowing this target to map the destination dir and apply filtering.
         */
        public abstract void mapDir(Template template, TemplateDirectory directory);

        public List<Template> getTemplates() {
            return templates;
        }

        public String getProjectDir() {
            return projectDir;
        }

        public boolean getRootDir() {
            return rootDir;
        }

        public boolean isRootDir() {
            return rootDir;
        }

        public void setRootDir(boolean rootDir) {
            this.rootDir = rootDir;
        }
    }

    public class SourceBuilder {
        private final Set<File> cleaned;
        private final String module;
        private final Collection<String> modules;
        private final Collection<String> testModules;

        public SourceBuilder(String module, Collection<String> modules, Collection<String> testModules, Set<File> cleaned) {
            this.module = module;
            this.cleaned = cleaned;
            this.modules = modules;
            this.testModules = testModules;
        }

        /**
         * Returns the relative path from the sample dir to the root of the samples source tree.
         */
        public String relativePathTo(String targetDirName) {
            return getSampleDir().dir(targetDirName).get().getAsFile().toPath().relativize(getProject().getProjectDir().getParentFile().toPath()).toString();
        }

        public File getTemplateFile(String templateFileName) {
            return getTemplatesDir().file(templateFileName).get().getAsFile();
        }

        public File writeTargetFile(String targetFileName, String content) throws IOException {
            File file = targetFile(targetFileName);
            file.getParentFile().mkdirs();
            FileUtils.write(file, content, Charset.defaultCharset());
            return file;
        }

        public File targetFile(String targetFileName) {
            File file = getSampleDir().file(targetFileName).get().getAsFile();
            return ((File) (file));
        }

        public void copyDir(String templateDirName, String targetDir, Action<TemplateDirectory> dirAction) {
            final File templateDir = getTemplatesDir().dir(templateDirName).get().getAsFile();
            if (!templateDir.exists()) {
                throw new IllegalArgumentException("Template directory " + String.valueOf(templateDir) + " does not exist");
            }

            doCopyDir(templateDirName, "", targetDir, dirAction);
        }

        public void doCopyDir(String templateDirName, String srcName, String targetDir, Action<TemplateDirectory> dirAction) {
            String srcFileName = templateDirName + "/" + srcName;
            File srcDir = getTemplatesDir().dir(srcFileName).get().getAsFile();
            if (!srcDir.isDirectory() || srcDir.list().length == 0) {
                return;

            }

            final TemplateDirectory dirDetails = new TemplateDirectory(srcName);
            dirAction.execute(dirDetails);
            File destDir = getSampleDir().dir(targetDir + "/" + dirDetails.getTargetDirName()).get().getAsFile();
            copy(srcDir, destDir, dirDetails.getLineFilter(), dirDetails.getRecursive());
            if (dirDetails.getRecursive()) {
                return;

            }

            Arrays.stream(srcDir.listFiles()).forEach(f -> {
                if (f.isDirectory() && !f.getName().equals(".gradle")) {
                    doCopyDir(templateDirName, StringGroovyMethods.asBoolean(srcName) ? srcName + "/" + f.getName() : f.getName(), targetDir, dirAction);
                }
            });
        }

        private void copy(File srcDir, File destDir, Transformer<String, String> lineFilter, boolean recursive) {
            if (recursive) {
                cleanDir(destDir);
            }

            getProject().copy(copySpec -> {
                copySpec.from(srcDir);
                copySpec.into(destDir);
                copySpec.setIncludeEmptyDirs(false);
                if (!recursive) {
                    copySpec.include("*");
                }

                if (lineFilter != null) {
                    copySpec.filter(lineFilter);
                }

                copySpec.filter(line -> {
                    if (getModules().contains(line)) {
                        return null;
                    }

                    if (getTestModules().contains(line)) {
                        return "@testable import " + getModule();
                    }

                    return line;
                });
            });
        }

        private void cleanDir(File destDir) {
            if (cleaned.add(destDir) && destDir.exists()) {
                Arrays.stream(destDir.listFiles()).forEach(f -> {
                    if (f.isDirectory()) {
                        cleanDir(f);
                    } else if (!f.getName().equals("CMakeLists.txt")) {
                        f.delete();
                    }
                });
                destDir.delete();
            }

        }

        public final Set<File> getCleaned() {
            return cleaned;
        }

        public final String getModule() {
            return module;
        }

        public final Collection<String> getModules() {
            return modules;
        }

        public final Collection<String> getTestModules() {
            return testModules;
        }
    }

    public static class TemplateDirectory {
        private final String sourceDirName;
        private String targetDirName;
        private Transformer<String, String> lineFilter;
        private boolean recursive;

        private TemplateDirectory(String sourceDirName) {
            this.sourceDirName = sourceDirName;
            this.targetDirName = sourceDirName;
        }

        public void map(String srcDir, String destDir, Transformer<String, String> lineFilter) {
            if (sourceDirName.equals(srcDir)) {
                this.targetDirName = destDir;
                this.lineFilter = lineFilter;
                this.recursive = true;
            }

        }

        public final String getSourceDirName() {
            return sourceDirName;
        }

        public String getTargetDirName() {
            return targetDirName;
        }

        public void setTargetDirName(String targetDirName) {
            this.targetDirName = targetDirName;
        }

        public Transformer<String, String> getLineFilter() {
            return lineFilter;
        }

        public void setLineFilter(Transformer<String, String> lineFilter) {
            this.lineFilter = lineFilter;
        }

        public boolean getRecursive() {
            return recursive;
        }

        public boolean isRecursive() {
            return recursive;
        }

        public void setRecursive(boolean recursive) {
            this.recursive = recursive;
        }
    }

    public static class GradleTarget extends TemplateTarget {
        private GradleTarget(String projectDir) {
            super(projectDir);
        }

        @Override
        public String getKey() {
            return getProjectDir();
        }

        @Override
        public void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/java", "src/main/java", null);
            directory.map("src/main/groovy", "src/main/groovy", null);
        }

    }

    public static class AppTarget extends GradleTarget {
        private AppTarget(String projectDir) {
            super(projectDir);
        }

        @Override
        public String getModule() {
            return "App";
        }

        @Override
        public void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/cpp", "src/main/cpp", null);
            directory.map("src/main/c", "src/main/c", null);
            directory.map("src/main/headers", "src/main/headers", null);
            directory.map("src/main/public", "src/main/headers", null);
            directory.map("src/main/swift", "src/main/swift", null);
            directory.map("src/test/swift", "src/test/swift", null);
        }

    }

    public static class LibTarget extends GradleTarget {
        private LibTarget(String projectDir) {
            super(projectDir);
        }

        public LibTarget noPrivateHeaderDir() {
            privateHeaderDir = false;
            return this;
        }

        @Override
        public void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/cpp", "src/main/cpp", null);
            directory.map("src/main/c", "src/main/c", null);
            if (privateHeaderDir) {
                directory.map("src/main/headers", "src/main/headers", null);
            } else {
                directory.map("src/main/headers", "src/main/cpp", null);
            }

            directory.map("src/main/public", "src/main/public", addDllExportToPublicHeader(template));
            directory.map("src/main/swift", "src/main/swift", null);
            directory.map("src/test/swift", "src/test/swift", null);
        }

        public boolean getPrivateHeaderDir() {
            return privateHeaderDir;
        }

        public boolean isPrivateHeaderDir() {
            return privateHeaderDir;
        }

        public void setPrivateHeaderDir(boolean privateHeaderDir) {
            this.privateHeaderDir = privateHeaderDir;
        }

        private boolean privateHeaderDir = true;
    }

    public static class StaticLibTarget extends GradleTarget {
        private StaticLibTarget(String projectDir) {
            super(projectDir);
        }

        @Override
        public void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/cpp", "src/main/cpp", null);
            directory.map("src/main/c", "src/main/c", null);
            directory.map("src/main/headers", "src/main/headers", null);
            directory.map("src/main/public", "src/main/public", null);
            directory.map("src/main/swift", "src/main/swift", null);
            directory.map("src/test/swift", "src/test/swift", null);
        }

    }

    public static class SwiftPmTarget extends TemplateTarget {
        private final String targetName;

        private SwiftPmTarget(String projectDir, String targetName) {
            super(projectDir);
            this.targetName = targetName;
        }

        @Override
        public String getModule() {
            return targetName;
        }

        @Override
        public String getKey() {
            return getProjectDir() + ":" + getTargetName();
        }

        @Override
        public String getSwiftTestDirName() {
            return "Tests/" + getTargetName() + "Tests";
        }

        @Override
        public void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/cpp", "Sources/" + getTargetName(), null);
            directory.map("src/main/c", "Sources/" + getTargetName(), null);
            directory.map("src/main/headers", "Sources/" + getTargetName() + "/include", null);
            directory.map("src/main/public", "Sources/" + getTargetName() + "/include", addDllExportToPublicHeader(template));
            directory.map("src/main/swift", "Sources/" + getTargetName(), null);
            directory.map("src/test/swift", "Tests/" + getTargetName() + "Tests", null);
        }

        public final String getTargetName() {
            return targetName;
        }
    }

    public static class CmakeTarget extends TemplateTarget {
        private final String targetName;
        private final boolean isLibrary;

        private CmakeTarget(String projectDir, String targetName, boolean isLibrary) {
            super(projectDir);
            this.isLibrary = isLibrary;
            this.targetName = targetName;
        }

        @Override
        public String getModule() {
            return targetName;
        }

        @Override
        public String getKey() {
            return getProjectDir() + ":" + getTargetName();
        }

        @Override
        public void mapDir(Template template, TemplateDirectory directory) {
            directory.map("src/main/cpp", "src/" + getTargetName(), null);
            directory.map("src/main/c", "src/" + getTargetName(), null);
            directory.map("src/main/headers", "src/" + getTargetName() + "/include", null);
            directory.map("src/main/public", "src/" + getTargetName() + "/include", isLibrary ? addDllExportToPublicHeader(template) : null);
        }

        public final String getTargetName() {
            return targetName;
        }
    }
}

