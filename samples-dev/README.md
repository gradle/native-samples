This directory contains utilities for managing and testing the samples. It does not contain any samples.

The source for most samples is generated from templates in this directory:

Template                 | Description
-------------------------|------------
`cpp-app`                | C++ application, uses the list and utilities libraries.
`cpp-lib-with-api-dep`   | C++ implementation of the utilities library, uses the list library.
`cpp-lib`                | C++ implementation of the list library. No dependencies
`cpp-lib-with-c-api`     | C++ implementation of the list library with a C API.
`swift-app`              | Swift application, uses the list and utilities libraries.
`swift-lib-with-api-dep` | Swift implementation of the utilities library, uses the list library.
`swift-lib`              | Swift implementation of the list library. No dependencies
`swift-lib-uses-c-api`   | Swift implementation of the list library that wraps the C API implementation.

The tasks in this project take care of copying the relevant source files into each sample, transforming them as required. Some sample projects may merge several of these templates into the same project.

* `import <lib-name>` statements are removed from Swift source files when the target library is merged into the same project.
* `@testable import <lib-name>` statements are replaced in Swift library test source files when the library is merged into some other project.
* Public C++ header files are assumed to use a `<lib-name>_API` macro to export public symbols. A definition of this is inserted into public header files for those projects that may produce a DLL.

When creating a sample, make sure you work within these constraints:

* Use a single folder. All files related to the specific samples should be located in the same folder.
* Reuse the existing template, where possible.
* Symmetry. Strive to keep symmetry between Swift and C++ by creating the equivalent sample for both ecosystems.
* Test. Before opening a pull request, execute `./gradlew test`.
