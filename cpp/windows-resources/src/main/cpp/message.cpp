#include "windows.h"

#include "message.h"
#include "resources.h"

static HINSTANCE moduleInstance = NULL;

static HINSTANCE get_module_instance() {
    if (moduleInstance == NULL) {
        return GetModuleHandle(NULL);
    }
    return moduleInstance;
}

static std::string LoadStringFromResource(UINT stringId) {
    HINSTANCE instance = get_module_instance();
    WCHAR * buffer = NULL;
    int length = LoadStringW(instance, stringId, reinterpret_cast<LPWSTR>(&buffer), 0);
    std::wstring wide = std::wstring(buffer, length);
    return std::string(wide.begin(), wide.end());
}

std::string get_message() {
    return LoadStringFromResource(IDS_MESSAGE);
}

#ifdef _DLL
BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD dwReason, LPVOID lpvReserved) {
    moduleInstance = hinstDLL;
    return TRUE;
}
#endif