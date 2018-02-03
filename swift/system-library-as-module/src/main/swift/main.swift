import CCurl

let handle = curl_easy_init()

curlHelperSetOptString(handle, CURLOPT_URL, "http://www.example.com")
curlHelperSetOptBool(handle, CURLOPT_VERBOSE, CURL_TRUE)

let ret = curl_easy_perform(handle)
let error = curl_easy_strerror(ret)

print("error = \(error)")
print("ret = \(ret)")
