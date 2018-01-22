/*
 * A logger that writes to the console.
 */
#if os(Linux)
    import LinuxConsole
#else
    import MacOsConsole
#endif

class Logger {
    let console = Console()

    // Writes an info message and trailing new-line
    func info(_ s: String) {
        console.color(s)
    }
}
