#include <iostream>
#include <string>

extern std::string reverse(const std::string & source);

static void print_help() {
    std::cout << "usage: app \"string to reverse\"" << std::endl;
}

static std::string get_message() {
    return "Hello, World!";
}

int main(int argc, const char * argv[]) {
    if (argc == 2) {
        std::string string_to_reverse = argv[1];
        std::cout << get_message() << std::endl;
        std::cout << "Here is the reverse string:" << std::endl;
        std::cout << reverse(string_to_reverse) << std::endl;
        return 0;
    } else {
        if (argc > 2) {
            std::cout << "error: too many arguments" << std::endl;
        }
        print_help();
        return -1;
    }
}
