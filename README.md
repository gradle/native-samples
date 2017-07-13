# Swift Sample Projects

This repository holds several sample Gradle Projects which demonstrate how to
use Gradle to build Swift modules and executables including dependencies on
native libraries written in other native languages like C, C++, and Objective-C.

Each sample project is listed below with a bit of information related to the
features of Gradle that are demonstrated in that project.

## Simple Module (simple-module)

This project just shows that Swift modules can be built with Gradle. There
are no dependencies, just the module itself. To run it:
```
> cd simple-module
> ./gradlew assemble
Parallel execution is an incubating feature.

BUILD SUCCESSFUL in 0s
2 actionable tasks: 2 executed

> find build
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

## Executable Multi-Project (executable)

This project shows how a multi-project Swift Executable can be built including
demonstrating how another module can be linked in.

```
> cd executable
> ./gradlew assemble
Parallel execution is an incubating feature.

BUILD SUCCESSFUL in 1s
5 actionable tasks: 5 executed
> ./app/build/install/app/app
Hello, World!
12
```
