import PackageDescription

let package = Package(
    name: "App",
    targets: [
        Target(name: "Greeter"),
        Target(name: "App", dependencies: ["Greeter"]),
        Target(name: "AppTests"),
        Target(name: "GreeterTests")
    ]
)