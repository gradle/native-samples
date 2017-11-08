This directory contains utilities for managing and testing the samples. It does not contain any samples.

When creating a sample, make sure you work within these constraints:
* Use a single folder. All files related to the specific samples should be located in the same folder.
* Be explicit. A sample can depend on the existence of another one, and it's current built status. These states are specified as annotation in the `settings.gradle` file in the form of specially formatted comments.
  * `// dependsOn {sample-name} {comma-separated-task-list}`: Ensure the specified tasks are up-to-date on the specified sample.
  * `// copy {sample-name}`: Ensure the specified sample is copied over in during testing.
  * `// ignored`: Ignore this sample during testing, useful when developing a sample.
* Symmetry. Strive to keep symmetry between ecosystem by creating the equivalent sample for all ecosystem.
* Test. Before opening a pull request, execute `./gradlew test`.
