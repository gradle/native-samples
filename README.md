
# Swift and C++ Sample Projects

This repository holds several sample Gradle builds which demonstrate how to
use Gradle to build Swift/C++ libraries and applications.

Each sample build is listed below with a bit of information related to the
features of Gradle that are demonstrated in that build. Each sample is functionally
the same for both Swift and C++ languages.

Each C++ sample works on macOS, Linux and Windows with GCC, Clang and Visual C++.

Each Swift sample works on macOS and Linux, with Swift 3 and later.

### Contributing to these samples

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

### Google Test support

The C++ sample `simple-library` demonstrates some basic Google test support in Gradle. This is currently not as refined as the XCTest support. 

To run the tests from the command line:

```
> cd <sample-dir>
> ./gradlew test
```

### Debug and release variants

The Swift/C++ plugins add a 'debug' and 'release' variant for each library or application. By default, the `assemble` task will build the debug variant only.

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

### C++

To build the library:

```
> cd cpp/simple-library
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> find build/lib/main/debug
build/lib/main/debug/liblist.dylib
```

To run the unit tests for the library:

```
> ./gradlew test

BUILD SUCCESSFUL in 1s
```

### Swift

To build the library:

```
> cd swift/simple-library
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> find build/lib/main/debug
build/lib/main/debug/libList.dylib
```

To run the unit tests for the library:

```
> ./gradlew test
> open build/reports/tests/xcTest/index.html
```

## Simple application (application)

This build shows how a Swift or C++ application can be built with Gradle. The application has no dependencies.

### C++

To build and run the application:

```
> cd cpp/application
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/app
Hello, World!
```

### Swift

To build and run the application:

```
> cd swift/application
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/App
Hello, World!
```

## Application with library dependencies in a single build (transitive-dependencies)

This builds shows how an application and several Swift or C++ libraries can be built with Gradle and linked together. The
dependencies are added using project dependencies between libraries.

In this sample, the application and libraries all use the same implementation language.

### C++

To build and run the application:

```
> cd cpp/transitive-dependencies
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./app/build/install/main/debug/app
Hello, World!
```

The build script also demonstrates how to configure convenience tasks like `assembleDebuggable`, which will assemble all "debuggable" binaries.

```
> ./gradlew assembleDebuggable

BUILD SUCCESSFUL in 1s
```

### Swift

To build and run the application:

```
> cd swift/transitive-dependencies
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./app/build/install/main/debug/App
Hello, World!
```

## Application with library dependencies in a composite build (composite-build)

This build shows that several otherwise independent Swift or C++ libraries can be built together with Gradle. The
dependencies are added transitively from the dependencies between modules
and the builds taking part in the composite build.

### C++

To build and run the application:

```
> cd cpp/composite-build
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/app
Hello, World!
```

### Swift

To build and run the application:

```
> cd swift/composite-build
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/App
Hello, World!
```

## Application with prebuilt library dependencies in a Maven repository (binary-dependencies)

This build shows how to publish C++ libraries to a Maven repository and use them from another build. This is currently only supported for C++.

### C++

To use the sample, first publish a library to the repository using the `simple-library` build:

```
> cd cpp/binary-dependencies
> ./gradlew -p ../simple-library publish

BUILD SUCCESSFUL in 1s
```

You can find the repository in the `cpp/repo` directory.

Next, build the application that uses the library from this repository.

```
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/app
Hello, World!
```

## Application with prebuilt library dependencies (prebuilt-binaries)

This build shows how to use pre-built binaries that are already available on the local machine. Currently, Gradle does not offer a convenient way to do this but it is possible to configure Gradle to use these binaries.

### C++

To use the sample, first create the binaries using the `simple-library` build:

```
> cd cpp/prebuilt-binaries
> ./gradlew -p ../simple-library assembleDebug assembleRelease

BUILD SUCCESSFUL in 1s
```

Next, build and run the application that uses these binaries:

```
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/app
Hello, World!
```

### Swift

To use the sample, first create the binaries using the `simple-library` build:

```
> cd swift/prebuilt-binaries
> ./gradlew -p ../simple-library assembleDebug assembleRelease

BUILD SUCCESSFUL in 1s
```

Next, build and run the application that uses these binaries:

```
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/install/main/debug/App
Hello, World!
```

## Application with Swift package manager conventions (swift-package-manager)

This build shows how to configure Gradle to use the typical layout for a Swift Package Manager package.
It contains an application and a single library. The source files for the application and libraries are all under a single `Sources` directory.

This sample also includes a Swift Package Manager build file, so the same source can be built using Swift Package Manager

### C++

```
> cd cpp/swift-package-manager
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/app/install/main/debug/app
Hello, World!
```

### Swift

