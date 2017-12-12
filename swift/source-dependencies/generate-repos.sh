#!/bin/bash

function createRepo() {
    declare src=$1
    declare dest=$1-repo
    rm -rf $dest
    mkdir $dest
    echo "Creating $src repository in $dest"
    cd $dest
    cp -r ../$src/* .
    git init
    git add .
    git commit -m 'Initial commit'
    git tag '1.0'
    git --no-pager log -1 -p 
    cd ..
}

function addTwoOh() {
    cd $1-repo
    git add .
    git commit -m 'Add 2.0'
    git tag '2.0'
    git --no-pager log -1 -p 
    cd ..
}

cd repos

# Populate Hello library's repo
createRepo "hello"
cat << EOF > hello-repo/src/main/swift/hello.swift
import Greeting

public func sayHello() {
    print(getHello())
    print(getGoodbye())
}
EOF
cat << EOF > hello-repo/build.gradle
plugins {
    id 'swift-library'
    id 'xcode'
}

group = 'org.gradle.swift-samples'

dependencies {
    api 'org.gradle.swift-samples:Greeting:2.0'
}
EOF
addTwoOh "hello"

# Populate Greeting library's repo
createRepo "greeting"
cat << EOF > greeting-repo/src/main/swift/greetings.swift
public func getHello() -> String {
    return "Hello, World!"
}
public func getGoodbye() -> String {
    return "Goodbye, World!"
}
EOF
addTwoOh "greeting"
