
# Swift and C++ Sample Projects

This repository holds several sample Gradle builds which demonstrate how to
use Gradle to build Swift/C++ libraries and executables including dependencies on
native libraries written in other native languages like C, C++, and Objective-C.

Each sample build is listed below with a bit of information related to the
features of Gradle that are demonstrated in that build. Each samples are functionally
the same for both Swift and C++ languages.

### Xcode support

All of the samples have Xcode support, added by applying the `xcode` plugin. To open a sample build in Xcode:

```
> cd <sample-dir>
> ./gradlew xcode
> open <root-project>.xcworkspace
```

### XCTest support

Both Swift sample `simple-library` and `executable` demonstrate XCTest support in Gradle. As a user, you can either
interact with the test code as you would usually do through Xcode or you can run the test directly from the command line:

```
> cd <sample-dir>
> ./gradlew test
```

### Debug and release variants

The Swift/C++ plugins add a 'debug' and 'release' variant for each library or application. By default, the `assemble` will task will build the debug variant only.

At this stage, there are no convenience tasks to build the release variant, or all variants, of a library or application.
To build the release variant of a library, use the `linkRelease` task. To build the release variant of an application, use the `installRelease` task.

### Publishing binaries to a Maven repository

Some of the C++ samples are configured to publish binaries to a local Maven repository. For these samples you can run:

```
> cd <sample-dir>
> ./gradle publish
> tree ../repo/
```

This will build and publish the debug and release binaries for each library and executables. The binaries are published to a repository in the `cpp/repo` directory.

## Simple Library (simple-library)

This build just shows that Swift or C++ libraries can be built with Gradle. There
are no dependencies, just the library itself. To run it:

```
> cd simple-module
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
2 actionable tasks: 2 executed

> find build/lib/main/debug  # C++ project will differ a bit
build/lib/main/debug/libmath.dylib
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

The build script also demonstrates how to configure convenience tasks like `assembleDebuggable`, which will assemble all "debuggable" binaries.

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
The sum of 40 and 2 is 42!
```

## Binary dependencies (binary-dependencies)

This build shows how to publish C++ libraries to a Maven repository and use them from another build. This is currently only supported for C++.

To use the sample, first publish a library to the repository using the `simple-library` build:

```
> cd simple-library
> ./gradlew publish

BUILD SUCCESSFUL in 0s
```

You can find the repository in the `cpp/repo` directory.

Next, run the sample that uses the library from this repository.

```
> cd ../binary-dependencies
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/install/main/debug/app
The sum of 40 and 2 is 42!
```

## Prebuilt binaries (prebuilt-binaries)

This build shows how to use pre-built binaries that are already available on the local machine. Currently, Gradle does not offer a convenient way to do this but it is possible to configure Gradle to use these binaries.

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

> ./build/install/main/debug/app
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

## Source generation (Swift and C++) 

These builds demonstrate using a task to generate source code before building a Swift or C++ executable.

```
> cd source-generation
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
```

Generated sources will be under `build/generated`.

## Source Dependencies (Swift and C++)

These builds demonstrate using External Source Dependencies to build Swift and C++ executables that require two external libraries.

### Swift
```
> cd swift/source-dependencies
> ./gradlew assemble

BUILD SUCCESSFUL in 3s

> ./build/exe/main/debug/App
Hello, World!
```

### C++
```
> cd cpp/source-dependencies
> ./gradlew assemble

BUILD SUCCESSFUL in 3s

> ./build/exe/main/debug/app
Hello, World!
```
