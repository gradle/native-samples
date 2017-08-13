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

## Simple Library (simple-library)

This build just shows that Swift modules can be built with Gradle. There
are no dependencies, just the module itself. To run it:
```
> cd simple-module
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
2 actionable tasks: 2 executed

> find build  # C++ project will differ a bit
build
build/lib
build/lib/libhello.dylib
build/main
build/main/objs
build/main/objs/2oepwq5hb4xzy326h8tk3lnu
build/main/objs/2oepwq5hb4xzy326h8tk3lnu/hello.o
build/main/objs/2oepwq5hb4xzy326h8tk3lnu/hello~partial.swiftdoc
build/main/objs/2oepwq5hb4xzy326h8tk3lnu/hello~partial.swiftmodule
build/main/objs/7nvd7tof895oopgq6sztmh5rf
build/main/objs/7nvd7tof895oopgq6sztmh5rf/sum.o
build/main/objs/7nvd7tof895oopgq6sztmh5rf/sum~partial.swiftdoc
build/main/objs/7nvd7tof895oopgq6sztmh5rf/sum~partial.swiftmodule
build/main/objs/hello.swiftdoc
build/main/objs/hello.swiftmodule
build/main/objs/output-file-map.json
build/tmp
build/tmp/compileSwift
build/tmp/compileSwift/output.txt
build/tmp/linkMain
build/tmp/linkMain/output.txt
```

## Executable (executable)

This build shows how a Swift/C++ executable can be built with Gradle.

```
> cd executable
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
2 actionable tasks: 2 executed

> ./build/install/app/app
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

> ./app/build/install/app/app
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

> ./build/install/app/app
Hello, World!
Hello, World!
```

## Swift package manager conventions (swift-package-manager)

This build shows how to configure Gradle to use the typical layout for a Swift Package Manager package.
It contains an executable and a single library. The source files for the executable and libraries are all under a single `Sources` directory. 

This sample also includes a Swift Package Manager build file, so the same source can be built using Swift Package Manager

```
> cd swift-package-manager
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/app/install/app/app
Hello, World!
```
