# Contributing to Gradle
Thank you for considering a contribution to Gradle! This guide explains how to:

* maximize the chance of your changes being accepted
* work on the native samples
* get help if you encounter trouble

## Get in touch

Before starting to work on a large feature or a fix, please open an issue to discuss the use case or bug with us. This can save both you and us a lot of time.  

## Accept Developer Certificate of Origin

In order for your contributions to be accepted, you must [sign off](https://git-scm.com/docs/git-commit#git-commit---signoff) your Git commits to indicate that you agree to the terms of [Developer Certificate of Origin](https://developercertificate.org/).

## Follow the Code of Conduct

In order to foster a more inclusive community, Gradle has adopted the [Contributor Covenant](https://www.contributor-covenant.org/version/1/4/code-of-conduct/).

Contributors must follow the Code of Conduct outlined at [https://gradle.org/conduct/](https://gradle.org/conduct/).

## Making Changes

### Development Setup

In order to make changes to our samples, you'll need:

* A text editor or IDE. 
* A [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (JDK) version 1.7 or higher
* Native tool chains: Clang, GCC or Visual Studio, depending on your OS and Swift on macOS and Linux.
* [git](https://git-scm.com/) and a [GitHub account](https://github.com/join)

Gradle uses pull requests for contributions. Fork [gradle/native-samples](https://github.com/gradle/native-samples) and clone your fork. Configure your Git username and email with

    git config user.name 'First Last'
    git config user.email user@example.com

### Development Workflow

After making changes, you need to test them:

To run tests, execute `./gradlew check`. This verifies that your changes pass some of our automated checks. We require samples to have an entry in the `README.md` and work when running `gradle help`.

### Creating Commits And Writing Commit Messages

The commit messages that accompany your code changes are an important piece of documentation, please follow these guidelines when writing commit messages:

* Keep commits discrete: avoid including multiple unrelated changes in a single commit
* Keep commits self-contained: avoid spreading a single change across multiple commits. A single commit should make sense in isolation
* If your commit pertains to a GitHub issue, include (`Issue: #123`) in the commit message on a separate line
* [Sign off](https://git-scm.com/docs/git-commit#git-commit---signoff) your commits to indicate that you agree to the terms of [Developer Certificate of Origin](https://developercertificate.org/).

### Submitting Your Change

After you submit your pull request, a Gradle core developer will review it. It is normal that this takes several iterations, so don't get discouraged by change requests. They ensure the high quality that we all enjoy.

### Signing Off Commits After Submitting a Pull Request

Pull requests are automatically verified that all commit messages contain the Signed-off-by line with an email address that matches the commit author. In case you didn't sign off your commits before creating a pull request, you can still fix that to confirm that you agree to the terms of [Developer Certificate of Origin](https://developercertificate.org/).

To sign off a single commit:

`git commit --amend --signoff`

To sign off one or multiple commits:

`git filter-branch --msg-filter "cat - && echo && echo 'Signed-off-by: Your Name <Your.Name@example.com>'" HEAD`

Then force push your branch:

`git push --force origin test-branch`

## Getting Help

If you run into any trouble, please reach out to us on the issue you are working on.

## Our Thanks

We deeply appreciate your effort toward improving Gradle. For any contribution, large or small, you will be immortalized in the release notes for the version you've contributed to.

If you enjoyed this process, perhaps you should consider getting [paid to develop Gradle](https://gradle.com/careers)?
