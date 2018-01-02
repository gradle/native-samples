
# Swift and C++ Sample Projects

This repository holds several sample Gradle builds which demonstrate how to
use Gradle to build Swift/C++ libraries and applications including dependencies on
native libraries written in other native languages like C, C++, and Objective-C.

Each sample build is listed below with a bit of information related to the
features of Gradle that are demonstrated in that build. Each samples are functionally
the same for both Swift and C++ languages.

Each C++ sample works on macOS, Linux and Windows with GCC, Clang and Visual C++.

Each Swift sample works on macOS and Linux, with Swift 3 and later.

### Contribution

If you want to contribute an improvement to the samples, please refer to the [`samples-dev` subproject](samples-dev/README.md).

### Xcode support

All of the samples have Xcode support, added by applying the `xcode` plugin. To open a sample build in Xcode:

```
> cd <sample-dir>
> ./gradlew xcode
> open <root-project>.xcworkspace
```

### XCTest support

All Swift samples demonstrate XCTest support in Gradle. As a user, you can either interact with the test code as you would usually do through Xcode or you can run the test directly from the command line:

```
> cd <sample-dir>
> ./gradlew test
```

### Google test support

The C++ sample `simple-library` demonstrates some basic Google test support in Gradle. This is currently not as refined as the XCTest support. 

To run the tests from the command line:

```
> cd <sample-dir>
> ./gradlew test
```

### Debug and release variants

The Swift/C++ plugins add a 'debug' and 'release' variant for each library or application. By default, the `assemble` will task will build the debug variant only.

You can also use the `assembleDebug` and `assembleRelease` tasks to build a specific variant, or both variants.

At this stage, there are no convenience tasks to build all variants of a library or application.

### Publishing binaries to a Maven repository

Some of the C++ samples are configured to publish binaries to a local Maven repository. For these samples you can run:

```
> cd <sample-dir>
> ./gradle publish
> tree ../repo/
```

This will build and publish the debug and release binaries. The binaries are published to a repository in the `cpp/repo` directory.

## Simple Library (simple-library)

This build shows how a Swift or C++ library can be built with Gradle. There are no dependencies, just the library itself.

To build the library:

```
> cd simple-library
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
2 actionable tasks: 2 executed

> find build/lib/main/debug  # C++ project will differ a bit
build/lib/main/debug/libmath.dylib
```

To run the unit tests for the library:

```
> cd simple-library
> ./gradlew test
```

## Application (application)

This build shows how a Swift/C++ application can be built with Gradle. The application has no dependencies.

To build and run the application:

```
> cd application
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
2 actionable tasks: 2 executed

> ./build/install/main/debug/app
Hello, World!
```

## Application with library dependencies in a multi-project build (transitive-dependencies)

This builds shows how an application and several Swift/C++ libraries can be built with Gradle and linked together. The
dependencies are added transitively from the dependencies between modules.

To build and run the application:

```
> cd transitive-dependencies
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
7 actionable tasks: 7 executed

> ./app/build/install/main/debug/app
Hello, World!
```

The build script also demonstrates how to configure convenience tasks like `assembleDebuggable`, which will assemble all "debuggable" binaries.

## Application with library dependencies in a composite build (composite-build)

This build shows that several otherwise independent Swift/C++ libraries can be built together with Gradle. The
dependencies are added transitively from the dependencies between modules
and the build take part in a composite build.

To build and run the application:

```
> cd composite-build
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/install/main/debug/app
Hello, World!
The sum of 40 and 2 is 42!
```

## Application with prebuilt library dependencies in a Maven repository (binary-dependencies)

This build shows how to publish C++ libraries to a Maven repository and use them from another build. This is currently only supported for C++.

To use the sample, first publish a library to the repository using the `simple-library` build:

```
> cd binary-dependencies
> ./gradlew -p ../simple-library publish

BUILD SUCCESSFUL in 0s
```

You can find the repository in the `cpp/repo` directory.

Next, build the application that uses the library from this repository.

```
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/install/main/debug/app
The sum of 40 and 2 is 42!
```

## Application with prebuilt library dependencies (prebuilt-binaries)

This build shows how to use pre-built binaries that are already available on the local machine. Currently, Gradle does not offer a convenient way to do this but it is possible to configure Gradle to use these binaries.

To use the sample, first create the binaries using the `simple-library` build:

```
> cd prebuilt-binaries
> ./gradlew -p ../simple-library assemble

BUILD SUCCESSFUL in 0s
```

Next, build and run the application that uses these binaries:

```
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/install/main/debug/app
The sum of 40 and 2 is 42!
```

## Application with Swift package manager conventions (swift-package-manager)

This build shows how to configure Gradle to use the typical layout for a Swift Package Manager package.
It contains an application and a single library. The source files for the application and libraries are all under a single `Sources` directory.

This sample also includes a Swift Package Manager build file, so the same source can be built using Swift Package Manager

```
> cd swift-package-manager
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/app/install/main/debug/app
Hello, World!
```

## Source generation (source-generation) 

This build demonstrate using a task to generate source code before building a Swift or C++ application.

```
> cd source-generation
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
```

Generated sources will be under `build/generated`.

## Application with source library dependencies (source-dependencies)

This build demonstrates using external source dependencies to build Swift and C++ applications that require two external libraries.

### Swift
```
> cd swift/source-dependencies
> ./gradlew -p ../.. generateSwiftRepos
> ./gradlew assemble

BUILD SUCCESSFUL in 3s

> ./build/exe/main/debug/App
Hello, World!
```

### C++
```
> cd cpp/source-dependencies
> ./gradlew -p ../.. generateCppRepos
> ./gradlew assemble

BUILD SUCCESSFUL in 3s

> ./build/exe/main/debug/app
Hello, World!
```

## Application with static library dependencies (static-library)

This build demonstrates building and using static libraries.

```
> cd static-library
> ./gradlew assemble

> ./build/exe/main/debug/app
Hello, World!
```

## Swift application with C++ library dependencies (cpp-dependencies)

This build demonstrates using a C++ library from Swift.

```
> cd swift/cpp-dependencies
> ./gradlew assemble

> ./build/exe/main/debug/app
Hello, World!
```

There are three projects in this sample: a shared C++ library (`:list`), a static C++ library (`:listStatic`), and a swift application with a dependency on the shared C++ library (`:app`).

By default, when building the application, only the shared library will be built and used.  To change the application to use the static library instead, change the dependency in [app/build.gradle](swift/cpp-dependencies/app/build.gradle#L9) to depend on the `:listStatic` project instead of `:list`.  You'll also need to change [app/src/main/swift/LinkedList.swift](swift/cpp-dependencies/app/src/main/swift/LinkedList.swift#L4) to import the "listStatic" module (rather than the "list" module).
