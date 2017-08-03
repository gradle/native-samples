# Swift and C++ Sample Projects

This repository holds several sample Gradle Projects which demonstrate how to
use Gradle to build Swift/C++ libraries and executables including dependencies on
native libraries written in other native languages like C, C++, and Objective-C.

Each sample project is listed below with a bit of information related to the
features of Gradle that are demonstrated in that project. Each samples are functionally
the same for both Swift and C++ language.

## Simple Library (simple-library)

This project just shows that Swift modules can be built with Gradle. There
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

This project shows how a Swift/C++ Executable can be built including
demonstrating how another module can be linked in.

```
> cd executable
> ./gradlew assemble

BUILD SUCCESSFUL in 0s
2 actionable tasks: 2 executed
> ./build/install/app/app
Hello, World!
```

## Executable Multi-Project with Transitive Dependencies (transitive-dependencies)

This project just shows that Swift/C++ libraries can be built with Gradle. The
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

This project just shows that Swift/C++ libraries can be built with Gradle. The
dependencies are added transitively from the dependencies between modules
and the projects take part of a composite build. To run it:
```
> cd composite-build
> ./gradlew assemble

BUILD SUCCESSFUL in 0s

> ./build/install/app/app
Hello, World!
Hello, World!
```
