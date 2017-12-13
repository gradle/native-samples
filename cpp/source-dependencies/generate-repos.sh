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
cat << EOF > hello-repo/src/main/cpp/hello.cpp
#include "greeting.h"

#include "logger.h"

void sayHello() {
    log(getHello());
    log(getGoodbye());
}

EOF
cat << EOF > hello-repo/build.gradle
plugins {
    id 'cpp-library'
}

group = 'org.gradle.cpp-samples'

dependencies {
    implementation 'org.gradle.cpp-samples:greeting:2.0'
}
EOF
addTwoOh "hello"

# Populate Greeting library's repo
createRepo "greeting"
cat << EOF > greeting-repo/src/main/cpp/greetings.cpp
#include "greeting.h"

std::string getHello() {
    return "Hello, World!";
}
std::string getGoodbye() {
    return "Goodbye, World!";
}
EOF
cat << EOF > greeting-repo/src/main/public/greeting.h
#pragma once

#include <string>

std::string getHello();
std::string getGoodbye();
EOF

addTwoOh "greeting"