```
> cd swift/swift-package-manager
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/app/install/main/debug/App
Hello, World!
```

## Source generation (source-generation) 

This build demonstrates using a task to generate source code before building a Swift or C++ application.

### C++

```
> cd cpp/source-generation
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/app
Hello, World!
```

Generated sources will be under `build/generated`.

### Swift

```
> cd swift/source-generation
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/App
Hello, World!
```

Generated sources will be under `build/generated`.

## Application with source library dependencies (source-dependencies)

This build demonstrates using external source dependencies to build Swift and C++ applications that require two libraries. The source for the libraries are hosted in separate Git repositories and declared as 'source dependencies' of the application. When Gradle builds the application, it first checks out a revision of the library source and uses this to build the binaries for the library.

### Swift

To use this sample, first create the Git repositories for the libraries:

```
> cd swift/source-dependencies
> ./gradlew -p ../.. generateRepos
```

The repositories are created in the `repos` directory. Each repository is set up to contain some source files and includes several commits and tags.

Next, build and run the sample:

```
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/App
Hello, World!
```

The build is configured to use the most recent revision from the 'utilities-library' Git repository. To see this in action, you can edit a library source file and commit the change:

```
> cd repos/utilities-library
> edit src/main/swift/Util.swift # add to split() function: print("split: " + s)
> git commit -a -m 'added some logging'
> cd ../..
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/App
split:   Hello,      World!
Hello, World!
```

You can also depend on specific versions of the libraries. For example, version 1.0 of the utilities library contains a bug.

```
> edit build.gradle # change dependency on utilities:latest.integration to utilities:1.0
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/App
Hello, Hello,
```

Change to version 1.1 to use a version with a fix for the bug. Dynamic dependencies are also supported, so you could also use `1.+`, `[1.1,2.0]` or `latest.integration`. Gradle matches the tags of the Git repository. Branches are also supported, but use a different syntax. See the following sample.

### C++

```
> cd cpp/source-dependencies
> ./gradlew -p ../.. generateRepos
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/app
Hello, World!
```

Try the same experiments as for the Swift sample above.

## Application with dependency on upstream branch (dependency-on-upstream-branch)

This sample shows how a source dependency on a particular branch can be used.

### Swift

To use this sample, create the Git repositories containing the libraries:

```
> cd swift/dependency-on-upstream-branch/app
> ./gradlew -p ../../.. generateRepos
```

Now you can build the application:

```
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/App
Hello, World!
```

The build is configured to use the most recent changes from the 'release' branch of the utilities library. Try changing the source of the library:

```
> cd ../utilities-library
> git checkout release
> edit src/main/swift/Util.swift # add to split() function: print("split: " + s)
> git commit -a -m 'added some logging'
> cd ../app
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/App
split:   Hello,      World!
Hello, World!
```

### C++

To use this sample, create the Git repositories containing the libraries:

```
> cd cpp/dependency-on-upstream-branch/app
> ./gradlew -p ../../.. generateRepos
```

```
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/app
Hello, World!
```

Try the same experiments as for the Swift sample above.

## Application with static library dependencies (static-library)

This build demonstrates building and using static libraries.

### C++

```
> cd cpp/static-library
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./app/build/install/main/debug/app
Hello, World!
```

### Swift

```
> cd swift/static-library
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./app/build/install/main/debug/App
Hello, World!
```

## Application with operating system specific library dependencies (operating-system-specific-dependencies)

This build demonstrates an application that has dependencies on different libraries for each operating system. Currently there are no conveniences for use libraries that are installed on the build machine.

### Swift

```
> cd swift/operating-system-specific-dependencies
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> app/build/install/main/debug/App
Hello, World!
```

The application selects the 'MacOsConsole' library that prints the output in blue when building on macOS.  On Linux, it selects the 'LinuxConsole' library that prints the output in green.

### C++

```
> cd cpp/operating-system-specific-dependencies
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./app/build/install/main/debug/app
Hello, World!
```

The application selects the 'ansiConsole' library on macOS and Linux and the 'winConsole' library when built on Windows. The output is blue on macOS and green on Linux and Windows.

## Swift application with C++ library dependencies (cpp-dependencies)

This build demonstrates using a C++ library from Swift.

### Swift

```
> cd swift/cpp-dependencies
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./app/build/install/main/debug/app
Hello, World!
```

There are three projects in this sample: a shared C++ library (`:list`), a static C++ library (`:listStatic`), and a swift application with a dependency on the shared C++ library (`:app`).

