# Swift and C++ Sample Projects

This repository holds several sample Gradle builds which demonstrate how to
use Gradle to build Swift/C++ libraries and executables including dependencies on
native libraries written in other native languages like C, C++, and Objective-C.

Each sample build is listed below with a bit of information related to the
features of Gradle that are demonstrated in that build. Each samples are functionally
the same for both Swift and C++ language.

### Xcode support

All of the samples have Xcode support, added by applying the `xcode` plugin. To open a sample build in Xcode:

```
> cd <sample-dir>
> ./gradlew xcode
> open <root-project>.xcworkspace
```

### Debug and release variants

The Swift/C++ plugins add a 'debug' and 'release' variant for each library or application. By default, the `assemble` will task will build the debug variant only. 

At this stage, there are no convenience tasks to build the release variant, or all variants, of a library or application.
To build the release variant of a library, use the `linkRelease` task. To build the release variant of an application, use the `installRelease` task.

## Simple Library (simple-library)

This build just shows that Swift or C++ libraries can be built with Gradle. There
are no dependencies, just the library itself. To run it:

```
> cd simple-module
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
2 actionable tasks: 2 executed

> find build/lib/main/debug  # C++ project will differ a bit
build/lib/main/debug/libhello.dylib
```

## Executable (executable)

This build shows how a Swift/C++ executable can be built with Gradle.

```
> cd executable
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
2 actionable tasks: 2 executed

> ./build/install/main/debug/app
Hello, World!
```

## Executable Multi-Project with Transitive Dependencies (transitive-dependencies)

This builds shows how an executable and several Swift/C++ libraries can be built with Gradle and linked together. The
dependencies are added transitively from the dependencies between modules.
To run it:

```
> cd transitive-dependencies
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
7 actionable tasks: 7 executed

> ./app/build/install/main/debug/app
Hello, World!
```

## Executable Composite Build with Transitive Dependencies (composite-build)

This build shows that several otherwise independent Swift/C++ libraries can be built together with Gradle. The
dependencies are added transitively from the dependencies between modules
and the build take part in a composite build. To run it:

```
> cd composite-build
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/install/main/debug/app
Hello, World!
Hello, World!
```

## Prebuilt binaries (prebuilt-binaries)

This build show how to use pre-built binaries that are already available on the local machine. Currently, Gradle does not offer a convenient way to do this but it is possible to configure Gradle to use these binaries.

To use the sample, first create the binaries using the `simple-library` build:

```
> cd simple-library
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

```

Next, run the sample that uses these binaries:

```
> cd ../prebuilt-binaries
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/install/main/debug/App
The sum of 40 and 2 is 42!
```

## Swift package manager conventions (swift-package-manager)

This build shows how to configure Gradle to use the typical layout for a Swift Package Manager package.
It contains an executable and a single library. The source files for the executable and libraries are all under a single `Sources` directory. 

This sample also includes a Swift Package Manager build file, so the same source can be built using Swift Package Manager

```
> cd swift-package-manager
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/app/install/main/debug/app
Hello, World!
```
