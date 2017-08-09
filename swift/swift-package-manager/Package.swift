import PackageDescription

let package = Package(
    name: "App",
    targets: [
        Target(name: "Greeter"),
        Target(name: "App", dependencies: ["Greeter"])
    ]
)