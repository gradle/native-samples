import PackageDescription

let package = Package(
    name: "App",
    targets: [
        Target(name: "List"),
        Target(name: "Utilities", dependencies: ["List"]),
        Target(name: "App", dependencies: ["Utilities"])
    ]
)