By default, when building the application, only the shared library will be built and used.  To change the application to use the static library instead, change the dependency in [app/build.gradle](swift/cpp-dependencies/app/build.gradle#L9) to depend on the `:listStatic` project instead of `:list`.  You'll also need to change [app/src/main/swift/LinkedList.swift](swift/cpp-dependencies/app/src/main/swift/LinkedList.swift#L4) to import the "listStatic" module (rather than the "list" module).

## Supporting multiple Swift versions (swift-versions)

This build demonstrates using multiple versions of Swift in a single build. There are two projects that build identical applications. One is written in Swift 3 compatible code (`swift3-app`) and one is written with Swift 4 compatible code (`swift4-app`). When running the application, it will print a message about which version of Swift was used.

### Swift 4

If you have the Swift 4 compiler installed, you can build both applications: 

```
# NOTE: Needs Swift 4 tool chain
> cd swift/swift-versions
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./swift4-app/build/install/main/debug/App
Built for Swift 4
Hello, World!
> ./swift3-app/build/install/main/debug/App
Built for Swift 3
Hello, World!
```

By default, the tests for a given Swift production component will be compiled for the same version of Swift. For instance, in `swift3-app`, the production and test code will be built with Swift 3 source compatibility.

### Swift 3

If you have the Swift 3 compiler installed, you can only build the Swift 3 application. Attempting to build the Swift 4 application will fail.

```
> cd swift/swift-versions
> ./gradlew swift3-app:assemble

BUILD SUCCESSFUL in 1s

> ./swift3-app/build/install/main/debug/app
Built for Swift 3
Hello, World!
```

Currently, Gradle does not offer a convenience to ignore projects that are not buildable due to missing or incompatible tool chains.

## Using non-Gradle source dependency builds (injected-plugins)

Gradle can also consume source dependencies that come from repositories without Gradle builds. When declaring a source dependency's repository information, you can instruct Gradle to inject plugins into the source dependency. These plugins can configure a Gradle build based on the contents of the repository.

### Swift

To use the sample, create the Git repositories containing the libraries:

```
> cd swift/injected-plugins
> ./gradlew -p ../.. generateRepos
```

Now build the application:

```
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/App
Hello, from Gradle build
Hello, World!
```

In the "repos" directory, you can find the source code without any Gradle configuration. The `utilities` and `list` builds are configured with the `utilities-build` and `list-build` plugins.

## Using Gradle builds from Swift Package Manager (swift-package-manager-publish)

This sample shows how libraries built with Gradle can be used by projects that are built with Swift Package Manager, without having to maintain separate Gradle and Swift PM build for the library.

The sample is made up of an application built using Swift PM, and two libraries that are built using Gradle. The sample also includes a 'release' plugin that takes care of generating a Swift PM build from a Gradle build.

### Swift

To use the sample, setup the Git repositories for the libraries:

```
> ./gradlew generateRepos
```

Next, create a release of the list library that can be used by Swift PM. This generates a `Package.swift` file to be used by Swift PM, commits the changes and creates a tag:

```
> cd swift/swift-package-manager-publish/list-library
> ./gradlew build release

BUILD SUCCESSFUL in 1s

```

Do the same for the utilities library: 

```
> cd ../utilities-library
> ./gradlew build release

BUILD SUCCESSFUL in 1s

```

Now build the application using Swift PM:

```
> cd ../app
> swift build
```

### C++

```
> ./gradlew generateRepos
> cd cpp/swift-package-manager-publish/list-library
> ./gradlew build release

BUILD SUCCESSFUL in 1s

> cd ../utilities-library
> ./gradlew build release

BUILD SUCCESSFUL in 1s

> cd ../app
> swift build
```

## Using a module for a system library with Swift (system-library-as-module)

Existing system libraries can be wrapped in user defined `module.modulemap` files.

### Swift

This sample demonstrates a Swift application that uses libcurl to fetch `example.com`.

```
> cd swift/system-library-as-module
> ./gradlew assemble

BUILD SUCCESSFUL in 1s

> ./build/install/main/debug/App
```

## Incremental Swift compilation

The `swiftc` has a built-in incremental compilation feature that tries to reduce the number of `.swift` files that need to be recompiled on each build by analyzing the dependencies between all files.

Gradle enables Swift incremental compilation by default, so no extra configuration is required to take advantage of this feature with your Swift projects.

## Consuming a legacy library built by CMake (cmake-library)

This sample demonstrates integrating a legacy library that is built by CMake into a Gradle build.  There are two projects: 'app' which is a Gradle-built executable that depends on 'library' which is built using CMake.  The 'library' project has a Gradle build that wraps the CMake build and exposes its artifacts in a way that other Gradle builds can consume.

### C++

```
> cd cpp/cmake-library
> ./gradlew :app:assemble

BUILD SUCCESSFUL in 1s

> ./app/build/install/main/debug/app
Hello, World!
```