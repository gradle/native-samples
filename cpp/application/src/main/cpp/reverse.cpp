#include <string>

std::string reverse(const std::string & source) {
    return std::string(source.crbegin(), source.crend());
}
