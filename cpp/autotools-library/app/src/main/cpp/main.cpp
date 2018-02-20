/*
 * A simple hello world application. Uses a library to tokenize and join a string and prints the result.
 */
#include <iostream>
#include <stdlib.h>
#include <cstring>
#include <curl/curl.h>

#include "string_utils.h"
#include "linked_list.h"

struct MemoryStruct {
    char *memory;
    size_t size;
};

static size_t WriteMemoryCallback(void *contents, size_t size, size_t nmemb, void *userp) {
    size_t realsize = size * nmemb;
    struct MemoryStruct *mem = (struct MemoryStruct *)userp;

    mem->memory = (char *)realloc(mem->memory, mem->size + realsize + 1);
    if(mem->memory == NULL) {
        std::cerr << "not enough memory (realloc returned NULL)" << std::endl;
        return 0;
    }

    std::memcpy(&(mem->memory[mem->size]), contents, realsize);
    mem->size += realsize;
    mem->memory[mem->size] = 0;

    return realsize;
}

std::string getCapital(char *s) {
    std::string content = std::string(s);
    std::string colon = ":";
    std::string quote = "\"";
    content.erase(0, content.find(colon) + 2);
    content.erase(content.find(quote), 3);
    return content;
}

int main() {
    CURL *curl_handle;
    CURLcode res;
    struct MemoryStruct response;

    linked_list tokens;
    tokens = split("Hello,      World!");
    std::cout << join(tokens) << std::endl;

    response.memory = (char *)malloc(1);
    response.size = 0;

    curl_global_init(CURL_GLOBAL_ALL);
    curl_handle = curl_easy_init();
    curl_easy_setopt(curl_handle, CURLOPT_URL, "http://restcountries.eu/rest/v2/name/sweden?fields=capital");
    curl_easy_setopt(curl_handle, CURLOPT_VERBOSE, 0L);
    curl_easy_setopt(curl_handle, CURLOPT_NOPROGRESS, 1L);
    curl_easy_setopt(curl_handle, CURLOPT_WRITEFUNCTION, WriteMemoryCallback);
    curl_easy_setopt(curl_handle, CURLOPT_WRITEDATA, (void *)&response);

    res = curl_easy_perform(curl_handle);

    if(res != CURLE_OK) {
        std::cerr << "curl failed: " << curl_easy_strerror(res) << std::endl;
    } else {
        std::cout << "The capital of Sweden is " << getCapital(response.memory) << std::endl;
    }

    curl_easy_cleanup(curl_handle);
    free(response.memory);
    curl_global_cleanup();

    return 0;
}
