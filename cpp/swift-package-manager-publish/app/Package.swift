// swift-tools-version:4.0

import PackageDescription

let package = Package(
    name: "App",
    dependencies: [
        .package(url: "../utilities-library", from: "1.0.0"),
        .package(url: "../list-library", from: "1.0.0")
    ],
    targets: [
        .target(
            name: "App",
            dependencies: [
                .product(name: "utilities"),
                .product(name: "list")
            ]
        ),
    ]
)
