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

# Populate util library's repo
createRepo "utilities"

cat << EOF > utilities-repo/build.gradle
plugins {
    id 'cpp-library'
}

group = 'org.gradle.cpp-samples'

dependencies {
    api 'org.gradle.cpp-samples:list:2.0'
}
EOF
addTwoOh "utilities"

# Populate list library's repo
createRepo "list"

addTwoOh "list"
